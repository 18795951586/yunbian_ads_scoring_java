// 文件路径: src/main/java/com/yunbian/adsscoring/scoring/dto/algorithm/AlgorithmTemplateDefinitionAggregate.java
package com.yunbian.adsscoring.scoring.dto.algorithm;

import java.util.List;

public class AlgorithmTemplateDefinitionAggregate {

    private AlgorithmTemplate template;
    private List<AlgorithmCoreIndicator> coreIndicators;

    public AlgorithmTemplate getTemplate() {
        return template;
    }

    public void setTemplate(AlgorithmTemplate template) {
        this.template = template;
    }

    public List<AlgorithmCoreIndicator> getCoreIndicators() {
        return coreIndicators;
    }

    public void setCoreIndicators(List<AlgorithmCoreIndicator> coreIndicators) {
        this.coreIndicators = coreIndicators;
    }
}