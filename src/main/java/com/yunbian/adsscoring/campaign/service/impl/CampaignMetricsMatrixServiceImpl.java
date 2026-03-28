package com.yunbian.adsscoring.campaign.service.impl;

import com.yunbian.adsscoring.campaign.dto.CampaignMetricsMatrixItem;
import com.yunbian.adsscoring.campaign.mapper.CampaignMetricsMatrixMapper;
import com.yunbian.adsscoring.campaign.service.CampaignMetricsMatrixService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CampaignMetricsMatrixServiceImpl implements CampaignMetricsMatrixService {

    @Resource
    private CampaignMetricsMatrixMapper campaignMetricsMatrixMapper;

    @Override
    public List<CampaignMetricsMatrixItem> getMetricsMatrix(Long sid, LocalDate logDate, Integer limit) {
        int safeLimit = (limit == null || limit <= 0) ? 10 : Math.min(limit, 20);
        return campaignMetricsMatrixMapper.selectMetricsMatrixBySidAndLogDate(sid, logDate, safeLimit);
    }
}