package com.yunbian.adsscoring.scoring.mapper;

import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlgorithmTemplateMapper {

    List<AlgorithmTemplate> selectByAlgorithmTemplateIds(@Param("algorithmTemplateIds") List<Long> algorithmTemplateIds);
}
