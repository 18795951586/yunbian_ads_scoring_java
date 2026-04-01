package com.yunbian.adsscoring.scoring.service.impl;

import com.yunbian.adsscoring.bidword.dto.BidwordMetricsMatrixItem;
import com.yunbian.adsscoring.bidword.mapper.BidwordMetricsMatrixMapper;
import com.yunbian.adsscoring.scoring.dto.BidwordScoringResponse;
import com.yunbian.adsscoring.scoring.dto.BidwordWeightedRankingPreviewResponse;
import com.yunbian.adsscoring.scoring.enums.ScoringEntityLevel;
import com.yunbian.adsscoring.scoring.enums.ScoringMetricKey;
import com.yunbian.adsscoring.scoring.enums.ScoringRuleType;
import com.yunbian.adsscoring.scoring.request.ScoringLevelConfigRequest;
import com.yunbian.adsscoring.scoring.request.ScoringMetricConfigRequest;
import com.yunbian.adsscoring.scoring.request.ScoringSchemeCreateRequest;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class BidwordWeightedPreviewService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Resource
    private BidwordMetricsMatrixMapper bidwordMetricsMatrixMapper;

    public BidwordWeightedRankingPreviewResponse previewBidwordWeightedRanking(
            Long sid,
            LocalDate logDate,
            Integer effectDays,
            ScoringSchemeCreateRequest request
    ) {
        List<BidwordMetricsMatrixItem> sourceRows =
                bidwordMetricsMatrixMapper.selectAllMetricsMatrixBySidAndLogDate(sid, logDate);

        ScoringLevelConfigRequest bidwordLevelConfig = request.getLevelConfigs().stream()
                .filter(level -> ScoringEntityLevel.BIDWORD.getCode().equals(level.getEntityLevel()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("bidword level config is required"));

        List<WeightedMetricSpec> metricSpecs = bidwordLevelConfig.getMetricConfigs().stream()
                .filter(metric -> Boolean.TRUE.equals(metric.getEnabled()))
                .filter(metric -> metric.getWeight() != null && metric.getWeight().compareTo(ZERO) > 0)
                .map(this::buildWeightedMetricSpec)
                .filter(Objects::nonNull)
                .toList();

        List<BidwordWeightedRankingPreviewResponse.MetricSummary> metricSummaries = new ArrayList<>();
        Map<String, BidwordWeightedRowAccumulator> rowAccumulatorMap = new LinkedHashMap<>();

        for (WeightedMetricSpec metricSpec : metricSpecs) {
            List<BidwordMetricCandidate> candidates = buildBidwordMetricCandidates(sourceRows, metricSpec.getMetricKey(), effectDays);
            int comparisonCount = candidates.size();
            int excludedNullCount = sourceRows.size() - comparisonCount;
            boolean usedInAggregation = comparisonCount > 0 && metricSpec.getWeight().compareTo(ZERO) > 0;

            BidwordWeightedRankingPreviewResponse.MetricSummary metricSummary = new BidwordWeightedRankingPreviewResponse.MetricSummary();
            metricSummary.setMetricKey(metricSpec.getMetricKey().getCode());
            metricSummary.setMetricName(metricSpec.getMetricKey().getName());
            metricSummary.setRuleType(metricSpec.getRuleType().getCode());
            metricSummary.setWeight(metricSpec.getWeight());
            metricSummary.setComparisonCount(comparisonCount);
            metricSummary.setExcludedNullCount(excludedNullCount);
            metricSummary.setUsedInAggregation(usedInAggregation);
            metricSummaries.add(metricSummary);

            if (!usedInAggregation) {
                continue;
            }

            Map<String, BidwordMetricScoreSnapshot> scoreSnapshotByBidwordId =
                    buildBidwordMetricScoreSnapshotByBidwordId(metricSpec, candidates);

            for (BidwordMetricScoreSnapshot snapshot : scoreSnapshotByBidwordId.values()) {
                if (snapshot.getScore() == null) {
                    continue;
                }
                BigDecimal weightedScore = snapshot.getScore().multiply(metricSpec.getWeight());

                BidwordWeightedRowAccumulator accumulator = rowAccumulatorMap.computeIfAbsent(
                        buildBidwordIdentityKey(snapshot.getCampaignId(), snapshot.getAdgroupId(), snapshot.getBidwordId()),
                        key -> {
                            BidwordWeightedRowAccumulator value = new BidwordWeightedRowAccumulator();
                            value.setCampaignId(snapshot.getCampaignId());
                            value.setCampaignName(snapshot.getCampaignName());
                            value.setAdgroupId(snapshot.getAdgroupId());
                            value.setAdgroupName(snapshot.getAdgroupName());
                            value.setBidwordId(snapshot.getBidwordId());
                            value.setBidwordText(snapshot.getBidwordText());
                            value.setWeightedScoreSum(ZERO);
                            value.setParticipatingWeightSum(ZERO);
                            value.setMetricContributions(new ArrayList<>());
                            return value;
                        }
                );

                accumulator.setWeightedScoreSum(accumulator.getWeightedScoreSum().add(weightedScore));
                accumulator.setParticipatingWeightSum(accumulator.getParticipatingWeightSum().add(metricSpec.getWeight()));

                BidwordWeightedRankingPreviewResponse.MetricContribution contribution =
                        new BidwordWeightedRankingPreviewResponse.MetricContribution();
                contribution.setMetricKey(metricSpec.getMetricKey().getCode());
                contribution.setMetricName(metricSpec.getMetricKey().getName());
                contribution.setRuleType(metricSpec.getRuleType().getCode());
                contribution.setMetricValue(snapshot.getMetricValue());
                contribution.setRank(snapshot.getRank());
                contribution.setScore(snapshot.getScore());
                contribution.setWeight(metricSpec.getWeight());
                contribution.setWeightedScore(weightedScore.setScale(4, RoundingMode.HALF_UP));
                accumulator.getMetricContributions().add(contribution);
            }
        }

        List<BidwordWeightedRankingPreviewResponse.BidwordWeightedRankingRow> rows = rowAccumulatorMap.values().stream()
                .map(this::buildBidwordWeightedRankingRow)
                .sorted(
                        Comparator.comparing(
                                        BidwordWeightedRankingPreviewResponse.BidwordWeightedRankingRow::getTotalScore,
                                        Comparator.nullsLast(BigDecimal::compareTo)
                                )
                                .reversed()
                                .thenComparing(BidwordWeightedRankingPreviewResponse.BidwordWeightedRankingRow::getCampaignId,
                                        Comparator.nullsLast(Long::compareTo))
                                .thenComparing(BidwordWeightedRankingPreviewResponse.BidwordWeightedRankingRow::getAdgroupId,
                                        Comparator.nullsLast(Long::compareTo))
                                .thenComparing(BidwordWeightedRankingPreviewResponse.BidwordWeightedRankingRow::getBidwordId,
                                        Comparator.nullsLast(Long::compareTo))
                ).toList();

        int usedMetricCount = (int) metricSummaries.stream().filter(summary -> Boolean.TRUE.equals(summary.getUsedInAggregation())).count();
        int enabledRankingMetricCount = (int) metricSpecs.stream().filter(metric -> metric.getRuleType() == ScoringRuleType.RANKING).count();

        BidwordWeightedRankingPreviewResponse response = new BidwordWeightedRankingPreviewResponse();
        response.setSid(sid);
        response.setLogDate(logDate);
        response.setEntityLevel(ScoringEntityLevel.BIDWORD.getCode());
        response.setRuleType(ScoringRuleType.RANKING.getCode());
        response.setEffectDays(effectDays);
        response.setRawRowCount(sourceRows.size());
        response.setEnabledMetricCount(metricSpecs.size());
        response.setEnabledRankingMetricCount(enabledRankingMetricCount);
        response.setUsedMetricCount(usedMetricCount);
        response.setSkippedMetricCount(metricSpecs.size() - usedMetricCount);
        response.setMetricSummaries(metricSummaries);
        response.setRows(rows);
        return response;
    }

    
    public BidwordScoringResponse calculateBidwordScoring(
            Long sid,
            LocalDate logDate,
            Integer effectDays,
            ScoringSchemeCreateRequest request
    ) {
        return toBidwordScoringResponse(previewBidwordWeightedRanking(sid, logDate, effectDays, request));
    }

    private BidwordScoringResponse toBidwordScoringResponse(BidwordWeightedRankingPreviewResponse previewResponse) {
        BidwordScoringResponse response = new BidwordScoringResponse();
        response.setSid(previewResponse.getSid());
        response.setLogDate(previewResponse.getLogDate());
        response.setEntityLevel(previewResponse.getEntityLevel());
        response.setRuleType(previewResponse.getRuleType());
        response.setEffectDays(previewResponse.getEffectDays());
        response.setRawRowCount(previewResponse.getRawRowCount());
        response.setEnabledMetricCount(previewResponse.getEnabledMetricCount());
        response.setEnabledRankingMetricCount(previewResponse.getEnabledRankingMetricCount());
        response.setUsedMetricCount(previewResponse.getUsedMetricCount());
        response.setSkippedMetricCount(previewResponse.getSkippedMetricCount());
        response.setMetricSummaries(previewResponse.getMetricSummaries().stream()
                .map(this::toBidwordScoringMetricSummary)
                .toList());
        response.setRows(previewResponse.getRows().stream()
                .map(this::toBidwordScoringRow)
                .toList());
        return response;
    }

    private BidwordScoringResponse.MetricSummary toBidwordScoringMetricSummary(
            BidwordWeightedRankingPreviewResponse.MetricSummary previewMetricSummary
    ) {
        BidwordScoringResponse.MetricSummary metricSummary = new BidwordScoringResponse.MetricSummary();
        metricSummary.setMetricKey(previewMetricSummary.getMetricKey());
        metricSummary.setMetricName(previewMetricSummary.getMetricName());
        metricSummary.setRuleType(previewMetricSummary.getRuleType());
        metricSummary.setWeight(previewMetricSummary.getWeight());
        metricSummary.setComparisonCount(previewMetricSummary.getComparisonCount());
        metricSummary.setExcludedNullCount(previewMetricSummary.getExcludedNullCount());
        metricSummary.setUsedInAggregation(previewMetricSummary.getUsedInAggregation());
        return metricSummary;
    }

    private BidwordScoringResponse.BidwordScoringRow toBidwordScoringRow(
            BidwordWeightedRankingPreviewResponse.BidwordWeightedRankingRow previewRow
    ) {
        BidwordScoringResponse.BidwordScoringRow row = new BidwordScoringResponse.BidwordScoringRow();
        row.setCampaignId(previewRow.getCampaignId());
        row.setCampaignName(previewRow.getCampaignName());
        row.setAdgroupId(previewRow.getAdgroupId());
        row.setAdgroupName(previewRow.getAdgroupName());
        row.setBidwordId(previewRow.getBidwordId());
        row.setBidwordText(previewRow.getBidwordText());
        row.setTotalScore(previewRow.getTotalScore());
        row.setParticipatingMetricCount(previewRow.getParticipatingMetricCount());
        row.setParticipatingWeightSum(previewRow.getParticipatingWeightSum());
        row.setMetricContributions(previewRow.getMetricContributions().stream()
                .map(this::toBidwordScoringMetricContribution)
                .toList());
        return row;
    }

    private BidwordScoringResponse.MetricContribution toBidwordScoringMetricContribution(
            BidwordWeightedRankingPreviewResponse.MetricContribution previewContribution
    ) {
        BidwordScoringResponse.MetricContribution contribution = new BidwordScoringResponse.MetricContribution();
        contribution.setMetricKey(previewContribution.getMetricKey());
        contribution.setMetricName(previewContribution.getMetricName());
        contribution.setRuleType(previewContribution.getRuleType());
        contribution.setMetricValue(previewContribution.getMetricValue());
        contribution.setRank(previewContribution.getRank());
        contribution.setScore(previewContribution.getScore());
        contribution.setWeight(previewContribution.getWeight());
        contribution.setWeightedScore(previewContribution.getWeightedScore());
        return contribution;
    }



    private Map<String, BidwordMetricScoreSnapshot> buildBidwordMetricScoreSnapshotByBidwordId(WeightedMetricSpec metricSpec, List<BidwordMetricCandidate> candidates) {
        Map<String, BidwordMetricScoreSnapshot> snapshotById = new LinkedHashMap<>();
        if (candidates.isEmpty()) {
            return snapshotById;
        }

        if (metricSpec.getRuleType() == ScoringRuleType.RANKING) {
            Map<String, List<BidwordMetricCandidate>> groupedCandidates = groupByCampaignAndAdgroup(candidates);
            for (List<BidwordMetricCandidate> group : groupedCandidates.values()) {
                sortBidwordMetricCandidates(group, metricSpec.getMetricKey());
                int[] ranks = buildCompetitionRanks(group);
                for (int i = 0; i < group.size(); i++) {
                    BidwordMetricCandidate c = group.get(i);
                    BidwordMetricScoreSnapshot s = fromCandidate(c);
                    s.setRank(ranks[i]);
                    s.setScore(buildRankingScore(ranks[i], group.size()));
                    snapshotById.put(buildBidwordIdentityKey(c.getCampaignId(), c.getAdgroupId(), c.getBidwordId()), s);
                }
            }
            return snapshotById;
        }

        if (metricSpec.getRuleType() == ScoringRuleType.SMART_BENCHMARK) {
            Map<String, List<BidwordMetricCandidate>> groupedCandidates = groupByCampaignAndAdgroup(candidates);
            for (List<BidwordMetricCandidate> group : groupedCandidates.values()) {
                BigDecimal benchmark = metricSpec.getMetricKey().isHigherBetter()
                        ? group.stream().map(BidwordMetricCandidate::getMetricValue).max(BigDecimal::compareTo).orElse(null)
                        : group.stream().map(BidwordMetricCandidate::getMetricValue).min(BigDecimal::compareTo).orElse(null);
                for (BidwordMetricCandidate c : group) {
                    BidwordMetricScoreSnapshot s = fromCandidate(c);
                    s.setScore(buildSmartBenchmarkScore(c.getMetricValue(), benchmark, metricSpec.getMetricKey()));
                    snapshotById.put(buildBidwordIdentityKey(c.getCampaignId(), c.getAdgroupId(), c.getBidwordId()), s);
                }
            }
            return snapshotById;
        }

        for (BidwordMetricCandidate c : candidates) {
            BidwordMetricScoreSnapshot s = fromCandidate(c);
            s.setScore(buildTargetValueScore(c.getMetricValue(), metricSpec.getTargetValue(), metricSpec.getMetricKey()));
            snapshotById.put(buildBidwordIdentityKey(c.getCampaignId(), c.getAdgroupId(), c.getBidwordId()), s);
        }
        return snapshotById;
    }

    private BidwordMetricScoreSnapshot fromCandidate(BidwordMetricCandidate candidate) {
        BidwordMetricScoreSnapshot s = new BidwordMetricScoreSnapshot();
        s.setCampaignId(candidate.getCampaignId());
        s.setCampaignName(candidate.getCampaignName());
        s.setAdgroupId(candidate.getAdgroupId());
        s.setAdgroupName(candidate.getAdgroupName());
        s.setBidwordId(candidate.getBidwordId());
        s.setBidwordText(candidate.getBidwordText());
        s.setMetricValue(candidate.getMetricValue());
        return s;
    }

    private Map<String, List<BidwordMetricCandidate>> groupByCampaignAndAdgroup(List<BidwordMetricCandidate> candidates) {
        Map<String, List<BidwordMetricCandidate>> grouped = new LinkedHashMap<>();
        for (BidwordMetricCandidate candidate : candidates) {
            grouped.computeIfAbsent(buildCampaignAdgroupKey(candidate.getCampaignId(), candidate.getAdgroupId()), key -> new ArrayList<>())
                    .add(candidate);
        }
        return grouped;
    }

    private String buildCampaignAdgroupKey(Long campaignId, Long adgroupId) {
        return String.valueOf(campaignId) + "#" + String.valueOf(adgroupId);
    }

    private String buildBidwordIdentityKey(Long campaignId, Long adgroupId, Long bidwordId) {
        return String.valueOf(campaignId) + "#" + String.valueOf(adgroupId) + "#" + String.valueOf(bidwordId);
    }

    private BidwordWeightedRankingPreviewResponse.BidwordWeightedRankingRow buildBidwordWeightedRankingRow(BidwordWeightedRowAccumulator accumulator) {
        BidwordWeightedRankingPreviewResponse.BidwordWeightedRankingRow row = new BidwordWeightedRankingPreviewResponse.BidwordWeightedRankingRow();
        row.setCampaignId(accumulator.getCampaignId());
        row.setCampaignName(accumulator.getCampaignName());
        row.setAdgroupId(accumulator.getAdgroupId());
        row.setAdgroupName(accumulator.getAdgroupName());
        row.setBidwordId(accumulator.getBidwordId());
        row.setBidwordText(accumulator.getBidwordText());
        row.setParticipatingMetricCount(accumulator.getMetricContributions().size());
        row.setParticipatingWeightSum(accumulator.getParticipatingWeightSum().setScale(4, RoundingMode.HALF_UP));
        if (accumulator.getParticipatingWeightSum().compareTo(ZERO) > 0) {
            row.setTotalScore(accumulator.getWeightedScoreSum().divide(accumulator.getParticipatingWeightSum(), 4, RoundingMode.HALF_UP));
        }
        row.setMetricContributions(accumulator.getMetricContributions());
        return row;
    }

    private List<BidwordMetricCandidate> buildBidwordMetricCandidates(List<BidwordMetricsMatrixItem> sourceRows, ScoringMetricKey metricKey, Integer effectDays) {
        List<BidwordMetricCandidate> candidates = new ArrayList<>();
        for (BidwordMetricsMatrixItem item : sourceRows) {
            BigDecimal metricValue = extractMetricValue(item, metricKey, effectDays);
            if (metricValue == null) {
                continue;
            }
            BidwordMetricCandidate candidate = new BidwordMetricCandidate();
            candidate.setCampaignId(item.getCampaignId());
            candidate.setCampaignName(item.getCampaignName());
            candidate.setAdgroupId(item.getAdgroupId());
            candidate.setAdgroupName(item.getAdgroupName());
            candidate.setBidwordId(item.getBidwordId());
            candidate.setBidwordText(item.getBidwordText());
            candidate.setMetricValue(metricValue);
            candidates.add(candidate);
        }
        return candidates;
    }

    private void sortBidwordMetricCandidates(List<BidwordMetricCandidate> candidates, ScoringMetricKey metricKey) {
        Comparator<BigDecimal> metricValueComparator = metricKey.isHigherBetter() ? (left, right) -> right.compareTo(left) : BigDecimal::compareTo;
        candidates.sort(Comparator.comparing(BidwordMetricCandidate::getMetricValue, metricValueComparator)
                .thenComparing(BidwordMetricCandidate::getBidwordId, Comparator.nullsLast(Long::compareTo)));
    }

    private int[] buildCompetitionRanks(List<BidwordMetricCandidate> candidates) {
        int[] ranks = new int[candidates.size()];
        for (int i = 0; i < candidates.size(); i++) {
            if (i == 0) {
                ranks[i] = 1;
            } else if (candidates.get(i).getMetricValue().compareTo(candidates.get(i - 1).getMetricValue()) == 0) {
                ranks[i] = ranks[i - 1];
            } else {
                ranks[i] = i + 1;
            }
        }
        return ranks;
    }

    private BigDecimal extractMetricValue(BidwordMetricsMatrixItem item, ScoringMetricKey metricKey, Integer effectDays) {
        return switch (effectDays) {
            case 1 -> extract1dMetricValue(item, metricKey);
            case 3 -> extract3dMetricValue(item, metricKey);
            case 7 -> extract7dMetricValue(item, metricKey);
            default -> null;
        };
    }

    private BigDecimal extract1dMetricValue(BidwordMetricsMatrixItem item, ScoringMetricKey metricKey) {
        return switch (metricKey) {
            case ROI -> item.getRoi1d();
            case CVR -> item.getCvr1d();
            case CPC -> item.getEcpc1d();
            case CART_COST -> item.getCartCost1d();
            case DEAL_NEW_CUSTOMER_RATIO -> item.getDealNewCustomerRatio1d();
            case NEW_CUSTOMER_RATIO -> item.getNewCustomerRatio1d();
            case DIRECT_DEAL_RATIO -> item.getDirectDealRatio1d();
        };
    }

    private BigDecimal extract3dMetricValue(BidwordMetricsMatrixItem item, ScoringMetricKey metricKey) {
        return switch (metricKey) {
            case ROI -> item.getRoi3d();
            case CVR -> item.getCvr3d();
            case CPC -> item.getEcpc3d();
            case CART_COST -> item.getCartCost3d();
            case DEAL_NEW_CUSTOMER_RATIO -> item.getDealNewCustomerRatio3d();
            case NEW_CUSTOMER_RATIO -> item.getNewCustomerRatio3d();
            case DIRECT_DEAL_RATIO -> item.getDirectDealRatio3d();
        };
    }

    private BigDecimal extract7dMetricValue(BidwordMetricsMatrixItem item, ScoringMetricKey metricKey) {
        return switch (metricKey) {
            case ROI -> item.getRoi7d();
            case CVR -> item.getCvr7d();
            case CPC -> item.getEcpc7d();
            case CART_COST -> item.getCartCost7d();
            case DEAL_NEW_CUSTOMER_RATIO -> item.getDealNewCustomerRatio7d();
            case NEW_CUSTOMER_RATIO -> item.getNewCustomerRatio7d();
            case DIRECT_DEAL_RATIO -> item.getDirectDealRatio7d();
        };
    }

    private BigDecimal buildRankingScore(int rank, int comparisonCount) {
        if (comparisonCount <= 0) {
            return null;
        }
        if (comparisonCount == 1) {
            return ONE_HUNDRED.setScale(4, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(comparisonCount - rank)
                .multiply(ONE_HUNDRED)
                .divide(BigDecimal.valueOf(comparisonCount - 1), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal buildTargetValueScore(BigDecimal metricValue, BigDecimal targetValue, ScoringMetricKey metricKey) {
        if (metricValue == null || targetValue == null || targetValue.compareTo(ZERO) == 0) {
            return null;
        }
        BigDecimal score;
        if (metricKey.isHigherBetter()) {
            score = metricValue.compareTo(targetValue) >= 0 ? ONE_HUNDRED : metricValue.divide(targetValue, 8, RoundingMode.HALF_UP).multiply(ONE_HUNDRED);
        } else if (metricValue.compareTo(targetValue) <= 0 || metricValue.compareTo(ZERO) == 0) {
            score = ONE_HUNDRED;
        } else {
            score = targetValue.divide(metricValue, 8, RoundingMode.HALF_UP).multiply(ONE_HUNDRED);
        }
        if (score.compareTo(ZERO) < 0) {
            return ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        if (score.compareTo(ONE_HUNDRED) > 0) {
            return ONE_HUNDRED.setScale(4, RoundingMode.HALF_UP);
        }
        return score.setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal buildSmartBenchmarkScore(BigDecimal metricValue, BigDecimal benchmarkValue, ScoringMetricKey metricKey) {
        if (metricValue == null || benchmarkValue == null) {
            return null;
        }
        BigDecimal score;
        if (metricKey.isHigherBetter()) {
            score = benchmarkValue.compareTo(ZERO) == 0
                    ? (metricValue.compareTo(ZERO) == 0 ? ONE_HUNDRED : ZERO)
                    : metricValue.divide(benchmarkValue, 8, RoundingMode.HALF_UP).multiply(ONE_HUNDRED);
        } else {
            score = metricValue.compareTo(ZERO) == 0
                    ? (benchmarkValue.compareTo(ZERO) == 0 ? ONE_HUNDRED : ZERO)
                    : benchmarkValue.divide(metricValue, 8, RoundingMode.HALF_UP).multiply(ONE_HUNDRED);
        }
        if (score.compareTo(ZERO) < 0) {
            return ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        if (score.compareTo(ONE_HUNDRED) > 0) {
            return ONE_HUNDRED.setScale(4, RoundingMode.HALF_UP);
        }
        return score.setScale(4, RoundingMode.HALF_UP);
    }

    private WeightedMetricSpec buildWeightedMetricSpec(ScoringMetricConfigRequest metricConfig) {
        ScoringMetricKey metricKey = ScoringMetricKey.fromCode(metricConfig.getMetricKey());
        if (metricKey == null) {
            return null;
        }
        ScoringRuleType ruleType;
        try {
            ruleType = ScoringRuleType.fromCode(metricConfig.getRuleType());
        } catch (IllegalArgumentException ex) {
            return null;
        }
        if (ruleType != ScoringRuleType.RANKING && ruleType != ScoringRuleType.TARGET_VALUE && ruleType != ScoringRuleType.SMART_BENCHMARK) {
            return null;
        }
        WeightedMetricSpec metricSpec = new WeightedMetricSpec();
        metricSpec.setMetricKey(metricKey);
        metricSpec.setRuleType(ruleType);
        metricSpec.setWeight(metricConfig.getWeight());
        metricSpec.setTargetValue(metricConfig.getTargetValue());
        return metricSpec;
    }

    private static class WeightedMetricSpec {
        private ScoringMetricKey metricKey;
        private ScoringRuleType ruleType;
        private BigDecimal weight;
        private BigDecimal targetValue;
        public ScoringMetricKey getMetricKey() { return metricKey; }
        public void setMetricKey(ScoringMetricKey metricKey) { this.metricKey = metricKey; }
        public ScoringRuleType getRuleType() { return ruleType; }
        public void setRuleType(ScoringRuleType ruleType) { this.ruleType = ruleType; }
        public BigDecimal getWeight() { return weight; }
        public void setWeight(BigDecimal weight) { this.weight = weight; }
        public BigDecimal getTargetValue() { return targetValue; }
        public void setTargetValue(BigDecimal targetValue) { this.targetValue = targetValue; }
    }

    private static class BidwordMetricScoreSnapshot {
        private Long campaignId;
        private String campaignName;
        private Long adgroupId;
        private String adgroupName;
        private Long bidwordId;
        private String bidwordText;
        private BigDecimal metricValue;
        private Integer rank;
        private BigDecimal score;
        public Long getCampaignId() { return campaignId; }
        public void setCampaignId(Long campaignId) { this.campaignId = campaignId; }
        public String getCampaignName() { return campaignName; }
        public void setCampaignName(String campaignName) { this.campaignName = campaignName; }
        public Long getAdgroupId() { return adgroupId; }
        public void setAdgroupId(Long adgroupId) { this.adgroupId = adgroupId; }
        public String getAdgroupName() { return adgroupName; }
        public void setAdgroupName(String adgroupName) { this.adgroupName = adgroupName; }
        public Long getBidwordId() { return bidwordId; }
        public void setBidwordId(Long bidwordId) { this.bidwordId = bidwordId; }
        public String getBidwordText() { return bidwordText; }
        public void setBidwordText(String bidwordText) { this.bidwordText = bidwordText; }
        public BigDecimal getMetricValue() { return metricValue; }
        public void setMetricValue(BigDecimal metricValue) { this.metricValue = metricValue; }
        public Integer getRank() { return rank; }
        public void setRank(Integer rank) { this.rank = rank; }
        public BigDecimal getScore() { return score; }
        public void setScore(BigDecimal score) { this.score = score; }
    }

    private static class BidwordMetricCandidate {
        private Long campaignId;
        private String campaignName;
        private Long adgroupId;
        private String adgroupName;
        private Long bidwordId;
        private String bidwordText;
        private BigDecimal metricValue;
        public Long getCampaignId() { return campaignId; }
        public void setCampaignId(Long campaignId) { this.campaignId = campaignId; }
        public String getCampaignName() { return campaignName; }
        public void setCampaignName(String campaignName) { this.campaignName = campaignName; }
        public Long getAdgroupId() { return adgroupId; }
        public void setAdgroupId(Long adgroupId) { this.adgroupId = adgroupId; }
        public String getAdgroupName() { return adgroupName; }
        public void setAdgroupName(String adgroupName) { this.adgroupName = adgroupName; }
        public Long getBidwordId() { return bidwordId; }
        public void setBidwordId(Long bidwordId) { this.bidwordId = bidwordId; }
        public String getBidwordText() { return bidwordText; }
        public void setBidwordText(String bidwordText) { this.bidwordText = bidwordText; }
        public BigDecimal getMetricValue() { return metricValue; }
        public void setMetricValue(BigDecimal metricValue) { this.metricValue = metricValue; }
    }

    private static class BidwordWeightedRowAccumulator {
        private Long campaignId;
        private String campaignName;
        private Long adgroupId;
        private String adgroupName;
        private Long bidwordId;
        private String bidwordText;
        private BigDecimal weightedScoreSum;
        private BigDecimal participatingWeightSum;
        private List<BidwordWeightedRankingPreviewResponse.MetricContribution> metricContributions;
        public Long getCampaignId() { return campaignId; }
        public void setCampaignId(Long campaignId) { this.campaignId = campaignId; }
        public String getCampaignName() { return campaignName; }
        public void setCampaignName(String campaignName) { this.campaignName = campaignName; }
        public Long getAdgroupId() { return adgroupId; }
        public void setAdgroupId(Long adgroupId) { this.adgroupId = adgroupId; }
        public String getAdgroupName() { return adgroupName; }
        public void setAdgroupName(String adgroupName) { this.adgroupName = adgroupName; }
        public Long getBidwordId() { return bidwordId; }
        public void setBidwordId(Long bidwordId) { this.bidwordId = bidwordId; }
        public String getBidwordText() { return bidwordText; }
        public void setBidwordText(String bidwordText) { this.bidwordText = bidwordText; }
        public BigDecimal getWeightedScoreSum() { return weightedScoreSum; }
        public void setWeightedScoreSum(BigDecimal weightedScoreSum) { this.weightedScoreSum = weightedScoreSum; }
        public BigDecimal getParticipatingWeightSum() { return participatingWeightSum; }
        public void setParticipatingWeightSum(BigDecimal participatingWeightSum) { this.participatingWeightSum = participatingWeightSum; }
        public List<BidwordWeightedRankingPreviewResponse.MetricContribution> getMetricContributions() { return metricContributions; }
        public void setMetricContributions(List<BidwordWeightedRankingPreviewResponse.MetricContribution> metricContributions) { this.metricContributions = metricContributions; }
    }
}
