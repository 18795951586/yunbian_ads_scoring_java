package com.yunbian.adsscoring.scoring.dto.algorithm;

import java.time.LocalDateTime;

public class AlgorithmScoreDetail {

    private Long id;
    private Long algorithmScoreId;
    private String indicatorCode;
    private String originalScore;
    private String weightedScore;
    private String uniqueMark;
    private LocalDateTime createTime;
    private LocalDateTime lastModifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAlgorithmScoreId() {
        return algorithmScoreId;
    }

    public void setAlgorithmScoreId(Long algorithmScoreId) {
        this.algorithmScoreId = algorithmScoreId;
    }

    public String getIndicatorCode() {
        return indicatorCode;
    }

    public void setIndicatorCode(String indicatorCode) {
        this.indicatorCode = indicatorCode;
    }

    public String getOriginalScore() {
        return originalScore;
    }

    public void setOriginalScore(String originalScore) {
        this.originalScore = originalScore;
    }

    public String getWeightedScore() {
        return weightedScore;
    }

    public void setWeightedScore(String weightedScore) {
        this.weightedScore = weightedScore;
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
