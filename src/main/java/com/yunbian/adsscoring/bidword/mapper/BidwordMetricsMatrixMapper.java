package com.yunbian.adsscoring.bidword.mapper;

import com.yunbian.adsscoring.bidword.dto.BidwordMetricsMatrixItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BidwordMetricsMatrixMapper {

    List<BidwordMetricsMatrixItem> selectAllMetricsMatrixBySidAndLogDate(
            @Param("sid") Long sid,
            @Param("logDate") LocalDate logDate
    );
}
