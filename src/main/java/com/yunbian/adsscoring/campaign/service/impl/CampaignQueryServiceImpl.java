package com.yunbian.adsscoring.campaign.service.impl;

import com.yunbian.adsscoring.campaign.dto.CampaignSampleItem;
import com.yunbian.adsscoring.campaign.mapper.CampaignSampleMapper;
import com.yunbian.adsscoring.campaign.service.CampaignQueryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CampaignQueryServiceImpl implements CampaignQueryService {

    @Resource
    private CampaignSampleMapper campaignSampleMapper;

    @Override
    public List<CampaignSampleItem> getCampaignSample(Long sid, LocalDate logDate, Integer limit) {
        int safeLimit = (limit == null || limit <= 0) ? 10 : Math.min(limit, 20);
        return campaignSampleMapper.selectSampleBySidAndLogDate(sid, logDate, safeLimit);
    }
}