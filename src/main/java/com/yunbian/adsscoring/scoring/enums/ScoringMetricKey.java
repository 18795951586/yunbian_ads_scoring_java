package com.yunbian.adsscoring.scoring.enums;

public enum ScoringMetricKey {

    ROI(
            "roi",
            "ROI",
            "HIGHER_BETTER",
            "原始字段 roi"
    ),
    CVR(
            "cvr",
            "CVR",
            "HIGHER_BETTER",
            "原始字段 cvr"
    ),
    CPC(
            "cpc",
            "CPC",
            "LOWER_BETTER",
            "原始字段 ecpc"
    ),
    CART_COST(
            "cart_cost",
            "加购成本",
            "LOWER_BETTER",
            "原始字段 cart_cost"
    ),
    DEAL_NEW_CUSTOMER_RATIO(
            "deal_new_customer_ratio",
            "成交新客占比",
            "HIGHER_BETTER",
            "优先使用 new_alipay_inshop_uv_rate；若后续口径改造，可切到显式派生"
    ),
    NEW_CUSTOMER_RATIO(
            "new_customer_ratio",
            "新客占比",
            "HIGHER_BETTER",
            "建议按 new_alipay_inshop_uv / alipay_inshop_uv 派生"
    ),
    DIRECT_DEAL_RATIO(
            "direct_deal_ratio",
            "直接成交占比",
            "HIGHER_BETTER",
            "建议按 alipay_dir_amt / alipay_inshop_amt 派生"
    );

    private final String code;
    private final String name;
    private final String direction;
    private final String dataHint;

    ScoringMetricKey(String code, String name, String direction, String dataHint) {
        this.code = code;
        this.name = name;
        this.direction = direction;
        this.dataHint = dataHint;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDirection() {
        return direction;
    }

    public String getDataHint() {
        return dataHint;
    }

    public boolean isHigherBetter() {
        return "HIGHER_BETTER".equals(direction);
    }

    public static ScoringMetricKey fromCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        for (ScoringMetricKey value : values()) {
            if (value.code.equalsIgnoreCase(code.trim())) {
                return value;
            }
        }
        return null;
    }
}