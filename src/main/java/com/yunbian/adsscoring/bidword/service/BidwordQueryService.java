package com.yunbian.adsscoring.bidword.service;

import com.yunbian.adsscoring.bidword.dto.BidwordSampleItem;

import java.time.LocalDate;
import java.util.List;

public interface BidwordQueryService {

    List<BidwordSampleItem> getBidwordSample(Long sid, LocalDate logDate, Integer limit);
}