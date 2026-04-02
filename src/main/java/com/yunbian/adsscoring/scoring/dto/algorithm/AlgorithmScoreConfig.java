// 文件路径: src/main/java/com/yunbian/adsscoring/scoring/dto/algorithm/AlgorithmScoreConfig.java
package com.yunbian.adsscoring.scoring.dto.algorithm;

import java.time.LocalDateTime;

public class AlgorithmScoreConfig {

    private Long id;
    private Long enterpriseId;
    private Long sid;
    private String businessType;
    private Long algorithmTemplateId;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Long getAlgorithmTemplateId() {
        return algorithmTemplateId;
    }

    public void setAlgorithmTemplateId(Long algorithmTemplateId) {
        this.algorithmTemplateId = algorithmTemplateId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}