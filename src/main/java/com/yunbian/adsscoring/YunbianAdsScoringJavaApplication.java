package com.yunbian.adsscoring;

/**
 * 兼容旧入口名，避免继续作为 Spring 配置类参与扫描。
 * 真正启动入口统一收敛到 Application。
 */
@Deprecated
public final class YunbianAdsScoringJavaApplication {

    private YunbianAdsScoringJavaApplication() {
    }

    public static void main(String[] args) {
        Application.main(args);
    }
}