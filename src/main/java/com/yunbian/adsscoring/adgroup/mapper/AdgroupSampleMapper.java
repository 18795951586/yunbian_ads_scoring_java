package com.yunbian.adsscoring.adgroup.mapper;

import com.yunbian.adsscoring.adgroup.dto.AdgroupSampleItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AdgroupSampleMapper {

    List<AdgroupSampleItem> selectSampleBySidAndLogDate(
            @Param("sid") Long sid,
            @Param("logDate") LocalDate logDate,
            @Param("limit") Integer limit
    );
}