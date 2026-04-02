// 文件路径: src/main/java/com/yunbian/adsscoring/scoring/mapper/AlgorithmScoreDetailMapper.java
package com.yunbian.adsscoring.scoring.mapper;

import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmScoreDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AlgorithmScoreDetailMapper {

    AlgorithmScoreDetail selectByUniqueMark(@Param("uniqueMark") String uniqueMark);
}