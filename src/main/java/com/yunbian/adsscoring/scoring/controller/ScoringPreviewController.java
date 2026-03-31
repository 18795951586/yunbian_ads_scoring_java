package com.yunbian.adsscoring.scoring.controller;

import com.yunbian.adsscoring.common.ApiResponse;
import com.yunbian.adsscoring.scoring.dto.CampaignRankingPreviewResponse;
import com.yunbian.adsscoring.scoring.dto.CampaignTargetValuePreviewResponse;
import com.yunbian.adsscoring.scoring.dto.CampaignSmartBenchmarkPreviewResponse;
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
import java.util.List;

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

        if (effectDays != 1 && effectDays != 3 && effectDays != 7) {
            return ApiResponse.failure("VALIDATION_ERROR", "effectDays must be one of 1, 3, 7");
        }

        CampaignRankingPreviewResponse response =
                scoringPreviewService.previewCampaignRanking(sid, logDate, scoringMetricKey, effectDays);

        return ApiResponse.success(response);
    }


    @PostMapping("/preview/campaign-target-value")
    public ApiResponse<?> previewCampaignTargetValue(
            @RequestParam("sid") @NotNull Long sid,
            @RequestParam("logDate") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate,
            @RequestParam(value = "effectDays", required = false, defaultValue = "1") Integer effectDays,
            @Valid @RequestBody ScoringSchemeCreateRequest request
    ) {
        if (effectDays != 1 && effectDays != 3 && effectDays != 7) {
            return ApiResponse.failure("VALIDATION_ERROR", "effectDays must be one of 1, 3, 7");
        }

        TargetValuePreviewSpec targetValuePreviewSpec = buildCampaignSingleTargetValueSpec(request);
        if (targetValuePreviewSpec.getValidationError() != null) {
            return ApiResponse.failure("VALIDATION_ERROR", targetValuePreviewSpec.getValidationError());
        }

        CampaignTargetValuePreviewResponse response = scoringPreviewService.previewCampaignTargetValue(
                sid,
                logDate,
                targetValuePreviewSpec.getMetricKey(),
                effectDays,
                targetValuePreviewSpec.getTargetValue()
        );

        return ApiResponse.success(response);
    }


    @PostMapping("/preview/campaign-smart-benchmark")
    public ApiResponse<?> previewCampaignSmartBenchmark(
            @RequestParam("sid") @NotNull Long sid,
            @RequestParam("logDate") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate,
            @RequestParam(value = "effectDays", required = false, defaultValue = "1") Integer effectDays,
            @Valid @RequestBody ScoringSchemeCreateRequest request
    ) {
        if (effectDays != 1 && effectDays != 3 && effectDays != 7) {
            return ApiResponse.failure("VALIDATION_ERROR", "effectDays must be one of 1, 3, 7");
        }

        SmartBenchmarkPreviewSpec smartBenchmarkPreviewSpec = buildCampaignSingleSmartBenchmarkSpec(request);
        if (smartBenchmarkPreviewSpec.getValidationError() != null) {
            return ApiResponse.failure("VALIDATION_ERROR", smartBenchmarkPreviewSpec.getValidationError());
        }

        CampaignSmartBenchmarkPreviewResponse response = scoringPreviewService.previewCampaignSmartBenchmark(
                sid,
                logDate,
                smartBenchmarkPreviewSpec.getMetricKey(),
                effectDays
        );

        return ApiResponse.success(response);
    }

    @PostMapping("/preview/campaign-weighted-ranking")
    public ApiResponse<?> previewCampaignWeightedRanking(
            @RequestParam("sid") @NotNull Long sid,
            @RequestParam("logDate") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate,
            @RequestParam(value = "effectDays", required = false, defaultValue = "1") Integer effectDays,
            @Valid @RequestBody ScoringSchemeCreateRequest request
    ) {
        if (effectDays != 1 && effectDays != 3 && effectDays != 7) {
            return ApiResponse.failure("VALIDATION_ERROR", "effectDays must be one of 1, 3, 7");
        }

        String validationError = validateCampaignWeightedPreviewRequest(request);
        if (validationError != null) {
            return ApiResponse.failure("VALIDATION_ERROR", validationError);
        }

        CampaignWeightedRankingPreviewResponse response =
                scoringPreviewService.previewCampaignWeightedRanking(sid, logDate, effectDays, request);

        return ApiResponse.success(response);
    }

    @PostMapping("/calculate/campaign")
    public ApiResponse<?> calculateCampaignScoring(
            @RequestParam("sid") @NotNull Long sid,
            @RequestParam("logDate") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate,
            @RequestParam(value = "effectDays", required = false, defaultValue = "1") Integer effectDays,
            @Valid @RequestBody ScoringSchemeCreateRequest request
    ) {
        if (effectDays != 1 && effectDays != 3 && effectDays != 7) {
            return ApiResponse.failure("VALIDATION_ERROR", "effectDays must be one of 1, 3, 7");
        }

        String validationError = validateCampaignWeightedPreviewRequest(request);
        if (validationError != null) {
            return ApiResponse.failure("VALIDATION_ERROR", validationError);
        }

        CampaignWeightedRankingPreviewResponse response =
                scoringPreviewService.calculateCampaignScoring(sid, logDate, effectDays, request);

        return ApiResponse.success(response);
    }

    private String validateCampaignWeightedPreviewRequest(ScoringSchemeCreateRequest request) {
        ScoringLevelConfigRequest campaignLevel = null;
        for (ScoringLevelConfigRequest levelConfig : request.getLevelConfigs()) {
            if (ScoringEntityLevel.CAMPAIGN.getCode().equals(levelConfig.getEntityLevel())) {
                campaignLevel = levelConfig;
                break;
            }
        }

        if (campaignLevel == null) {
            return "campaign level config is required";
        }

        int validEnabledMetricCount = 0;
        for (ScoringMetricConfigRequest metricConfig : campaignLevel.getMetricConfigs()) {
            if (!Boolean.TRUE.equals(metricConfig.getEnabled())) {
                continue;
            }

            ScoringRuleType ruleType;
            try {
                ruleType = ScoringRuleType.fromCode(metricConfig.getRuleType());
            } catch (IllegalArgumentException ex) {
                return "enabled campaign metric ruleType must be one of ranking, target_value, smart_benchmark";
            }
            if (ruleType != ScoringRuleType.RANKING
                    && ruleType != ScoringRuleType.TARGET_VALUE
                    && ruleType != ScoringRuleType.SMART_BENCHMARK) {
                return "enabled campaign metric ruleType must be one of ranking, target_value, smart_benchmark";
            }

            if (metricConfig.getWeight() == null || metricConfig.getWeight().compareTo(BigDecimal.ZERO) <= 0) {
                return "enabled campaign metric weight must be greater than 0";
            }

            if (ruleType == ScoringRuleType.TARGET_VALUE
                    && (metricConfig.getTargetValue() == null
                    || metricConfig.getTargetValue().compareTo(BigDecimal.ZERO) <= 0)) {
                return "enabled target_value campaign metric targetValue must be greater than 0";
            }

            validEnabledMetricCount++;
        }

        if (validEnabledMetricCount <= 0) {
            return "campaign level must contain at least one enabled metric with weight > 0";
        }

        return null;
    }

    private TargetValuePreviewSpec buildCampaignSingleTargetValueSpec(ScoringSchemeCreateRequest request) {
        ScoringLevelConfigRequest campaignLevel = null;
        for (ScoringLevelConfigRequest levelConfig : request.getLevelConfigs()) {
            if (ScoringEntityLevel.CAMPAIGN.getCode().equals(levelConfig.getEntityLevel())) {
                campaignLevel = levelConfig;
                break;
            }
        }

        if (campaignLevel == null) {
            return TargetValuePreviewSpec.error("campaign level config is required");
        }

        List<ScoringMetricConfigRequest> enabledMetrics = campaignLevel.getMetricConfigs().stream()
                .filter(metric -> Boolean.TRUE.equals(metric.getEnabled()))
                .toList();

        if (enabledMetrics.size() != 1) {
            return TargetValuePreviewSpec.error(
                    "campaign level must contain exactly one enabled metric for target_value preview"
            );
        }

        ScoringMetricConfigRequest enabledMetric = enabledMetrics.get(0);

        if (!ScoringRuleType.TARGET_VALUE.getCode().equals(enabledMetric.getRuleType())) {
            return TargetValuePreviewSpec.error(
                    "the only enabled campaign metric must use ruleType=target_value"
            );
        }

        if (enabledMetric.getTargetValue() == null || enabledMetric.getTargetValue().compareTo(BigDecimal.ZERO) <= 0) {
            return TargetValuePreviewSpec.error(
                    "targetValue of the enabled campaign metric must be greater than 0"
            );
        }

        ScoringMetricKey metricKey = ScoringMetricKey.fromCode(enabledMetric.getMetricKey());
        if (metricKey == null) {
            return TargetValuePreviewSpec.error("the enabled campaign metricKey is invalid");
        }

        TargetValuePreviewSpec spec = new TargetValuePreviewSpec();
        spec.setMetricKey(metricKey);
        spec.setTargetValue(enabledMetric.getTargetValue());
        return spec;
    }


    private SmartBenchmarkPreviewSpec buildCampaignSingleSmartBenchmarkSpec(ScoringSchemeCreateRequest request) {
        ScoringLevelConfigRequest campaignLevel = null;
        for (ScoringLevelConfigRequest levelConfig : request.getLevelConfigs()) {
            if (ScoringEntityLevel.CAMPAIGN.getCode().equals(levelConfig.getEntityLevel())) {
                campaignLevel = levelConfig;
                break;
            }
        }

        if (campaignLevel == null) {
            return SmartBenchmarkPreviewSpec.error("campaign level config is required");
        }

        List<ScoringMetricConfigRequest> enabledMetrics = campaignLevel.getMetricConfigs().stream()
                .filter(metric -> Boolean.TRUE.equals(metric.getEnabled()))
                .toList();

        if (enabledMetrics.size() != 1) {
            return SmartBenchmarkPreviewSpec.error(
                    "campaign level must contain exactly one enabled metric for smart_benchmark preview"
            );
        }

        ScoringMetricConfigRequest enabledMetric = enabledMetrics.get(0);
        if (!ScoringRuleType.SMART_BENCHMARK.getCode().equals(enabledMetric.getRuleType())) {
            return SmartBenchmarkPreviewSpec.error(
                    "the only enabled campaign metric must use ruleType=smart_benchmark"
            );
        }

        ScoringMetricKey metricKey = ScoringMetricKey.fromCode(enabledMetric.getMetricKey());
        if (metricKey == null) {
            return SmartBenchmarkPreviewSpec.error("the enabled campaign metricKey is invalid");
        }

        SmartBenchmarkPreviewSpec spec = new SmartBenchmarkPreviewSpec();
        spec.setMetricKey(metricKey);
        return spec;
    }

    private static class SmartBenchmarkPreviewSpec {
        private ScoringMetricKey metricKey;
        private String validationError;

        static SmartBenchmarkPreviewSpec error(String validationError) {
            SmartBenchmarkPreviewSpec spec = new SmartBenchmarkPreviewSpec();
            spec.setValidationError(validationError);
            return spec;
        }

        public ScoringMetricKey getMetricKey() {
            return metricKey;
        }

        public void setMetricKey(ScoringMetricKey metricKey) {
            this.metricKey = metricKey;
        }

        public String getValidationError() {
            return validationError;
        }

        public void setValidationError(String validationError) {
            this.validationError = validationError;
        }
    }

    private static class TargetValuePreviewSpec {
        private ScoringMetricKey metricKey;
        private BigDecimal targetValue;
        private String validationError;

        static TargetValuePreviewSpec error(String validationError) {
            TargetValuePreviewSpec spec = new TargetValuePreviewSpec();
            spec.setValidationError(validationError);
            return spec;
        }

        public ScoringMetricKey getMetricKey() {
            return metricKey;
        }

        public void setMetricKey(ScoringMetricKey metricKey) {
            this.metricKey = metricKey;
        }

        public BigDecimal getTargetValue() {
            return targetValue;
        }

        public void setTargetValue(BigDecimal targetValue) {
            this.targetValue = targetValue;
        }

        public String getValidationError() {
            return validationError;
        }

        public void setValidationError(String validationError) {
            this.validationError = validationError;
        }
    }

}