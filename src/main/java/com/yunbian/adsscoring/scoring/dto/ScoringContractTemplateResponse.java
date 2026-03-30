package com.yunbian.adsscoring.scoring.dto;

import com.yunbian.adsscoring.scoring.request.ScoringSchemeCreateRequest;

import java.math.BigDecimal;
import java.util.List;

public record ScoringContractTemplateResponse(
        String contractVersion,
        List<RuleTypeOption> availableRuleTypes,
        List<MetricDefinition> availableMetrics,
        List<EntityLevelDefinition> availableEntityLevels,
        SchemeTemplate templateScheme,
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

    public record SchemeTemplate(
            String schemeCode,
            String schemeName,
            String description,
            String status,
            List<LevelTemplate> levelConfigs
    ) {
    }

    public record LevelTemplate(
            String entityLevel,
            String entityName,
            String comparisonScope,
            List<MetricRuleTemplate> metricRules
    ) {
    }

    public record MetricRuleTemplate(
            String metricKey,
            String metricName,
            String direction,
            String dataHint,
            boolean enabled,
            String ruleType,
            BigDecimal weight,
            BigDecimal targetValue,
            boolean targetValueRequired
    ) {
    }
}