package com.yunbian.adsscoring.scoring.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CampaignRankingPreviewResponse {

    private Long sid;
    private LocalDate logDate;
    private String entityLevel;
    private String ruleType;
    private String metricKey;
    private String metricName;
    private String metricDirection;
    private Integer effectDays;
    private Integer rawRowCount;
    private Integer comparisonCount;
    private Integer excludedNullCount;
    private List<CampaignRankingRow> rows;

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public String getEntityLevel() {
        return entityLevel;
    }

    public void setEntityLevel(String entityLevel) {
        this.entityLevel = entityLevel;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getMetricKey() {
        return metricKey;
    }

    public void setMetricKey(String metricKey) {
        this.metricKey = metricKey;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricDirection() {
        return metricDirection;
    }

    public void setMetricDirection(String metricDirection) {
        this.metricDirection = metricDirection;
    }

    public Integer getEffectDays() {
        return effectDays;
    }

    public void setEffectDays(Integer effectDays) {
        this.effectDays = effectDays;
    }

    public Integer getRawRowCount() {
        return rawRowCount;
    }

    public void setRawRowCount(Integer rawRowCount) {
        this.rawRowCount = rawRowCount;
    }

    public Integer getComparisonCount() {
        return comparisonCount;
    }

    public void setComparisonCount(Integer comparisonCount) {
        this.comparisonCount = comparisonCount;
    }

    public Integer getExcludedNullCount() {
        return excludedNullCount;
    }

    public void setExcludedNullCount(Integer excludedNullCount) {
        this.excludedNullCount = excludedNullCount;
    }

    public List<CampaignRankingRow> getRows() {
        return rows;
    }

    public void setRows(List<CampaignRankingRow> rows) {
        this.rows = rows;
    }

    public static class CampaignRankingRow {

        private Long campaignId;
        private String campaignName;
        private BigDecimal metricValue;
        private Integer rank;
        private BigDecimal score;

        public Long getCampaignId() {
            return campaignId;
        }

        public void setCampaignId(Long campaignId) {
            this.campaignId = campaignId;
        }

        public String getCampaignName() {
            return campaignName;
        }

        public void setCampaignName(String campaignName) {
            this.campaignName = campaignName;
        }

        public BigDecimal getMetricValue() {
            return metricValue;
        }

        public void setMetricValue(BigDecimal metricValue) {
            this.metricValue = metricValue;
        }

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public BigDecimal getScore() {
            return score;
        }

        public void setScore(BigDecimal score) {
            this.score = score;
        }
    }
}