package com.yunbian.adsscoring.scoring.enums;

public enum ScoringEntityLevel {

    CAMPAIGN("campaign", "计划层", "同评分方案"),
    ADGROUP("adgroup", "单元层", "同评分方案 + 同计划"),
    BIDWORD("bidword", "关键词层", "同评分方案 + 同计划 + 同单元");

    private final String code;
    private final String name;
    private final String comparisonScope;

    ScoringEntityLevel(String code, String name, String comparisonScope) {
        this.code = code;
        this.name = name;
        this.comparisonScope = comparisonScope;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getComparisonScope() {
        return comparisonScope;
    }

    public static ScoringEntityLevel fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Unknown entityLevel: " + code);
        }
        for (ScoringEntityLevel value : values()) {
            if (value.code.equalsIgnoreCase(code.trim())) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown entityLevel: " + code);
    }
}