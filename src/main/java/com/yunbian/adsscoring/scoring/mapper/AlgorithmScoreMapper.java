// 文件路径: src/main/java/com/yunbian/adsscoring/scoring/mapper/AlgorithmScoreMapper.java
package com.yunbian.adsscoring.scoring.mapper;

import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AlgorithmScoreMapper {

    AlgorithmScore selectByUniqueMark(@Param("uniqueMark") String uniqueMark);
}