// 文件路径: src/main/java/com/yunbian/adsscoring/scoring/dto/algorithm/AlgorithmConfigBindingAggregate.java
package com.yunbian.adsscoring.scoring.dto.algorithm;

public class AlgorithmConfigBindingAggregate {

    private AlgorithmScoreConfig scoreConfig;
    private AlgorithmTemplateDefinitionAggregate templateDefinition;

    public AlgorithmScoreConfig getScoreConfig() {
        return scoreConfig;
    }

    public void setScoreConfig(AlgorithmScoreConfig scoreConfig) {
        this.scoreConfig = scoreConfig;
    }

    public AlgorithmTemplateDefinitionAggregate getTemplateDefinition() {
        return templateDefinition;
    }

    public void setTemplateDefinition(AlgorithmTemplateDefinitionAggregate templateDefinition) {
        this.templateDefinition = templateDefinition;
    }
}