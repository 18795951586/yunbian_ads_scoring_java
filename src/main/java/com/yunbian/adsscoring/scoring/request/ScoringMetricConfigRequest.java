package com.yunbian.adsscoring.scoring.request;

import com.yunbian.adsscoring.scoring.enums.ScoringMetricKey;
import com.yunbian.adsscoring.scoring.enums.ScoringRuleType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Arrays;

public class ScoringMetricConfigRequest {

    @NotBlank(message = "metricKey must not be blank")
    private String metricKey;

    @NotNull(message = "enabled must not be null")
    private Boolean enabled;

    @NotBlank(message = "ruleType must not be blank")
    private String ruleType;

    @NotNull(message = "weight must not be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "weight must be greater than or equal to 0.0")
    private BigDecimal weight;

    private BigDecimal targetValue;

    public String getMetricKey() {
        return metricKey;
    }

    public void setMetricKey(String metricKey) {
        this.metricKey = metricKey;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(BigDecimal targetValue) {
        this.targetValue = targetValue;
    }

    @AssertTrue(message = "metricKey is invalid, allowed values: roi, cvr, cpc, cart_cost, deal_new_customer_ratio, new_customer_ratio, direct_deal_ratio")
    public boolean isMetricKeyValid() {
        if (metricKey == null || metricKey.isBlank()) {
            return true;
        }
        return Arrays.stream(ScoringMetricKey.values())
                .anyMatch(item -> item.getCode().equals(metricKey));
    }

    @AssertTrue(message = "ruleType is invalid, allowed values: ranking, target_value, smart_benchmark")
    public boolean isRuleTypeValid() {
        if (ruleType == null || ruleType.isBlank()) {
            return true;
        }
        return Arrays.stream(ScoringRuleType.values())
                .anyMatch(item -> item.getCode().equals(ruleType));
    }

    @AssertTrue(message = "targetValue must not be null when ruleType is target_value")
    public boolean isTargetValueValidForRuleType() {
        if (ruleType == null || ruleType.isBlank()) {
            return true;
        }
        if (!isKnownRuleType(ruleType)) {
            return true;
        }
        if (!ScoringRuleType.TARGET_VALUE.getCode().equals(ruleType)) {
            return true;
        }
        return targetValue != null;
    }

    private boolean isKnownRuleType(String code) {
        return Arrays.stream(ScoringRuleType.values())
                .anyMatch(item -> item.getCode().equals(code));
    }
}