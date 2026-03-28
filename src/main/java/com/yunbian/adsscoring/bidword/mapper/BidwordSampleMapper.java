package com.yunbian.adsscoring.bidword.mapper;

import com.yunbian.adsscoring.bidword.dto.BidwordSampleItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BidwordSampleMapper {

    List<BidwordSampleItem> selectSampleBySidAndLogDate(
            @Param("sid") Long sid,
            @Param("logDate") LocalDate logDate,
            @Param("limit") Integer limit
    );
}