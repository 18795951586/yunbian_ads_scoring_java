package com.yunbian.adsscoring.scoring.dto;

import com.yunbian.adsscoring.scoring.request.ScoringSchemeCreateRequest;

import java.util.List;

public record ScoringContractTemplateResponse(
        String contractVersion,
        List<RuleTypeOption> availableRuleTypes,
        List<MetricDefinition> availableMetrics,
        List<EntityLevelDefinition> availableEntityLevels,
        ScoringSchemeCreateRequest createRequestTemplate
) {

    public record RuleTypeOption(
            String code,
            String name,
            boolean targetValueRequired,
            String description
    ) {
    }

    public record MetricDefinition(
            String metricKey,
            String metricName,
            String direction,
            String dataHint
    ) {
    }

    public record EntityLevelDefinition(
            String entityLevel,
            String entityName,
            String comparisonScope
    ) {
    }
}
