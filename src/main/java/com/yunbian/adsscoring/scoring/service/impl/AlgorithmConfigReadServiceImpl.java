package com.yunbian.adsscoring.scoring.service.impl;

import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmConfigBindingAggregate;
import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmCoreIndicator;
import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmScoreConfig;
import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmTemplate;
import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmTemplateDefinitionAggregate;
import com.yunbian.adsscoring.scoring.mapper.AlgorithmCoreIndicatorMapper;
import com.yunbian.adsscoring.scoring.mapper.AlgorithmScoreConfigMapper;
import com.yunbian.adsscoring.scoring.mapper.AlgorithmTemplateMapper;
import com.yunbian.adsscoring.scoring.service.AlgorithmConfigReadService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
public class AlgorithmConfigReadServiceImpl implements AlgorithmConfigReadService {

    @Resource
    private AlgorithmScoreConfigMapper algorithmScoreConfigMapper;

    @Resource
    private AlgorithmTemplateMapper algorithmTemplateMapper;

    @Resource
    private AlgorithmCoreIndicatorMapper algorithmCoreIndicatorMapper;

    @Override
    public List<AlgorithmConfigBindingAggregate> readConfigBindings(Long enterpriseId, Long sid, String businessType) {
        if (enterpriseId == null || sid == null || businessType == null || businessType.isBlank()) {
            return Collections.emptyList();
        }

        String normalizedBusinessType = businessType.toLowerCase(Locale.ROOT);

        List<AlgorithmScoreConfig> scoreConfigs = algorithmScoreConfigMapper
                .selectByEnterpriseAndSidAndBusinessType(enterpriseId, sid, normalizedBusinessType);
        if (scoreConfigs == null || scoreConfigs.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> algorithmTemplateIds = scoreConfigs.stream()
                .map(AlgorithmScoreConfig::getAlgorithmTemplateId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, AlgorithmTemplateDefinitionAggregate> templateDefinitionByTemplateId = new LinkedHashMap<>();
        List<AlgorithmTemplateDefinitionAggregate> templateDefinitions = readTemplateDefinitions(algorithmTemplateIds);
        for (AlgorithmTemplateDefinitionAggregate templateDefinition : templateDefinitions) {
            AlgorithmTemplate template = templateDefinition.getTemplate();
            if (template != null && template.getId() != null) {
                templateDefinitionByTemplateId.put(template.getId(), templateDefinition);
            }
        }

        List<AlgorithmConfigBindingAggregate> result = new ArrayList<>();
        for (AlgorithmScoreConfig scoreConfig : scoreConfigs) {
            AlgorithmConfigBindingAggregate aggregate = new AlgorithmConfigBindingAggregate();
            aggregate.setScoreConfig(scoreConfig);
            aggregate.setTemplateDefinition(
                    templateDefinitionByTemplateId.get(scoreConfig.getAlgorithmTemplateId())
            );
            result.add(aggregate);
        }

        return result;
    }

    @Override
    public List<AlgorithmTemplateDefinitionAggregate> readTemplateDefinitions(List<Long> algorithmTemplateIds) {
        if (algorithmTemplateIds == null || algorithmTemplateIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<AlgorithmTemplate> templates = algorithmTemplateMapper.selectByAlgorithmTemplateIds(algorithmTemplateIds);
        if (templates == null || templates.isEmpty()) {
            return Collections.emptyList();
        }

        List<AlgorithmCoreIndicator> indicators = algorithmCoreIndicatorMapper
                .selectByAlgorithmTemplateIds(algorithmTemplateIds);
        Map<Long, List<AlgorithmCoreIndicator>> indicatorByTemplateId = new LinkedHashMap<>();
        if (indicators != null) {
            for (AlgorithmCoreIndicator indicator : indicators) {
                if (indicator.getTemplateId() == null) {
                    continue;
                }
                indicatorByTemplateId
                        .computeIfAbsent(indicator.getTemplateId(), key -> new ArrayList<>())
                        .add(indicator);
            }
        }

        List<AlgorithmTemplateDefinitionAggregate> result = new ArrayList<>();
        for (AlgorithmTemplate template : templates) {
            AlgorithmTemplateDefinitionAggregate aggregate = new AlgorithmTemplateDefinitionAggregate();
            aggregate.setTemplate(template);
            aggregate.setCoreIndicators(
                    indicatorByTemplateId.getOrDefault(template.getId(), Collections.emptyList())
            );
            result.add(aggregate);
        }

        return result;
    }
}
