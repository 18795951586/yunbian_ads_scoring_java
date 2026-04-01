package com.yunbian.adsscoring.scoring.service.impl;

import com.yunbian.adsscoring.adgroup.dto.AdgroupMetricsMatrixItem;
import com.yunbian.adsscoring.adgroup.mapper.AdgroupMetricsMatrixMapper;
import com.yunbian.adsscoring.scoring.dto.AdgroupScoringResponse;
import com.yunbian.adsscoring.scoring.dto.AdgroupWeightedRankingPreviewResponse;
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
public class AdgroupWeightedPreviewService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Resource
    private AdgroupMetricsMatrixMapper adgroupMetricsMatrixMapper;

    public AdgroupWeightedRankingPreviewResponse previewAdgroupWeightedRanking(
            Long sid,
            LocalDate logDate,
            Integer effectDays,
            ScoringSchemeCreateRequest request
    ) {
        return buildAdgroupWeightedScoringResponse(sid, logDate, effectDays, request);
    }

    public AdgroupScoringResponse calculateAdgroupScoring(
            Long sid,
            LocalDate logDate,
            Integer effectDays,
            ScoringSchemeCreateRequest request
    ) {
        return toAdgroupScoringResponse(buildAdgroupWeightedScoringResponse(sid, logDate, effectDays, request));
    }

    private AdgroupScoringResponse toAdgroupScoringResponse(AdgroupWeightedRankingPreviewResponse previewResponse) {
        AdgroupScoringResponse response = new AdgroupScoringResponse();
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
                .map(this::toAdgroupScoringMetricSummary)
                .toList());
        response.setRows(previewResponse.getRows().stream()
                .map(this::toAdgroupScoringRow)
                .toList());
        return response;
    }

    private AdgroupScoringResponse.MetricSummary toAdgroupScoringMetricSummary(
            AdgroupWeightedRankingPreviewResponse.MetricSummary previewMetricSummary
    ) {
        AdgroupScoringResponse.MetricSummary metricSummary = new AdgroupScoringResponse.MetricSummary();
        metricSummary.setMetricKey(previewMetricSummary.getMetricKey());
        metricSummary.setMetricName(previewMetricSummary.getMetricName());
        metricSummary.setRuleType(previewMetricSummary.getRuleType());
        metricSummary.setWeight(previewMetricSummary.getWeight());
        metricSummary.setComparisonCount(previewMetricSummary.getComparisonCount());
        metricSummary.setExcludedNullCount(previewMetricSummary.getExcludedNullCount());
        metricSummary.setUsedInAggregation(previewMetricSummary.getUsedInAggregation());
        return metricSummary;
    }

    private AdgroupScoringResponse.AdgroupScoringRow toAdgroupScoringRow(
            AdgroupWeightedRankingPreviewResponse.AdgroupWeightedRankingRow previewRow
    ) {
        AdgroupScoringResponse.AdgroupScoringRow row = new AdgroupScoringResponse.AdgroupScoringRow();
        row.setCampaignId(previewRow.getCampaignId());
        row.setCampaignName(previewRow.getCampaignName());
        row.setAdgroupId(previewRow.getAdgroupId());
        row.setAdgroupName(previewRow.getAdgroupName());
        row.setTotalScore(previewRow.getTotalScore());
        row.setParticipatingMetricCount(previewRow.getParticipatingMetricCount());
        row.setParticipatingWeightSum(previewRow.getParticipatingWeightSum());
        row.setMetricContributions(previewRow.getMetricContributions().stream()
                .map(this::toAdgroupScoringMetricContribution)
                .toList());
        return row;
    }

    private AdgroupScoringResponse.MetricContribution toAdgroupScoringMetricContribution(
            AdgroupWeightedRankingPreviewResponse.MetricContribution previewContribution
    ) {
        AdgroupScoringResponse.MetricContribution contribution = new AdgroupScoringResponse.MetricContribution();
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

    private AdgroupWeightedRankingPreviewResponse buildAdgroupWeightedScoringResponse(
            Long sid,
            LocalDate logDate,
            Integer effectDays,
            ScoringSchemeCreateRequest request
    ) {
        List<AdgroupMetricsMatrixItem> sourceRows =
                adgroupMetricsMatrixMapper.selectAllMetricsMatrixBySidAndLogDate(sid, logDate);

        ScoringLevelConfigRequest adgroupLevelConfig = request.getLevelConfigs().stream()
                .filter(level -> ScoringEntityLevel.ADGROUP.getCode().equals(level.getEntityLevel()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("adgroup level config is required"));

        List<WeightedMetricSpec> metricSpecs = adgroupLevelConfig.getMetricConfigs().stream()
                .filter(metric -> Boolean.TRUE.equals(metric.getEnabled()))
                .filter(metric -> metric.getWeight() != null && metric.getWeight().compareTo(ZERO) > 0)
                .map(this::buildWeightedMetricSpec)
                .filter(Objects::nonNull)
                .toList();

        List<AdgroupWeightedRankingPreviewResponse.MetricSummary> metricSummaries = new ArrayList<>();
        Map<String, AdgroupWeightedRowAccumulator> rowAccumulatorMap = new LinkedHashMap<>();

        for (WeightedMetricSpec metricSpec : metricSpecs) {
            List<AdgroupMetricCandidate> candidates = buildAdgroupMetricCandidates(sourceRows, metricSpec.getMetricKey(), effectDays);
            int comparisonCount = candidates.size();
            int excludedNullCount = sourceRows.size() - comparisonCount;
            boolean usedInAggregation = comparisonCount > 0 && metricSpec.getWeight().compareTo(ZERO) > 0;

            AdgroupWeightedRankingPreviewResponse.MetricSummary metricSummary =
                    new AdgroupWeightedRankingPreviewResponse.MetricSummary();
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

            Map<String, AdgroupMetricScoreSnapshot> scoreSnapshotByAdgroupId =
                    buildAdgroupMetricScoreSnapshotByAdgroupId(metricSpec, candidates);

            for (AdgroupMetricScoreSnapshot snapshot : scoreSnapshotByAdgroupId.values()) {
                BigDecimal score = snapshot.getScore();
                if (score == null) {
                    continue;
                }
                BigDecimal weightedScore = score.multiply(metricSpec.getWeight());

                AdgroupWeightedRowAccumulator accumulator = rowAccumulatorMap.computeIfAbsent(
                        buildAdgroupIdentityKey(snapshot.getCampaignId(), snapshot.getAdgroupId()),
                        key -> {
                            AdgroupWeightedRowAccumulator value = new AdgroupWeightedRowAccumulator();
                            value.setCampaignId(snapshot.getCampaignId());
                            value.setCampaignName(snapshot.getCampaignName());
                            value.setAdgroupId(snapshot.getAdgroupId());
                            value.setAdgroupName(snapshot.getAdgroupName());
                            value.setWeightedScoreSum(ZERO);
                            value.setParticipatingWeightSum(ZERO);
                            value.setMetricContributions(new ArrayList<>());
                            return value;
                        }
                );

                accumulator.setCampaignId(snapshot.getCampaignId());
                accumulator.setCampaignName(snapshot.getCampaignName());
                accumulator.setAdgroupId(snapshot.getAdgroupId());
                accumulator.setAdgroupName(snapshot.getAdgroupName());
                accumulator.setWeightedScoreSum(accumulator.getWeightedScoreSum().add(weightedScore));
                accumulator.setParticipatingWeightSum(accumulator.getParticipatingWeightSum().add(metricSpec.getWeight()));

                AdgroupWeightedRankingPreviewResponse.MetricContribution contribution =
                        new AdgroupWeightedRankingPreviewResponse.MetricContribution();
                contribution.setMetricKey(metricSpec.getMetricKey().getCode());
                contribution.setMetricName(metricSpec.getMetricKey().getName());
                contribution.setRuleType(metricSpec.getRuleType().getCode());
                contribution.setMetricValue(snapshot.getMetricValue());
                contribution.setRank(snapshot.getRank());
                contribution.setScore(score);
                contribution.setWeight(metricSpec.getWeight());
                contribution.setWeightedScore(weightedScore.setScale(4, RoundingMode.HALF_UP));

                accumulator.getMetricContributions().add(contribution);
            }
        }

        List<AdgroupWeightedRankingPreviewResponse.AdgroupWeightedRankingRow> rows = rowAccumulatorMap.values().stream()
                .map(this::buildAdgroupWeightedRankingRow)
                .sorted(
                        Comparator.comparing(
                                        AdgroupWeightedRankingPreviewResponse.AdgroupWeightedRankingRow::getTotalScore,
                                        Comparator.nullsLast(BigDecimal::compareTo)
                                )
                                .reversed()
                                .thenComparing(
                                        AdgroupWeightedRankingPreviewResponse.AdgroupWeightedRankingRow::getCampaignId,
                                        Comparator.nullsLast(Long::compareTo)
                                )
                                .thenComparing(
                                        AdgroupWeightedRankingPreviewResponse.AdgroupWeightedRankingRow::getAdgroupId,
                                        Comparator.nullsLast(Long::compareTo)
                                )
                )
                .toList();

        int usedMetricCount = (int) metricSummaries.stream()
                .filter(summary -> Boolean.TRUE.equals(summary.getUsedInAggregation()))
                .count();
        int enabledRankingMetricCount = (int) metricSpecs.stream()
                .filter(metric -> metric.getRuleType() == ScoringRuleType.RANKING)
                .count();

        AdgroupWeightedRankingPreviewResponse response = new AdgroupWeightedRankingPreviewResponse();
        response.setSid(sid);
        response.setLogDate(logDate);
        response.setEntityLevel(ScoringEntityLevel.ADGROUP.getCode());
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

    private Map<String, AdgroupMetricScoreSnapshot> buildAdgroupMetricScoreSnapshotByAdgroupId(
            WeightedMetricSpec metricSpec,
            List<AdgroupMetricCandidate> candidates
    ) {
        Map<String, AdgroupMetricScoreSnapshot> snapshotByAdgroupId = new LinkedHashMap<>();
        if (candidates.isEmpty()) {
            return snapshotByAdgroupId;
        }

        if (metricSpec.getRuleType() == ScoringRuleType.RANKING) {
            Map<Long, List<AdgroupMetricCandidate>> groupedCandidates = groupByCampaignId(candidates);
            for (List<AdgroupMetricCandidate> group : groupedCandidates.values()) {
                sortAdgroupMetricCandidates(group, metricSpec.getMetricKey());
                int[] ranks = buildAdgroupCompetitionRanks(group);
                for (int i = 0; i < group.size(); i++) {
                    AdgroupMetricCandidate candidate = group.get(i);
                    AdgroupMetricScoreSnapshot snapshot = new AdgroupMetricScoreSnapshot();
                    snapshot.setCampaignId(candidate.getCampaignId());
                    snapshot.setCampaignName(candidate.getCampaignName());
                    snapshot.setAdgroupId(candidate.getAdgroupId());
                    snapshot.setAdgroupName(candidate.getAdgroupName());
                    snapshot.setMetricValue(candidate.getMetricValue());
                    snapshot.setRank(ranks[i]);
                    snapshot.setScore(buildRankingScore(ranks[i], group.size()));
                    snapshotByAdgroupId.put(buildAdgroupIdentityKey(candidate.getCampaignId(), candidate.getAdgroupId()), snapshot);
                }
            }
            return snapshotByAdgroupId;
        }

        if (metricSpec.getRuleType() == ScoringRuleType.SMART_BENCHMARK) {
            Map<Long, List<AdgroupMetricCandidate>> groupedCandidates = groupByCampaignId(candidates);
            for (List<AdgroupMetricCandidate> group : groupedCandidates.values()) {
                BigDecimal benchmark = metricSpec.getMetricKey().isHigherBetter()
                        ? group.stream().map(AdgroupMetricCandidate::getMetricValue).max(BigDecimal::compareTo).orElse(null)
                        : group.stream().map(AdgroupMetricCandidate::getMetricValue).min(BigDecimal::compareTo).orElse(null);

                for (AdgroupMetricCandidate candidate : group) {
                    AdgroupMetricScoreSnapshot snapshot = new AdgroupMetricScoreSnapshot();
                    snapshot.setCampaignId(candidate.getCampaignId());
                    snapshot.setCampaignName(candidate.getCampaignName());
                    snapshot.setAdgroupId(candidate.getAdgroupId());
                    snapshot.setAdgroupName(candidate.getAdgroupName());
                    snapshot.setMetricValue(candidate.getMetricValue());
                    snapshot.setScore(buildSmartBenchmarkScore(candidate.getMetricValue(), benchmark, metricSpec.getMetricKey()));
                    snapshotByAdgroupId.put(buildAdgroupIdentityKey(candidate.getCampaignId(), candidate.getAdgroupId()), snapshot);
                }
            }
            return snapshotByAdgroupId;
        }

        for (AdgroupMetricCandidate candidate : candidates) {
            AdgroupMetricScoreSnapshot snapshot = new AdgroupMetricScoreSnapshot();
            snapshot.setCampaignId(candidate.getCampaignId());
            snapshot.setCampaignName(candidate.getCampaignName());
            snapshot.setAdgroupId(candidate.getAdgroupId());
            snapshot.setAdgroupName(candidate.getAdgroupName());
            snapshot.setMetricValue(candidate.getMetricValue());
            snapshot.setScore(
                    buildTargetValueScore(candidate.getMetricValue(), metricSpec.getTargetValue(), metricSpec.getMetricKey())
            );
            snapshotByAdgroupId.put(buildAdgroupIdentityKey(candidate.getCampaignId(), candidate.getAdgroupId()), snapshot);
        }
        return snapshotByAdgroupId;
    }

    private String buildAdgroupIdentityKey(Long campaignId, Long adgroupId) {
        return String.valueOf(campaignId) + "#" + String.valueOf(adgroupId);
    }

    private Map<Long, List<AdgroupMetricCandidate>> groupByCampaignId(List<AdgroupMetricCandidate> candidates) {
        Map<Long, List<AdgroupMetricCandidate>> grouped = new LinkedHashMap<>();
        for (AdgroupMetricCandidate candidate : candidates) {
            grouped.computeIfAbsent(candidate.getCampaignId(), key -> new ArrayList<>()).add(candidate);
        }
        return grouped;
    }

    private AdgroupWeightedRankingPreviewResponse.AdgroupWeightedRankingRow buildAdgroupWeightedRankingRow(
            AdgroupWeightedRowAccumulator accumulator
    ) {
        AdgroupWeightedRankingPreviewResponse.AdgroupWeightedRankingRow row =
                new AdgroupWeightedRankingPreviewResponse.AdgroupWeightedRankingRow();
        row.setCampaignId(accumulator.getCampaignId());
        row.setCampaignName(accumulator.getCampaignName());
        row.setAdgroupId(accumulator.getAdgroupId());
        row.setAdgroupName(accumulator.getAdgroupName());
        row.setParticipatingMetricCount(accumulator.getMetricContributions().size());
        row.setParticipatingWeightSum(accumulator.getParticipatingWeightSum().setScale(4, RoundingMode.HALF_UP));

        BigDecimal totalScore = null;
        if (accumulator.getParticipatingWeightSum().compareTo(ZERO) > 0) {
            totalScore = accumulator.getWeightedScoreSum()
                    .divide(accumulator.getParticipatingWeightSum(), 4, RoundingMode.HALF_UP);
        }
        row.setTotalScore(totalScore);
        row.setMetricContributions(accumulator.getMetricContributions());
        return row;
    }

    private List<AdgroupMetricCandidate> buildAdgroupMetricCandidates(
            List<AdgroupMetricsMatrixItem> sourceRows,
            ScoringMetricKey metricKey,
            Integer effectDays
    ) {
        List<AdgroupMetricCandidate> candidates = new ArrayList<>();
        for (AdgroupMetricsMatrixItem item : sourceRows) {
            BigDecimal metricValue = extractMetricValue(item, metricKey, effectDays);
            if (metricValue == null) {
                continue;
            }

            AdgroupMetricCandidate candidate = new AdgroupMetricCandidate();
            candidate.setCampaignId(item.getCampaignId());
            candidate.setCampaignName(item.getCampaignName());
            candidate.setAdgroupId(item.getAdgroupId());
            candidate.setAdgroupName(item.getAdgroupName());
            candidate.setMetricValue(metricValue);
            candidates.add(candidate);
        }
        return candidates;
    }

    private void sortAdgroupMetricCandidates(List<AdgroupMetricCandidate> candidates, ScoringMetricKey metricKey) {
        Comparator<BigDecimal> metricValueComparator = metricKey.isHigherBetter()
                ? (left, right) -> right.compareTo(left)
                : BigDecimal::compareTo;

        candidates.sort(
                Comparator.comparing(AdgroupMetricCandidate::getMetricValue, metricValueComparator)
                        .thenComparing(AdgroupMetricCandidate::getAdgroupId, Comparator.nullsLast(Long::compareTo))
        );
    }

    private int[] buildAdgroupCompetitionRanks(List<AdgroupMetricCandidate> candidates) {
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

    private BigDecimal extractMetricValue(
            AdgroupMetricsMatrixItem item,
            ScoringMetricKey metricKey,
            Integer effectDays
    ) {
        return switch (effectDays) {
            case 1 -> extract1dMetricValue(item, metricKey);
            case 3 -> extract3dMetricValue(item, metricKey);
            case 7 -> extract7dMetricValue(item, metricKey);
            default -> null;
        };
    }

    private BigDecimal extract1dMetricValue(AdgroupMetricsMatrixItem item, ScoringMetricKey metricKey) {
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

    private BigDecimal extract3dMetricValue(AdgroupMetricsMatrixItem item, ScoringMetricKey metricKey) {
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

    private BigDecimal extract7dMetricValue(AdgroupMetricsMatrixItem item, ScoringMetricKey metricKey) {
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

        BigDecimal numerator = BigDecimal.valueOf(comparisonCount - rank);
        BigDecimal denominator = BigDecimal.valueOf(comparisonCount - 1);

        return numerator
                .multiply(ONE_HUNDRED)
                .divide(denominator, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal buildTargetValueScore(BigDecimal metricValue, BigDecimal targetValue, ScoringMetricKey metricKey) {
        if (metricValue == null || targetValue == null || targetValue.compareTo(ZERO) == 0) {
            return null;
        }

        BigDecimal score;
        if (metricKey.isHigherBetter()) {
            if (metricValue.compareTo(targetValue) >= 0) {
                score = ONE_HUNDRED;
            } else {
                score = metricValue
                        .divide(targetValue, 8, RoundingMode.HALF_UP)
                        .multiply(ONE_HUNDRED);
            }
        } else {
            if (metricValue.compareTo(targetValue) <= 0) {
                score = ONE_HUNDRED;
            } else if (metricValue.compareTo(ZERO) == 0) {
                score = ONE_HUNDRED;
            } else {
                score = targetValue
                        .divide(metricValue, 8, RoundingMode.HALF_UP)
                        .multiply(ONE_HUNDRED);
            }
        }

        if (score.compareTo(ZERO) < 0) {
            return ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        if (score.compareTo(ONE_HUNDRED) > 0) {
            return ONE_HUNDRED.setScale(4, RoundingMode.HALF_UP);
        }
        return score.setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal buildSmartBenchmarkScore(
            BigDecimal metricValue,
            BigDecimal benchmarkValue,
            ScoringMetricKey metricKey
    ) {
        if (metricValue == null || benchmarkValue == null) {
            return null;
        }

        BigDecimal score;
        if (metricKey.isHigherBetter()) {
            if (benchmarkValue.compareTo(ZERO) == 0) {
                score = metricValue.compareTo(ZERO) == 0 ? ONE_HUNDRED : ZERO;
            } else {
                score = metricValue
                        .divide(benchmarkValue, 8, RoundingMode.HALF_UP)
                        .multiply(ONE_HUNDRED);
            }
        } else {
            if (metricValue.compareTo(ZERO) == 0) {
                score = benchmarkValue.compareTo(ZERO) == 0 ? ONE_HUNDRED : ZERO;
            } else {
                score = benchmarkValue
                        .divide(metricValue, 8, RoundingMode.HALF_UP)
                        .multiply(ONE_HUNDRED);
            }
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

        if (ruleType != ScoringRuleType.RANKING
                && ruleType != ScoringRuleType.TARGET_VALUE
                && ruleType != ScoringRuleType.SMART_BENCHMARK) {
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

        public ScoringMetricKey getMetricKey() {
            return metricKey;
        }

        public void setMetricKey(ScoringMetricKey metricKey) {
            this.metricKey = metricKey;
        }

        public ScoringRuleType getRuleType() {
            return ruleType;
        }

        public void setRuleType(ScoringRuleType ruleType) {
            this.ruleType = ruleType;
        }

        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }

        public BigDecimal getTargetValue() {
            return targetValue;
        }

        public void setTargetValue(BigDecimal targetValue) {
            this.targetValue = targetValue;
        }
    }

    private static class AdgroupMetricScoreSnapshot {

        private Long campaignId;
        private String campaignName;
        private Long adgroupId;
        private String adgroupName;
        private BigDecimal metricValue;
        private Integer rank;
        private BigDecimal score;

        public Long getCampaignId() {
            return campaignId;
        }

        public void setCampaignId(Long campaignId) {
            this.campaignId = campaignId;
        }

        public String getCampaignName() {
            return campaignName;
        }

        public void setCampaignName(String campaignName) {
            this.campaignName = campaignName;
        }

        public Long getAdgroupId() {
            return adgroupId;
        }

        public void setAdgroupId(Long adgroupId) {
            this.adgroupId = adgroupId;
        }

        public String getAdgroupName() {
            return adgroupName;
        }

        public void setAdgroupName(String adgroupName) {
            this.adgroupName = adgroupName;
        }

        public BigDecimal getMetricValue() {
            return metricValue;
        }

        public void setMetricValue(BigDecimal metricValue) {
            this.metricValue = metricValue;
        }

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public BigDecimal getScore() {
            return score;
        }

        public void setScore(BigDecimal score) {
            this.score = score;
        }
    }

    private static class AdgroupMetricCandidate {

        private Long campaignId;
        private String campaignName;
        private Long adgroupId;
        private String adgroupName;
        private BigDecimal metricValue;

        public Long getCampaignId() {
            return campaignId;
        }

        public void setCampaignId(Long campaignId) {
            this.campaignId = campaignId;
        }

        public String getCampaignName() {
            return campaignName;
        }

        public void setCampaignName(String campaignName) {
            this.campaignName = campaignName;
        }

        public Long getAdgroupId() {
            return adgroupId;
        }

        public void setAdgroupId(Long adgroupId) {
            this.adgroupId = adgroupId;
        }

        public String getAdgroupName() {
            return adgroupName;
        }

        public void setAdgroupName(String adgroupName) {
            this.adgroupName = adgroupName;
        }

        public BigDecimal getMetricValue() {
            return metricValue;
        }

        public void setMetricValue(BigDecimal metricValue) {
            this.metricValue = metricValue;
        }
    }

    private static class AdgroupWeightedRowAccumulator {

        private Long campaignId;
        private String campaignName;
        private Long adgroupId;
        private String adgroupName;
        private BigDecimal weightedScoreSum;
        private BigDecimal participatingWeightSum;
        private List<AdgroupWeightedRankingPreviewResponse.MetricContribution> metricContributions;

        public Long getCampaignId() {
            return campaignId;
        }

        public void setCampaignId(Long campaignId) {
            this.campaignId = campaignId;
        }

        public String getCampaignName() {
            return campaignName;
        }

        public void setCampaignName(String campaignName) {
            this.campaignName = campaignName;
        }

        public Long getAdgroupId() {
            return adgroupId;
        }

        public void setAdgroupId(Long adgroupId) {
            this.adgroupId = adgroupId;
        }

        public String getAdgroupName() {
            return adgroupName;
        }

        public void setAdgroupName(String adgroupName) {
            this.adgroupName = adgroupName;
        }

        public BigDecimal getWeightedScoreSum() {
            return weightedScoreSum;
        }

        public void setWeightedScoreSum(BigDecimal weightedScoreSum) {
            this.weightedScoreSum = weightedScoreSum;
        }

        public BigDecimal getParticipatingWeightSum() {
            return participatingWeightSum;
        }

        public void setParticipatingWeightSum(BigDecimal participatingWeightSum) {
            this.participatingWeightSum = participatingWeightSum;
        }

        public List<AdgroupWeightedRankingPreviewResponse.MetricContribution> getMetricContributions() {
            return metricContributions;
        }

        public void setMetricContributions(List<AdgroupWeightedRankingPreviewResponse.MetricContribution> metricContributions) {
            this.metricContributions = metricContributions;
        }
    }
}