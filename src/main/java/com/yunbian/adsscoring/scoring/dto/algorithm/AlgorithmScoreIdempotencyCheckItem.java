package com.yunbian.adsscoring.scoring.dto.algorithm;

import java.time.LocalDate;

public class AlgorithmScoreIdempotencyCheckItem {

    private AlgorithmConfigBindingAggregate binding;
    private LocalDate logDate;
    private String uniqueMark;
    private boolean scoreExists;
    private AlgorithmScore existingScore;

    public AlgorithmConfigBindingAggregate getBinding() {
        return binding;
    }

    public void setBinding(AlgorithmConfigBindingAggregate binding) {
        this.binding = binding;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public String getUniqueMark() {
        return uniqueMark;
    }

    public void setUniqueMark(String uniqueMark) {
        this.uniqueMark = uniqueMark;
    }

    public boolean isScoreExists() {
        return scoreExists;
    }

    public void setScoreExists(boolean scoreExists) {
        this.scoreExists = scoreExists;
    }

    public AlgorithmScore getExistingScore() {
        return existingScore;
    }

    public void setExistingScore(AlgorithmScore existingScore) {
        this.existingScore = existingScore;
    }
}
