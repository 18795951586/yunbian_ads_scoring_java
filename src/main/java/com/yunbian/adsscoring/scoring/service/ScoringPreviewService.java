package com.yunbian.adsscoring.scoring.service;

import com.yunbian.adsscoring.scoring.dto.CampaignRankingPreviewResponse;
import com.yunbian.adsscoring.scoring.enums.ScoringMetricKey;

import java.time.LocalDate;

public interface ScoringPreviewService {

    CampaignRankingPreviewResponse previewCampaignRanking(
            Long sid,
            LocalDate logDate,
            ScoringMetricKey metricKey,
            Integer effectDays
    );
}