package com.yunbian.adsscoring.scoring.service;

import com.yunbian.adsscoring.scoring.dto.CampaignRankingPreviewResponse;
import com.yunbian.adsscoring.scoring.dto.CampaignWeightedRankingPreviewResponse;
import com.yunbian.adsscoring.scoring.enums.ScoringMetricKey;
import com.yunbian.adsscoring.scoring.request.ScoringSchemeCreateRequest;

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
}