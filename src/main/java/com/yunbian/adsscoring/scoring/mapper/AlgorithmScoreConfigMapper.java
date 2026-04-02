// 文件路径: src/main/java/com/yunbian/adsscoring/scoring/mapper/AlgorithmScoreConfigMapper.java
package com.yunbian.adsscoring.scoring.mapper;

import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmScoreConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlgorithmScoreConfigMapper {

    List<AlgorithmScoreConfig> selectByEnterpriseAndSidAndBusinessType(
            @Param("enterpriseId") Long enterpriseId,
            @Param("sid") Long sid,
            @Param("businessType") String businessType
    );
}