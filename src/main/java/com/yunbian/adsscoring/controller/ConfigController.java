package com.yunbian.adsscoring.controller;

import com.yunbian.adsscoring.common.ApiResponse;
import com.yunbian.adsscoring.config.AppProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class ConfigController {

    private final AppProperties appProperties;

    public ConfigController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping("/config")
    public ApiResponse<Map<String, Object>> config() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("env", appProperties.getEnv());
        result.put("logLevel", appProperties.getLogLevel());
        result.put("defaultSid", appProperties.getScoring().getDefaultSid());
        result.put("defaultLogDate", appProperties.getScoring().getDefaultLogDate());

        Map<String, Object> database = new LinkedHashMap<>();
        database.put("host", appProperties.getDatabase().getHost());
        database.put("port", appProperties.getDatabase().getPort());
        database.put("name", appProperties.getDatabase().getName());
        database.put("user", appProperties.getDatabase().getUser());
        database.put("schema", appProperties.getDatabase().getSchema());

        result.put("database", database);
        return ApiResponse.success(result);
    }
}