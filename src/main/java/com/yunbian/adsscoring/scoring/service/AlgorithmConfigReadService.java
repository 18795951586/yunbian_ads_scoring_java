// 文件路径: src/main/java/com/yunbian/adsscoring/scoring/service/AlgorithmConfigReadService.java
package com.yunbian.adsscoring.scoring.service;

import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmConfigBindingAggregate;
import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmTemplateDefinitionAggregate;

import java.util.List;

public interface AlgorithmConfigReadService {

    List<AlgorithmConfigBindingAggregate> readConfigBindings(Long enterpriseId, Long sid, String businessType);

    List<AlgorithmTemplateDefinitionAggregate> readTemplateDefinitions(List<Long> algorithmTemplateIds);
}