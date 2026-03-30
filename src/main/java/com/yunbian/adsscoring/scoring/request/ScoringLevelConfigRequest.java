package com.yunbian.adsscoring.scoring.request;

import com.yunbian.adsscoring.scoring.enums.ScoringEntityLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Arrays;
import java.util.List;

public class ScoringLevelConfigRequest {

    @NotBlank(message = "entityLevel must not be blank")
    private String entityLevel;

    @NotEmpty(message = "metricConfigs must not be empty")
    @Size(min = 7, max = 7, message = "metricConfigs must contain exactly 7 metrics")
    @Valid
    private List<ScoringMetricConfigRequest> metricConfigs;

    public String getEntityLevel() {
        return entityLevel;
    }

    public void setEntityLevel(String entityLevel) {
        this.entityLevel = entityLevel;
    }

    public List<ScoringMetricConfigRequest> getMetricConfigs() {
        return metricConfigs;
    }

    public void setMetricConfigs(List<ScoringMetricConfigRequest> metricConfigs) {
        this.metricConfigs = metricConfigs;
    }

    @AssertTrue(message = "entityLevel is invalid, allowed values: campaign, adgroup, bidword")
    public boolean isEntityLevelValid() {
        if (entityLevel == null || entityLevel.isBlank()) {
            return true;
        }
        return Arrays.stream(ScoringEntityLevel.values())
                .anyMatch(item -> item.getCode().equals(entityLevel));
    }
}