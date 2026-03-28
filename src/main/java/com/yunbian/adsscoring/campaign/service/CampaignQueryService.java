package com.yunbian.adsscoring.campaign.service;

import com.yunbian.adsscoring.campaign.dto.CampaignSampleItem;

import java.time.LocalDate;
import java.util.List;

public interface CampaignQueryService {

    List<CampaignSampleItem> getCampaignSample(Long sid, LocalDate logDate, Integer limit);
}