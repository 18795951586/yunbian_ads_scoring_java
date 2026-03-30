package com.yunbian.adsscoring.scoring.enums;

public enum ScoringRuleType {

    RANKING(
            "ranking",
            "规则1-排名规则",
            false,
            "在同比较池里按单指标排名，正向指标高者优，反向指标低者优，再转成 0-100 分"
    ),
    TARGET_VALUE(
            "target_value",
            "规则2-目标值规则",
            true,
            "用户只填一个目标值，按当前值与目标值的关系打分"
    ),
    SMART_BENCHMARK(
            "smart_benchmark",
            "规则3-智能基准规则",
            false,
            "在同比较池里自动找最优基准，正向指标按 当前值/基准值，反向指标按 基准值/当前值，再转成 0-100 分"
    );

    private final String code;
    private final String name;
    private final boolean targetValueRequired;
    private final String description;

    ScoringRuleType(String code, String name, boolean targetValueRequired, String description) {
        this.code = code;
        this.name = name;
        this.targetValueRequired = targetValueRequired;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean isTargetValueRequired() {
        return targetValueRequired;
    }

    public String getDescription() {
        return description;
    }
}