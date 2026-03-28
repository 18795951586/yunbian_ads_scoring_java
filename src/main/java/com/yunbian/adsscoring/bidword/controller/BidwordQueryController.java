package com.yunbian.adsscoring.bidword.controller;

import com.yunbian.adsscoring.bidword.dto.BidwordSampleItem;
import com.yunbian.adsscoring.bidword.service.BidwordQueryService;
import com.yunbian.adsscoring.common.ApiResponse;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/bidword")
public class BidwordQueryController {

    @Resource
    private BidwordQueryService bidwordQueryService;

    @GetMapping("/sample")
    public ApiResponse<Map<String, Object>> sample(
            @RequestParam("sid") @NotNull Long sid,
            @RequestParam("logDate") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate,
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        List<BidwordSampleItem> rows = bidwordQueryService.getBidwordSample(sid, logDate, limit);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("sid", sid);
        data.put("logDate", logDate);
        data.put("count", rows.size());
        data.put("rows", rows);

        return ApiResponse.success(data);
    }
}