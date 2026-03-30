package com.yunbian.adsscoring.scoring.controller;

import com.yunbian.adsscoring.common.ApiResponse;
import com.yunbian.adsscoring.scoring.dto.CampaignRankingPreviewResponse;
import com.yunbian.adsscoring.scoring.dto.CampaignWeightedRankingPreviewResponse;
import com.yunbian.adsscoring.scoring.enums.ScoringEntityLevel;
import com.yunbian.adsscoring.scoring.enums.ScoringMetricKey;
import com.yunbian.adsscoring.scoring.enums.ScoringRuleType;
import com.yunbian.adsscoring.scoring.request.ScoringLevelConfigRequest;
import com.yunbian.adsscoring.scoring.request.ScoringMetricConfigRequest;
import com.yunbian.adsscoring.scoring.request.ScoringSchemeCreateRequest;
import com.yunbian.adsscoring.scoring.service.ScoringPreviewService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/scoring")
public class ScoringPreviewController {

    @Resource
    private ScoringPreviewService scoringPreviewService;

    @GetMapping("/preview/campaign-ranking")
    public ApiResponse<?> previewCampaignRanking(
            @RequestParam("sid") @NotNull Long sid,
            @RequestParam("logDate") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate,
            @RequestParam("metricKey") String metricKey,
            @RequestParam(value = "effectDays", required = false, defaultValue = "1") Integer effectDays
    ) {
        ScoringMetricKey scoringMetricKey = ScoringMetricKey.fromCode(metricKey);
        if (scoringMetricKey == null) {
            return ApiResponse.failure(
                    "VALIDATION_ERROR",
                    "metricKey must be one of roi, cvr, cpc, cart_cost, deal_new_customer_ratio, new_customer_ratio, direct_deal_ratio"
            );
        }

        if (effectDays == null || (effectDays != 1 && effectDays != 3 && effectDays != 7)) {
            return ApiResponse.failure("VALIDATION_ERROR", "effectDays must be one of 1, 3, 7");
        }

        CampaignRankingPreviewResponse response =
                scoringPreviewService.previewCampaignRanking(sid, logDate, scoringMetricKey, effectDays);

        return ApiResponse.success(response);
    }

    @PostMapping("/preview/campaign-weighted-ranking")
    public ApiResponse<?> previewCampaignWeightedRanking(
            @RequestParam("sid") @NotNull Long sid,
            @RequestParam("logDate") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate,
            @RequestParam(value = "effectDays", required = false, defaultValue = "1") Integer effectDays,
            @Valid @RequestBody ScoringSchemeCreateRequest request
    ) {
        if (effectDays == null || (effectDays != 1 && effectDays != 3 && effectDays != 7)) {
            return ApiResponse.failure("VALIDATION_ERROR", "effectDays must be one of 1, 3, 7");
        }

        if (!hasAtLeastOneEnabledRankingMetricOnCampaign(request)) {
            return ApiResponse.failure(
                    "VALIDATION_ERROR",
                    "campaign level must contain at least one enabled metric with ruleType=ranking and weight > 0"
            );
        }

        CampaignWeightedRankingPreviewResponse response =
                scoringPreviewService.previewCampaignWeightedRanking(sid, logDate, effectDays, request);

        return ApiResponse.success(response);
    }

    private boolean hasAtLeastOneEnabledRankingMetricOnCampaign(ScoringSchemeCreateRequest request) {
        for (ScoringLevelConfigRequest levelConfig : request.getLevelConfigs()) {
            if (!ScoringEntityLevel.CAMPAIGN.getCode().equals(levelConfig.getEntityLevel())) {
                continue;
            }

            for (ScoringMetricConfigRequest metricConfig : levelConfig.getMetricConfigs()) {
                if (!Boolean.TRUE.equals(metricConfig.getEnabled())) {
                    continue;
                }
                if (!ScoringRuleType.RANKING.getCode().equals(metricConfig.getRuleType())) {
                    continue;
                }
                if (metricConfig.getWeight() == null) {
                    continue;
                }
                if (metricConfig.getWeight().compareTo(BigDecimal.ZERO) > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}