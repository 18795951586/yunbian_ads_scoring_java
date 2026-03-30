package com.yunbian.adsscoring.scoring.dto;

import java.math.BigDecimal;
import java.util.List;

public record ScoringSchemeCreatePreviewResponse(
        String contractVersion,
        BasicInfoPreview basicInfo,
        List<LevelPreview> levelConfigs,
        int totalLevelCount,
        int totalMetricCount,
        int enabledMetricCount
) {

    public record BasicInfoPreview(
            String schemeCode,
            String schemeName,
            String description,
            String status
    ) {
    }

    public record LevelPreview(
            String entityLevel,
            String entityName,
            String comparisonScope,
            int metricCount,
            int enabledMetricCount,
            List<MetricPreview> metricConfigs
    ) {
    }

    public record MetricPreview(
            String metricKey,
            String metricName,
            String direction,
            String dataHint,
            boolean enabled,
            String ruleType,
            String ruleName,
            boolean targetValueRequired,
            BigDecimal weight,
            BigDecimal targetValue
    ) {
    }
}