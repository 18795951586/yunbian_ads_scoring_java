// 文件路径: src/main/java/com/yunbian/adsscoring/scoring/dto/algorithm/AlgorithmScoreConfig.java
package com.yunbian.adsscoring.scoring.dto.algorithm;

import java.time.LocalDateTime;

public class AlgorithmScoreConfig {

    private Long id;
    private Long enterpriseId;
    private Long sid;
    private String bizCode;
    private Long algorithmTemplateId;
    private String businessType;
    private Long businessId;
    private String uniqueMark;
    private LocalDateTime createTime;
    private LocalDateTime lastModifyTime;

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

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public Long getAlgorithmTemplateId() {
        return algorithmTemplateId;
    }

    public void setAlgorithmTemplateId(Long algorithmTemplateId) {
        this.algorithmTemplateId = algorithmTemplateId;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getUniqueMark() {
        return uniqueMark;
    }

    public void setUniqueMark(String uniqueMark) {
        this.uniqueMark = uniqueMark;
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