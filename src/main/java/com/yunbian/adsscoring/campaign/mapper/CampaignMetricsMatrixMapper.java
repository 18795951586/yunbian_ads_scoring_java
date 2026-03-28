package com.yunbian.adsscoring.campaign.mapper;

import com.yunbian.adsscoring.campaign.dto.CampaignMetricsMatrixItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CampaignMetricsMatrixMapper {

    List<CampaignMetricsMatrixItem> selectMetricsMatrixBySidAndLogDate(
            @Param("sid") Long sid,
            @Param("logDate") LocalDate logDate,
            @Param("limit") Integer limit
    );
}