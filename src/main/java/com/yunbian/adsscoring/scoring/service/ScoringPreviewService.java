package com.yunbian.adsscoring.scoring.service;

import com.yunbian.adsscoring.scoring.dto.CampaignRankingPreviewResponse;
import com.yunbian.adsscoring.scoring.dto.CampaignScoringResponse;
import com.yunbian.adsscoring.scoring.dto.CampaignTargetValuePreviewResponse;
import com.yunbian.adsscoring.scoring.dto.CampaignSmartBenchmarkPreviewResponse;
import com.yunbian.adsscoring.scoring.dto.CampaignWeightedRankingPreviewResponse;
import com.yunbian.adsscoring.scoring.dto.AdgroupWeightedRankingPreviewResponse;
import com.yunbian.adsscoring.scoring.enums.ScoringMetricKey;
import com.yunbian.adsscoring.scoring.request.ScoringSchemeCreateRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ScoringPreviewService {

    CampaignRankingPreviewResponse previewCampaignRanking(
            Long sid,
            LocalDate logDate,
            ScoringMetricKey metricKey,
            Integer effectDays
    );

    CampaignWeightedRankingPreviewResponse previewCampaignWeightedRanking(
            Long sid,
            LocalDate logDate,
            Integer effectDays,
            ScoringSchemeCreateRequest request
    );


    AdgroupWeightedRankingPreviewResponse previewAdgroupWeightedRanking(
            Long sid,
            LocalDate logDate,
            Integer effectDays,
            ScoringSchemeCreateRequest request
    );

    CampaignScoringResponse calculateCampaignScoring(
            Long sid,
            LocalDate logDate,
            Integer effectDays,
            ScoringSchemeCreateRequest request
    );


    CampaignSmartBenchmarkPreviewResponse previewCampaignSmartBenchmark(
            Long sid,
            LocalDate logDate,
            ScoringMetricKey metricKey,
            Integer effectDays
    );

    CampaignTargetValuePreviewResponse previewCampaignTargetValue(
            Long sid,
            LocalDate logDate,
            ScoringMetricKey metricKey,
            Integer effectDays,
            BigDecimal targetValue
    );
}