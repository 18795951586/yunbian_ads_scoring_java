package com.yunbian.adsscoring.scoring.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AdgroupScoringResponse {

    private Long sid;
    private LocalDate logDate;
    private String entityLevel;
    private String ruleType;
    private Integer effectDays;
    private Integer rawRowCount;
    private Integer enabledMetricCount;
    private Integer enabledRankingMetricCount;
    private Integer usedMetricCount;
    private Integer skippedMetricCount;
    private Integer pageIndex;
    private Integer pageSize;
    private Integer totalCount;
    private Integer totalPages;
    private List<MetricSummary> metricSummaries;
    private List<AdgroupScoringRow> rows;

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

    public Integer getEnabledMetricCount() {
        return enabledMetricCount;
    }

    public void setEnabledMetricCount(Integer enabledMetricCount) {
        this.enabledMetricCount = enabledMetricCount;
    }

    public Integer getEnabledRankingMetricCount() {
        return enabledRankingMetricCount;
    }

    public void setEnabledRankingMetricCount(Integer enabledRankingMetricCount) {
        this.enabledRankingMetricCount = enabledRankingMetricCount;
    }

    public Integer getUsedMetricCount() {
        return usedMetricCount;
    }

    public void setUsedMetricCount(Integer usedMetricCount) {
        this.usedMetricCount = usedMetricCount;
    }

    public Integer getSkippedMetricCount() {
        return skippedMetricCount;
    }

    public void setSkippedMetricCount(Integer skippedMetricCount) {
        this.skippedMetricCount = skippedMetricCount;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<MetricSummary> getMetricSummaries() {
        return metricSummaries;
    }

    public void setMetricSummaries(List<MetricSummary> metricSummaries) {
        this.metricSummaries = metricSummaries;
    }

    public List<AdgroupScoringRow> getRows() {
        return rows;
    }

    public void setRows(List<AdgroupScoringRow> rows) {
        this.rows = rows;
    }

    public static class MetricSummary {

        private String metricKey;
        private String metricName;
        private String ruleType;
        private BigDecimal weight;
        private Integer comparisonCount;
        private Integer excludedNullCount;
        private Boolean usedInAggregation;

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

        public Boolean getUsedInAggregation() {
            return usedInAggregation;
        }

        public void setUsedInAggregation(Boolean usedInAggregation) {
            this.usedInAggregation = usedInAggregation;
        }
    }

    public static class AdgroupScoringRow {

        private Long campaignId;
        private String campaignName;
        private Long adgroupId;
        private String adgroupName;
        private BigDecimal totalScore;
        private Integer participatingMetricCount;
        private BigDecimal participatingWeightSum;
        private List<MetricContribution> metricContributions;

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

        public Long getAdgroupId() {
            return adgroupId;
        }

        public void setAdgroupId(Long adgroupId) {
            this.adgroupId = adgroupId;
        }

        public String getAdgroupName() {
            return adgroupName;
        }

        public void setAdgroupName(String adgroupName) {
            this.adgroupName = adgroupName;
        }

        public BigDecimal getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(BigDecimal totalScore) {
            this.totalScore = totalScore;
        }

        public Integer getParticipatingMetricCount() {
            return participatingMetricCount;
        }

        public void setParticipatingMetricCount(Integer participatingMetricCount) {
            this.participatingMetricCount = participatingMetricCount;
        }

        public BigDecimal getParticipatingWeightSum() {
            return participatingWeightSum;
        }

        public void setParticipatingWeightSum(BigDecimal participatingWeightSum) {
            this.participatingWeightSum = participatingWeightSum;
        }

        public List<MetricContribution> getMetricContributions() {
            return metricContributions;
        }

        public void setMetricContributions(List<MetricContribution> metricContributions) {
            this.metricContributions = metricContributions;
        }
    }

    public static class MetricContribution {

        private String metricKey;
        private String metricName;
        private String ruleType;
        private BigDecimal metricValue;
        private Integer rank;
        private BigDecimal score;
        private BigDecimal weight;
        private BigDecimal weightedScore;

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

        public String getRuleType() {
            return ruleType;
        }

        public void setRuleType(String ruleType) {
            this.ruleType = ruleType;
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

        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }

        public BigDecimal getWeightedScore() {
            return weightedScore;
        }

        public void setWeightedScore(BigDecimal weightedScore) {
            this.weightedScore = weightedScore;
        }
    }
}