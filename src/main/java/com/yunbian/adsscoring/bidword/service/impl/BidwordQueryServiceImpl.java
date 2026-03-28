package com.yunbian.adsscoring.bidword.service.impl;

import com.yunbian.adsscoring.bidword.dto.BidwordSampleItem;
import com.yunbian.adsscoring.bidword.mapper.BidwordSampleMapper;
import com.yunbian.adsscoring.bidword.service.BidwordQueryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BidwordQueryServiceImpl implements BidwordQueryService {

    @Resource
    private BidwordSampleMapper bidwordSampleMapper;

    @Override
    public List<BidwordSampleItem> getBidwordSample(Long sid, LocalDate logDate, Integer limit) {
        int safeLimit = (limit == null || limit <= 0) ? 10 : Math.min(limit, 20);
        return bidwordSampleMapper.selectSampleBySidAndLogDate(sid, logDate, safeLimit);
    }
}