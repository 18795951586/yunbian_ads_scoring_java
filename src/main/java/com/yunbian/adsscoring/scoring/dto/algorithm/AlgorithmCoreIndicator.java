package com.yunbian.adsscoring.scoring.dto.algorithm;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AlgorithmCoreIndicator {

    private Long id;
    private Long templateId;
    private String code;
    private String name;
    private String type;
    private String description;
    private BigDecimal baseWeight;
    private String direction;
    private String scoringMode;
    private BigDecimal targetValue;
    private String unit;
    private String targetRange;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime lastModifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBaseWeight() {
        return baseWeight;
    }

    public void setBaseWeight(BigDecimal baseWeight) {
        this.baseWeight = baseWeight;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getScoringMode() {
        return scoringMode;
    }

    public void setScoringMode(String scoringMode) {
        this.scoringMode = scoringMode;
    }

    public BigDecimal getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(BigDecimal targetValue) {
        this.targetValue = targetValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTargetRange() {
        return targetRange;
    }

    public void setTargetRange(String targetRange) {
        this.targetRange = targetRange;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(LocalDateTime lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }
}
