package com.yunbian.adsscoring.scoring.service.impl;

import com.yunbian.adsscoring.scoring.dto.ScoringContractTemplateResponse;
import com.yunbian.adsscoring.scoring.enums.ScoringEntityLevel;
import com.yunbian.adsscoring.scoring.enums.ScoringMetricKey;
import com.yunbian.adsscoring.scoring.enums.ScoringRuleType;
import com.yunbian.adsscoring.scoring.request.ScoringLevelConfigRequest;
import com.yunbian.adsscoring.scoring.request.ScoringMetricConfigRequest;
import com.yunbian.adsscoring.scoring.request.ScoringSchemeBasicInfoRequest;
import com.yunbian.adsscoring.scoring.request.ScoringSchemeCreateRequest;
import com.yunbian.adsscoring.scoring.service.ScoringContractService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class ScoringContractServiceImpl implements ScoringContractService {

    @Override
    public ScoringContractTemplateResponse buildTemplate() {
        return new ScoringContractTemplateResponse(
                "scoring_contract_v2",
                buildRuleTypeOptions(),
                buildMetricDefinitions(),
                buildEntityLevelDefinitions(),
                buildSchemeTemplate(),
                buildCreateRequestTemplate()
        );
    }

    @Override
    public ScoringSchemeCreateRequest previewCreateRequest(ScoringSchemeCreateRequest request) {
        return request;
    }

    private List<ScoringContractTemplateResponse.RuleTypeOption> buildRuleTypeOptions() {
        return Arrays.stream(ScoringRuleType.values())
                .map(ruleType -> new ScoringContractTemplateResponse.RuleTypeOption(
                        ruleType.getCode(),
                        ruleType.getName(),
                        ruleType.isTargetValueRequired(),
                        ruleType.getDescription()
                ))
                .toList();
    }

    private List<ScoringContractTemplateResponse.MetricDefinition> buildMetricDefinitions() {
        return Arrays.stream(ScoringMetricKey.values())
                .map(metric -> new ScoringContractTemplateResponse.MetricDefinition(
                        metric.getCode(),
                        metric.getName(),
                        metric.getDirection(),
                        metric.getDataHint()
                ))
                .toList();
    }

    private List<ScoringContractTemplateResponse.EntityLevelDefinition> buildEntityLevelDefinitions() {
        return Arrays.stream(ScoringEntityLevel.values())
                .map(level -> new ScoringContractTemplateResponse.EntityLevelDefinition(
                        level.getCode(),
                        level.getName(),
                        level.getComparisonScope()
                ))
                .toList();
    }

    private ScoringContractTemplateResponse.SchemeTemplate buildSchemeTemplate() {
        List<ScoringContractTemplateResponse.LevelTemplate> levelTemplates = Arrays.stream(ScoringEntityLevel.values())
                .map(this::buildLevelTemplate)
                .toList();

        return new ScoringContractTemplateResponse.SchemeTemplate(
                "scheme_template_v2",
                "评分方案模板V2",
                "第二刀仅把评分方案创建请求结构收稳；当前仍不执行评分计算，也不落数据库",
                "DRAFT",
                levelTemplates
        );
    }

    private ScoringContractTemplateResponse.LevelTemplate buildLevelTemplate(ScoringEntityLevel level) {
        List<ScoringContractTemplateResponse.MetricRuleTemplate> metricRules = Arrays.stream(ScoringMetricKey.values())
                .map(this::buildMetricRuleTemplate)
                .toList();

        return new ScoringContractTemplateResponse.LevelTemplate(
                level.getCode(),
                level.getName(),
                level.getComparisonScope(),
                metricRules
        );
    }

    private ScoringContractTemplateResponse.MetricRuleTemplate buildMetricRuleTemplate(ScoringMetricKey metric) {
        return new ScoringContractTemplateResponse.MetricRuleTemplate(
                metric.getCode(),
                metric.getName(),
                metric.getDirection(),
                metric.getDataHint(),
                true,
                ScoringRuleType.RANKING.getCode(),
                new BigDecimal("1.0"),
                null,
                ScoringRuleType.RANKING.isTargetValueRequired()
        );
    }

    private ScoringSchemeCreateRequest buildCreateRequestTemplate() {
        ScoringSchemeCreateRequest request = new ScoringSchemeCreateRequest();
        request.setBasicInfo(buildBasicInfoRequest());
        request.setLevelConfigs(Arrays.stream(ScoringEntityLevel.values())
                .map(this::buildLevelConfigRequest)
                .toList());
        return request;
    }

    private ScoringSchemeBasicInfoRequest buildBasicInfoRequest() {
        ScoringSchemeBasicInfoRequest basicInfo = new ScoringSchemeBasicInfoRequest();
        basicInfo.setSchemeCode("scheme_demo_v1");
        basicInfo.setSchemeName("评分方案演示版V1");
        basicInfo.setDescription("创建评分方案时的请求结构示例；当前只定义契约，不做评分计算");
        basicInfo.setStatus("DRAFT");
        return basicInfo;
    }

    private ScoringLevelConfigRequest buildLevelConfigRequest(ScoringEntityLevel level) {
        ScoringLevelConfigRequest levelConfig = new ScoringLevelConfigRequest();
        levelConfig.setEntityLevel(level.getCode());
        levelConfig.setMetricConfigs(Arrays.stream(ScoringMetricKey.values())
                .map(this::buildMetricConfigRequest)
                .toList());
        return levelConfig;
    }

    private ScoringMetricConfigRequest buildMetricConfigRequest(ScoringMetricKey metric) {
        ScoringMetricConfigRequest metricConfig = new ScoringMetricConfigRequest();
        metricConfig.setMetricKey(metric.getCode());
        metricConfig.setEnabled(Boolean.TRUE);
        metricConfig.setRuleType(ScoringRuleType.RANKING.getCode());
        metricConfig.setWeight(new BigDecimal("1.0"));
        metricConfig.setTargetValue(null);
        return metricConfig;
    }
}