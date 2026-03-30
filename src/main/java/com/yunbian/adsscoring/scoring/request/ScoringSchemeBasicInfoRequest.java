package com.yunbian.adsscoring.scoring.request;

import jakarta.validation.constraints.NotBlank;

public class ScoringSchemeBasicInfoRequest {

    private String schemeCode;

    @NotBlank(message = "schemeName must not be blank")
    private String schemeName;

    private String description;

    @NotBlank(message = "status must not be blank")
    private String status;

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}