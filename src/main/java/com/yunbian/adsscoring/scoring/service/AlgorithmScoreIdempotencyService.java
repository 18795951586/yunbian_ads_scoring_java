package com.yunbian.adsscoring.scoring.service;

import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmScoreIdempotencyCheckItem;

import java.time.LocalDate;
import java.util.List;

public interface AlgorithmScoreIdempotencyService {

    List<AlgorithmScoreIdempotencyCheckItem> prepareIdempotencyChecks(
            Long enterpriseId,
            Long sid,
            String businessType,
            LocalDate logDate
    );
}
