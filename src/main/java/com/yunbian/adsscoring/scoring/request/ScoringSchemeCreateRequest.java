package com.yunbian.adsscoring.scoring.request;

import com.yunbian.adsscoring.scoring.enums.ScoringEntityLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Arrays;

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

    @AssertTrue(message = "levelConfigs must contain campaign, adgroup, bidword exactly once")
    public boolean isLevelConfigsComplete() {
        if (levelConfigs == null || levelConfigs.isEmpty()) {
            return true;
        }

        Set<String> actualLevels = levelConfigs.stream()
                .map(ScoringLevelConfigRequest::getEntityLevel)
                .collect(Collectors.toSet());

        Set<String> expectedLevels = Arrays.stream(ScoringEntityLevel.values())
                .map(ScoringEntityLevel::getCode)
                .collect(Collectors.toSet());

        return actualLevels.equals(expectedLevels);
    }
}