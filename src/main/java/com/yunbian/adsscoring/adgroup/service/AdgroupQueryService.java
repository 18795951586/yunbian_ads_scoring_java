package com.yunbian.adsscoring.adgroup.service;

import com.yunbian.adsscoring.adgroup.dto.AdgroupSampleItem;

import java.time.LocalDate;
import java.util.List;

public interface AdgroupQueryService {

    List<AdgroupSampleItem> getAdgroupSample(Long sid, LocalDate logDate, Integer limit);
}