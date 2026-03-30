package com.yunbian.adsscoring.scoring.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ScoringSchemeCreateRequest {

    @NotNull(message = "basicInfo must not be null")
    @Valid
    private ScoringSchemeBasicInfoRequest basicInfo;

    @NotEmpty(message = "levelConfigs must not be empty")
    @Size(min = 3, max = 3, message = "levelConfigs must contain exactly 3 levels")
    @Valid
    private List<ScoringLevelConfigRequest> levelConfigs;

    public ScoringSchemeBasicInfoRequest getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(ScoringSchemeBasicInfoRequest basicInfo) {
        this.basicInfo = basicInfo;
    }

    public List<ScoringLevelConfigRequest> getLevelConfigs() {
        return levelConfigs;
    }

    public void setLevelConfigs(List<ScoringLevelConfigRequest> levelConfigs) {
        this.levelConfigs = levelConfigs;
    }
}