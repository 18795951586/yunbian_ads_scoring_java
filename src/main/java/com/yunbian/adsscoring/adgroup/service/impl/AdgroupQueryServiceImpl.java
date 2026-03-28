package com.yunbian.adsscoring.adgroup.service.impl;

import com.yunbian.adsscoring.adgroup.dto.AdgroupSampleItem;
import com.yunbian.adsscoring.adgroup.mapper.AdgroupSampleMapper;
import com.yunbian.adsscoring.adgroup.service.AdgroupQueryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdgroupQueryServiceImpl implements AdgroupQueryService {

    @Resource
    private AdgroupSampleMapper adgroupSampleMapper;

    @Override
    public List<AdgroupSampleItem> getAdgroupSample(Long sid, LocalDate logDate, Integer limit) {
        int safeLimit = (limit == null || limit <= 0) ? 10 : Math.min(limit, 20);
        return adgroupSampleMapper.selectSampleBySidAndLogDate(sid, logDate, safeLimit);
    }
}