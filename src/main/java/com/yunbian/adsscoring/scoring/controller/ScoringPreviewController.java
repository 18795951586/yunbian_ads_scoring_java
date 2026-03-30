package com.yunbian.adsscoring.scoring.controller;

import com.yunbian.adsscoring.common.ApiResponse;
import com.yunbian.adsscoring.scoring.dto.CampaignRankingPreviewResponse;
import com.yunbian.adsscoring.scoring.enums.ScoringMetricKey;
import com.yunbian.adsscoring.scoring.service.ScoringPreviewService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}