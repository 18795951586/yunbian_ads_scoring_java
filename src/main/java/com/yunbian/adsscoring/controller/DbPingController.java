package com.yunbian.adsscoring.controller;

import com.yunbian.adsscoring.common.ApiResponse;
import com.yunbian.adsscoring.common.mapper.DbPingMapper;
import com.yunbian.adsscoring.config.AppProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class DbPingController {

    private final DataSource dataSource;
    private final AppProperties appProperties;
    private final DbPingMapper dbPingMapper;

    public DbPingController(
            DataSource dataSource,
            AppProperties appProperties,
            DbPingMapper dbPingMapper
    ) {
        this.dataSource = dataSource;
        this.appProperties = appProperties;
        this.dbPingMapper = dbPingMapper;
    }

    @GetMapping("/db/ping")
    public ApiResponse<Map<String, Object>> ping() throws Exception {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1");
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("dbOk", true);
            result.put("value", resultSet.getInt(1));
            result.put("schema", appProperties.getDatabase().getSchema());
            result.put("name", appProperties.getDatabase().getName());
            result.put("mode", "datasource");
            return ApiResponse.success(result);
        }
    }

    @GetMapping("/db/ping-mybatis")
    public ApiResponse<Map<String, Object>> pingMybatis() {
        Integer value = dbPingMapper.ping();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dbOk", true);
        result.put("value", value);
        result.put("schema", appProperties.getDatabase().getSchema());
        result.put("name", appProperties.getDatabase().getName());
        result.put("mode", "mybatis");
        return ApiResponse.success(result);
    }
}