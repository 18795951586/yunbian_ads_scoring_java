package com.yunbian.adsscoring.scoring.service.impl;

import com.yunbian.adsscoring.campaign.dto.CampaignMetricsMatrixItem;
import com.yunbian.adsscoring.campaign.mapper.CampaignMetricsMatrixMapper;
import com.yunbian.adsscoring.scoring.dto.AdgroupScoringResponse;
import com.yunbian.adsscoring.scoring.dto.AdgroupWeightedRankingPreviewResponse;
import com.yunbian.adsscoring.scoring.dto.CampaignRankingPreviewResponse;
import com.yunbian.adsscoring.scoring.dto.CampaignScoringResponse;
import com.yunbian.adsscoring.scoring.dto.CampaignSmartBenchmarkPreviewResponse;
import com.yunbian.adsscoring.scoring.dto.CampaignTargetValuePreviewResponse;
import com.yunbian.adsscoring.scoring.dto.CampaignWeightedRankingPreviewResponse;
import com.yunbian.adsscoring.scoring.dto.BidwordScoringResponse;
import com.yunbian.adsscoring.scoring.dto.BidwordWeightedRankingPreviewResponse;
import com.yunbian.adsscoring.scoring.enums.ScoringEntityLevel;
import com.yunbian.adsscoring.scoring.enums.ScoringMetricKey;
import com.yunbian.adsscoring.scoring.enums.ScoringRuleType;
import com.yunbian.adsscoring.scoring.request.ScoringLevelConfigRequest;
import com.yunbian.adsscoring.scoring.request.ScoringMetricConfigRequest;
import com.yunbian.adsscoring.scoring.request.ScoringSchemeCreateRequest;
import com.yunbian.adsscoring.scoring.service.ScoringPreviewService;
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
public class ScoringPreviewServiceImpl implements ScoringPreviewService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Resource
    private CampaignMetricsMatrixMapper campaignMetricsMatrixMapper;

    @Resource
    private AdgroupWeightedPreviewService adgroupWeightedPreviewService;

    @Resource
    private BidwordWeightedPreviewService bidwordWeightedPreviewService;

    @Override
    public CampaignRankingPreviewResponse previewCampaignRanking(
            Long sid,
            LocalDate logDate,
            ScoringMetricKey metricKey,
            Integer effectDays
    ) {
        List<CampaignMetricsMatrixItem> sourceRows =
                campaignMetricsMatrixMapper.selectAllMetricsMatrixBySidAndLogDate(sid, logDate);

        List<MetricCandidate> candidates = buildMetricCandidates(sourceRows, metricKey, effectDays);
        sortMetricCandidates(candidates, metricKey);

        List<CampaignRankingPreviewResponse.CampaignRankingRow> rankingRows = new ArrayList<>();
        int comparisonCount = candidates.size();

        for (int i = 0; i < candidates.size(); i++) {
            MetricCandidate candidate = candidates.get(i);

            int rank;
            if (i == 0) {
                rank = 1;
            } else {
                BigDecimal prevValue = candidates.get(i - 1).getMetricValue();
                if (candidate.getMetricValue().compareTo(prevValue) == 0) {
                    rank = rankingRows.get(i - 1).getRank();
                } else {
                    rank = i + 1;
                }
            }

            CampaignRankingPreviewResponse.CampaignRankingRow row =
                    new CampaignRankingPreviewResponse.CampaignRankingRow();
            row.setCampaignId(candidate.getCampaignId());
            row.setCampaignName(candidate.getCampaignName());
            row.setMetricValue(candidate.getMetricValue());
            row.setRank(rank);
            row.setScore(buildRankingScore(rank, comparisonCount));

            rankingRows.add(row);
        }

        CampaignRankingPreviewResponse response = new CampaignRankingPreviewResponse();
        response.setSid(sid);
        response.setLogDate(logDate);
        response.setEntityLevel("campaign");
        response.setRuleType("ranking");
        response.setMetricKey(metricKey.getCode());
        response.setMetricName(metricKey.getName());
        response.setMetricDirection(metricKey.getDirection());
        response.setEffectDays(effectDays);
        response.setRawRowCount(sourceRows.size());
        response.setComparisonCount(comparisonCount);
        response.setExcludedNullCount(sourceRows.size() - comparisonCount);
        response.setRows(rankingRows);
        return response;
    }

    @Override
    public CampaignSmartBenchmarkPreviewResponse previewCampaignSmartBenchmark(
            Long sid,
            LocalDate logDate,
            ScoringMetricKey metricKey,
            Integer effectDays
    ) {
        List<CampaignMetricsMatrixItem> sourceRows =
                campaignMetricsMatrixMapper.selectAllMetricsMatrixBySidAndLogDate(sid, logDate);

        List<MetricCandidate> candidates = buildMetricCandidates(sourceRows, metricKey, effectDays);
        int comparisonCount = candidates.size();

        BigDecimal benchmarkValue = null;
        if (comparisonCount > 0) {
            benchmarkValue = metricKey.isHigherBetter()
                    ? candidates.stream().map(MetricCandidate::getMetricValue).max(BigDecimal::compareTo).orElse(null)
                    : candidates.stream().map(MetricCandidate::getMetricValue).min(BigDecimal::compareTo).orElse(null);
        }

        List<CampaignSmartBenchmarkPreviewResponse.CampaignSmartBenchmarkRow> rows = new ArrayList<>();
        if (benchmarkValue != null) {
            for (MetricCandidate candidate : candidates) {
                CampaignSmartBenchmarkPreviewResponse.CampaignSmartBenchmarkRow row =
                        new CampaignSmartBenchmarkPreviewResponse.CampaignSmartBenchmarkRow();
                row.setCampaignId(candidate.getCampaignId());
                row.setCampaignName(candidate.getCampaignName());
                row.setMetricValue(candidate.getMetricValue());
                row.setScore(buildSmartBenchmarkScore(candidate.getMetricValue(), benchmarkValue, metricKey));
                rows.add(row);
            }

            rows = rows.stream()
                    .sorted(
                            Comparator.comparing(
                                            CampaignSmartBenchmarkPreviewResponse.CampaignSmartBenchmarkRow::getScore,
                                            Comparator.nullsLast(BigDecimal::compareTo)
                                    )
                                    .reversed()
                                    .thenComparing(
                                            CampaignSmartBenchmarkPreviewResponse.CampaignSmartBenchmarkRow::getCampaignId,
                                            Comparator.nullsLast(Long::compareTo)
                                    )
                    )
                    .toList();
        }

        CampaignSmartBenchmarkPreviewResponse response = new CampaignSmartBenchmarkPreviewResponse();
        response.setSid(sid);
        response.setLogDate(logDate);
        response.setEntityLevel(ScoringEntityLevel.CAMPAIGN.getCode());
        response.setRuleType(ScoringRuleType.SMART_BENCHMARK.getCode());
        response.setMetricKey(metricKey.getCode());
        response.setMetricName(metricKey.getName());
        response.setMetricDirection(metricKey.getDirection());
        response.setEffectDays(effectDays);
        response.setBenchmarkValue(benchmarkValue);
        response.setRawRowCount(sourceRows.size());
        response.setComparisonCount(comparisonCount);
        response.setExcludedNullCount(sourceRows.size() - comparisonCount);
        response.setRows(rows);
        return response;
    }

    @Override
    public CampaignTargetValuePreviewResponse previewCampaignTargetValue(
            Long sid,
            LocalDate logDate,
            ScoringMetricKey metricKey,
            Integer effectDays,
            BigDecimal targetValue
    ) {
        List<CampaignMetricsMatrixItem> sourceRows =
                campaignMetricsMatrixMapper.selectAllMetricsMatrixBySidAndLogDate(sid, logDate);

        List<MetricCandidate> candidates = buildMetricCandidates(sourceRows, metricKey, effectDays);

        List<CampaignTargetValuePreviewResponse.CampaignTargetValueRow> rows = new ArrayList<>();
        for (MetricCandidate candidate : candidates) {
            CampaignTargetValuePreviewResponse.CampaignTargetValueRow row =
                    new CampaignTargetValuePreviewResponse.CampaignTargetValueRow();
            row.setCampaignId(candidate.getCampaignId());
            row.setCampaignName(candidate.getCampaignName());
            row.setMetricValue(candidate.getMetricValue());
            row.setScore(buildTargetValueScore(candidate.getMetricValue(), targetValue, metricKey));
            rows.add(row);
        }

        rows = rows.stream()
                .sorted(
                        Comparator.comparing(
                                        CampaignTargetValuePreviewResponse.CampaignTargetValueRow::getScore,
                                        Comparator.nullsLast(BigDecimal::compareTo)
                                )
                                .reversed()
                                .thenComparing(
                                        CampaignTargetValuePreviewResponse.CampaignTargetValueRow::getCampaignId,
                                        Comparator.nullsLast(Long::compareTo)
                                )
                )
                .toList();

        CampaignTargetValuePreviewResponse response = new CampaignTargetValuePreviewResponse();
        response.setSid(sid);
        response.setLogDate(logDate);
        response.setEntityLevel(ScoringEntityLevel.CAMPAIGN.getCode());
        response.setRuleType(ScoringRuleType.TARGET_VALUE.getCode());
        response.setMetricKey(metricKey.getCode());
        response.setMetricName(metricKey.getName());
        response.setMetricDirection(metricKey.getDirection());
        response.setEffectDays(effectDays);
        response.setTargetValue(targetValue);
        response.setRawRowCount(sourceRows.size());
        response.setComparisonCount(candidates.size());
        response.setExcludedNullCount(sourceRows.size() - candidates.size());
        response.setRows(rows);
        return response;
    }

    @Override
    public CampaignWeightedRankingPreviewResponse previewCampaignWeightedRanking(
            Long sid,
            LocalDate logDate,
            Integer effectDays,
            ScoringSchemeCreateRequest request
    ) {
        return buildCampaignWeightedScoringResponse(sid, logDate, effectDays, request);
    }

    @Override
    public AdgroupWeightedRankingPreviewResponse previewAdgroupWeightedRanking(
            Long sid,
            LocalDate logDate,
            Integer effectDays,
            ScoringSchemeCreateRequest request
    ) {
        return adgroupWeightedPreviewService.previewAdgroupWeightedRanking(sid, logDate, effectDays, request);
    }

    @Override
    public BidwordWeightedRankingPreviewResponse previewBidwordWeightedRanking(
            Long sid,
            LocalDate logDate,
            Integer effectDays,
            ScoringSchemeCreateRequest request
    ) {
        return bidwordWeightedPreviewService.previewBidwordWeightedRanking(sid, logDate, effectDays, request);
    }

    @Override
    public CampaignScoringResponse calculateCampaignScoring(
            Long sid,
            LocalDate logDate,
            Integer effectDays,
            ScoringSchemeCreateRequest request
    ) {
        return toCampaignScoringResponse(buildCampaignWeightedScoringResponse(sid, logDate, effectDays, request));
    }

    @Override
    public AdgroupScoringResponse calculateAdgroupScoring(
            Long sid,
            LocalDate logDate,
            Integer effectDays,
            ScoringSchemeCreateRequest request
    ) {
        return adgroupWeightedPreviewService.calculateAdgroupScoring(sid, logDate, effectDays, request);
    }

        @Override
    public BidwordScoringResponse calculateBidwordScoring(
            Long sid,
            LocalDate logDate,
            Integer effectDays,
            ScoringSchemeCreateRequest request
    ) {
        return bidwordWeightedPreviewService.calculateBidwordScoring(sid, logDate, effectDays, request);
    }


    private CampaignScoringResponse toCampaignScoringResponse(CampaignWeightedRankingPreviewResponse previewResponse) {
        CampaignScoringResponse response = new CampaignScoringResponse();
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

        List<CampaignScoringResponse.MetricSummary> metricSummaries = previewResponse.getMetricSummaries().stream()
                .map(this::toCampaignScoringMetricSummary)
                .toList();
        response.setMetricSummaries(metricSummaries);

        List<CampaignScoringResponse.CampaignScoringRow> rows = previewResponse.getRows().stream()
                .map(this::toCampaignScoringRow)
                .toList();
        response.setRows(rows);
        return response;
    }

    private CampaignScoringResponse.MetricSummary toCampaignScoringMetricSummary(
            CampaignWeightedRankingPreviewResponse.MetricSummary previewMetricSummary
    ) {
        CampaignScoringResponse.MetricSummary metricSummary = new CampaignScoringResponse.MetricSummary();
        metricSummary.setMetricKey(previewMetricSummary.getMetricKey());
        metricSummary.setMetricName(previewMetricSummary.getMetricName());
        metricSummary.setRuleType(previewMetricSummary.getRuleType());
        metricSummary.setWeight(previewMetricSummary.getWeight());
        metricSummary.setComparisonCount(previewMetricSummary.getComparisonCount());
        metricSummary.setExcludedNullCount(previewMetricSummary.getExcludedNullCount());
        metricSummary.setUsedInAggregation(previewMetricSummary.getUsedInAggregation());
        return metricSummary;
    }

    private CampaignScoringResponse.CampaignScoringRow toCampaignScoringRow(
            CampaignWeightedRankingPreviewResponse.CampaignWeightedRankingRow previewRow
    ) {
        CampaignScoringResponse.CampaignScoringRow row = new CampaignScoringResponse.CampaignScoringRow();
        row.setCampaignId(previewRow.getCampaignId());
        row.setCampaignName(previewRow.getCampaignName());
        row.setTotalScore(previewRow.getTotalScore());
        row.setParticipatingMetricCount(previewRow.getParticipatingMetricCount());
        row.setParticipatingWeightSum(previewRow.getParticipatingWeightSum());

        List<CampaignScoringResponse.MetricContribution> contributions = previewRow.getMetricContributions().stream()
                .map(this::toCampaignScoringMetricContribution)
                .toList();
        row.setMetricContributions(contributions);
        return row;
    }

    private CampaignScoringResponse.MetricContribution toCampaignScoringMetricContribution(
            CampaignWeightedRankingPreviewResponse.MetricContribution previewContribution
    ) {
        CampaignScoringResponse.MetricContribution contribution = new CampaignScoringResponse.MetricContribution();
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

    private CampaignWeightedRankingPreviewResponse buildCampaignWeightedScoringResponse(
            Long sid,
            LocalDate logDate,
            Integer effectDays,
            ScoringSchemeCreateRequest request
    ) {
        List<CampaignMetricsMatrixItem> sourceRows =
                campaignMetricsMatrixMapper.selectAllMetricsMatrixBySidAndLogDate(sid, logDate);

        ScoringLevelConfigRequest campaignLevelConfig = request.getLevelConfigs().stream()
                .filter(level -> ScoringEntityLevel.CAMPAIGN.getCode().equals(level.getEntityLevel()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("campaign level config is required"));

        List<WeightedMetricSpec> metricSpecs = campaignLevelConfig.getMetricConfigs().stream()
                .filter(metric -> Boolean.TRUE.equals(metric.getEnabled()))
                .filter(metric -> metric.getWeight() != null && metric.getWeight().compareTo(ZERO) > 0)
                .map(this::buildWeightedMetricSpec)
                .filter(Objects::nonNull)
                .toList();

        List<CampaignWeightedRankingPreviewResponse.MetricSummary> metricSummaries = new ArrayList<>();
        Map<Long, WeightedRowAccumulator> rowAccumulatorMap = new LinkedHashMap<>();

        for (WeightedMetricSpec metricSpec : metricSpecs) {
            List<MetricCandidate> candidates = buildMetricCandidates(sourceRows, metricSpec.getMetricKey(), effectDays);
            int comparisonCount = candidates.size();
            int excludedNullCount = sourceRows.size() - comparisonCount;
            boolean usedInAggregation = comparisonCount > 0 && metricSpec.getWeight().compareTo(ZERO) > 0;

            CampaignWeightedRankingPreviewResponse.MetricSummary metricSummary =
                    new CampaignWeightedRankingPreviewResponse.MetricSummary();
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

            Map<Long, MetricScoreSnapshot> scoreSnapshotByCampaignId =
                    buildMetricScoreSnapshotByCampaignId(metricSpec, candidates);

            for (MetricScoreSnapshot snapshot : scoreSnapshotByCampaignId.values()) {
                BigDecimal score = snapshot.getScore();
                if (score == null) {
                    continue;
                }
                BigDecimal weightedScore = score.multiply(metricSpec.getWeight());

                WeightedRowAccumulator accumulator = rowAccumulatorMap.computeIfAbsent(
                        snapshot.getCampaignId(),
                        key -> {
                            WeightedRowAccumulator value = new WeightedRowAccumulator();
                            value.setCampaignId(snapshot.getCampaignId());
                            value.setCampaignName(snapshot.getCampaignName());
                            value.setWeightedScoreSum(ZERO);
                            value.setParticipatingWeightSum(ZERO);
                            value.setMetricContributions(new ArrayList<>());
                            return value;
                        }
                );

                accumulator.setCampaignName(snapshot.getCampaignName());
                accumulator.setWeightedScoreSum(accumulator.getWeightedScoreSum().add(weightedScore));
                accumulator.setParticipatingWeightSum(accumulator.getParticipatingWeightSum().add(metricSpec.getWeight()));

                CampaignWeightedRankingPreviewResponse.MetricContribution contribution =
                        new CampaignWeightedRankingPreviewResponse.MetricContribution();
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

        List<CampaignWeightedRankingPreviewResponse.CampaignWeightedRankingRow> rows = rowAccumulatorMap.values().stream()
                .map(this::buildWeightedRankingRow)
                .sorted(
                        Comparator.comparing(
                                        CampaignWeightedRankingPreviewResponse.CampaignWeightedRankingRow::getTotalScore,
                                        Comparator.nullsLast(BigDecimal::compareTo)
                                )
                                .reversed()
                                .thenComparing(
                                        CampaignWeightedRankingPreviewResponse.CampaignWeightedRankingRow::getCampaignId,
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

        CampaignWeightedRankingPreviewResponse response = new CampaignWeightedRankingPreviewResponse();
        response.setSid(sid);
        response.setLogDate(logDate);
        response.setEntityLevel(ScoringEntityLevel.CAMPAIGN.getCode());
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

    private Map<Long, MetricScoreSnapshot> buildMetricScoreSnapshotByCampaignId(
            WeightedMetricSpec metricSpec,
            List<MetricCandidate> candidates
    ) {
        Map<Long, MetricScoreSnapshot> snapshotByCampaignId = new LinkedHashMap<>();
        if (candidates.isEmpty()) {
            return snapshotByCampaignId;
        }

        if (metricSpec.getRuleType() == ScoringRuleType.RANKING) {
            sortMetricCandidates(candidates, metricSpec.getMetricKey());
            int[] ranks = buildCompetitionRanks(candidates);
            for (int i = 0; i < candidates.size(); i++) {
                MetricCandidate candidate = candidates.get(i);
                MetricScoreSnapshot snapshot = new MetricScoreSnapshot();
                snapshot.setCampaignId(candidate.getCampaignId());
                snapshot.setCampaignName(candidate.getCampaignName());
                snapshot.setMetricValue(candidate.getMetricValue());
                snapshot.setRank(ranks[i]);
                snapshot.setScore(buildRankingScore(ranks[i], candidates.size()));
                snapshotByCampaignId.put(candidate.getCampaignId(), snapshot);
            }
            return snapshotByCampaignId;
        }

        BigDecimal smartBenchmark = null;
        if (metricSpec.getRuleType() == ScoringRuleType.SMART_BENCHMARK) {
            smartBenchmark = metricSpec.getMetricKey().isHigherBetter()
                    ? candidates.stream().map(MetricCandidate::getMetricValue).max(BigDecimal::compareTo).orElse(null)
                    : candidates.stream().map(MetricCandidate::getMetricValue).min(BigDecimal::compareTo).orElse(null);
        }

        for (MetricCandidate candidate : candidates) {
            MetricScoreSnapshot snapshot = new MetricScoreSnapshot();
            snapshot.setCampaignId(candidate.getCampaignId());
            snapshot.setCampaignName(candidate.getCampaignName());
            snapshot.setMetricValue(candidate.getMetricValue());

            if (metricSpec.getRuleType() == ScoringRuleType.TARGET_VALUE) {
                snapshot.setScore(
                        buildTargetValueScore(candidate.getMetricValue(), metricSpec.getTargetValue(), metricSpec.getMetricKey())
                );
            } else if (metricSpec.getRuleType() == ScoringRuleType.SMART_BENCHMARK) {
                snapshot.setScore(
                        buildSmartBenchmarkScore(candidate.getMetricValue(), smartBenchmark, metricSpec.getMetricKey())
                );
            }
            snapshotByCampaignId.put(candidate.getCampaignId(), snapshot);
        }
        return snapshotByCampaignId;
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

    private CampaignWeightedRankingPreviewResponse.CampaignWeightedRankingRow buildWeightedRankingRow(
            WeightedRowAccumulator accumulator
    ) {
        CampaignWeightedRankingPreviewResponse.CampaignWeightedRankingRow row =
                new CampaignWeightedRankingPreviewResponse.CampaignWeightedRankingRow();
        row.setCampaignId(accumulator.getCampaignId());
        row.setCampaignName(accumulator.getCampaignName());
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

    private List<MetricCandidate> buildMetricCandidates(
            List<CampaignMetricsMatrixItem> sourceRows,
            ScoringMetricKey metricKey,
            Integer effectDays
    ) {
        List<MetricCandidate> candidates = new ArrayList<>();
        for (CampaignMetricsMatrixItem item : sourceRows) {
            BigDecimal metricValue = extractMetricValue(item, metricKey, effectDays);
            if (metricValue == null) {
                continue;
            }

            MetricCandidate candidate = new MetricCandidate();
            candidate.setCampaignId(item.getCampaignId());
            candidate.setCampaignName(item.getCampaignName());
            candidate.setMetricValue(metricValue);
            candidates.add(candidate);
        }
        return candidates;
    }

    private void sortMetricCandidates(List<MetricCandidate> candidates, ScoringMetricKey metricKey) {
        Comparator<BigDecimal> metricValueComparator = metricKey.isHigherBetter()
                ? (left, right) -> right.compareTo(left)
                : BigDecimal::compareTo;

        candidates.sort(
                Comparator.comparing(MetricCandidate::getMetricValue, metricValueComparator)
                        .thenComparing(MetricCandidate::getCampaignId, Comparator.nullsLast(Long::compareTo))
        );
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

    private int[] buildCompetitionRanks(List<MetricCandidate> candidates) {
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
            CampaignMetricsMatrixItem item,
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

    private BigDecimal extract1dMetricValue(CampaignMetricsMatrixItem item, ScoringMetricKey metricKey) {
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

    private BigDecimal extract3dMetricValue(CampaignMetricsMatrixItem item, ScoringMetricKey metricKey) {
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

    private BigDecimal extract7dMetricValue(CampaignMetricsMatrixItem item, ScoringMetricKey metricKey) {
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

        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }

        public ScoringRuleType getRuleType() {
            return ruleType;
        }

        public void setRuleType(ScoringRuleType ruleType) {
            this.ruleType = ruleType;
        }

        public BigDecimal getTargetValue() {
            return targetValue;
        }

        public void setTargetValue(BigDecimal targetValue) {
            this.targetValue = targetValue;
        }
    }

    private static class MetricScoreSnapshot {

        private Long campaignId;
        private String campaignName;
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

    private static class MetricCandidate {

        private Long campaignId;
        private String campaignName;
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

        public BigDecimal getMetricValue() {
            return metricValue;
        }

        public void setMetricValue(BigDecimal metricValue) {
            this.metricValue = metricValue;
        }
    }

    private static class WeightedRowAccumulator {

        private Long campaignId;
        private String campaignName;
        private BigDecimal weightedScoreSum;
        private BigDecimal participatingWeightSum;
        private List<CampaignWeightedRankingPreviewResponse.MetricContribution> metricContributions;

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

        public List<CampaignWeightedRankingPreviewResponse.MetricContribution> getMetricContributions() {
            return metricContributions;
        }

        public void setMetricContributions(
                List<CampaignWeightedRankingPreviewResponse.MetricContribution> metricContributions
        ) {
            this.metricContributions = metricContributions;
        }
    }
}