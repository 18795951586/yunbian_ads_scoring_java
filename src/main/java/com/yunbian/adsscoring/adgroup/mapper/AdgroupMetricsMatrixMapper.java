package com.yunbian.adsscoring.adgroup.mapper;

import com.yunbian.adsscoring.adgroup.dto.AdgroupMetricsMatrixItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AdgroupMetricsMatrixMapper {

    List<AdgroupMetricsMatrixItem> selectAllMetricsMatrixBySidAndLogDate(
            @Param("sid") Long sid,
            @Param("logDate") LocalDate logDate
    );
}