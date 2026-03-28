package com.yunbian.adsscoring.campaign.service;

import com.yunbian.adsscoring.campaign.dto.CampaignMetricsMatrixItem;

import java.time.LocalDate;
import java.util.List;

public interface CampaignMetricsMatrixService {

    List<CampaignMetricsMatrixItem> getMetricsMatrix(Long sid, LocalDate logDate, Integer limit);
}