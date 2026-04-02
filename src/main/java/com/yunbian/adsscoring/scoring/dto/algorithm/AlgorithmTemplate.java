package com.yunbian.adsscoring.scoring.dto.algorithm;

import java.time.LocalDateTime;

public class AlgorithmTemplate {

    private Long id;
    private String templateId;
    private String name;
    private String description;
    private String category;
    private String templateType;
    private String campaignGroupId;
    private String campaignGroupName;
    private Integer isActive;
    private Integer isDefault;
    private String defaultDataRange;
    private Long enterpriseId;
    private Long sid;
    private LocalDateTime createTime;
    private LocalDateTime lastModifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getCampaignGroupId() {
        return campaignGroupId;
    }

    public void setCampaignGroupId(String campaignGroupId) {
        this.campaignGroupId = campaignGroupId;
    }

    public String getCampaignGroupName() {
        return campaignGroupName;
    }

    public void setCampaignGroupName(String campaignGroupName) {
        this.campaignGroupName = campaignGroupName;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public String getDefaultDataRange() {
        return defaultDataRange;
    }

    public void setDefaultDataRange(String defaultDataRange) {
        this.defaultDataRange = defaultDataRange;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
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
