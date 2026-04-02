package com.yunbian.adsscoring.scoring.mapper;

import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmCoreIndicator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlgorithmCoreIndicatorMapper {

    List<AlgorithmCoreIndicator> selectByAlgorithmTemplateIds(@Param("algorithmTemplateIds") List<Long> algorithmTemplateIds);
}
