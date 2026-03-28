package com.yunbian.adsscoring.controller;

import com.yunbian.adsscoring.adgroup.dto.AdgroupSampleItem;
import com.yunbian.adsscoring.adgroup.service.AdgroupQueryService;
import com.yunbian.adsscoring.bidword.dto.BidwordSampleItem;
import com.yunbian.adsscoring.bidword.service.BidwordQueryService;
import com.yunbian.adsscoring.campaign.dto.CampaignSampleItem;
import com.yunbian.adsscoring.campaign.service.CampaignQueryService;
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
@RequestMapping("/smoke")
public class RawSmokeController {

    @Resource
    private CampaignQueryService campaignQueryService;

    @Resource
    private AdgroupQueryService adgroupQueryService;

    @Resource
    private BidwordQueryService bidwordQueryService;

    @GetMapping("/raw")
    public ApiResponse<Map<String, Object>> raw(
            @RequestParam("sid") @NotNull Long sid,
            @RequestParam("logDate") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate,
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        List<CampaignSampleItem> campaignRows = campaignQueryService.getCampaignSample(sid, logDate, limit);
        List<AdgroupSampleItem> adgroupRows = adgroupQueryService.getAdgroupSample(sid, logDate, limit);
        List<BidwordSampleItem> bidwordRows = bidwordQueryService.getBidwordSample(sid, logDate, limit);

        Map<String, Object> campaign = new LinkedHashMap<>();
        campaign.put("count", campaignRows.size());
        campaign.put("rows", campaignRows);

        Map<String, Object> adgroup = new LinkedHashMap<>();
        adgroup.put("count", adgroupRows.size());
        adgroup.put("rows", adgroupRows);

        Map<String, Object> bidword = new LinkedHashMap<>();
        bidword.put("count", bidwordRows.size());
        bidword.put("rows", bidwordRows);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("sid", sid);
        data.put("logDate", logDate);
        data.put("limit", limit);
        data.put("campaign", campaign);
        data.put("adgroup", adgroup);
        data.put("bidword", bidword);

        return ApiResponse.success(data);
    }
}