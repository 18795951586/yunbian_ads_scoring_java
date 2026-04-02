package com.yunbian.adsscoring.scoring.service.impl;

import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmConfigBindingAggregate;
import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmScore;
import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmScoreConfig;
import com.yunbian.adsscoring.scoring.dto.algorithm.AlgorithmScoreIdempotencyCheckItem;
import com.yunbian.adsscoring.scoring.mapper.AlgorithmScoreMapper;
import com.yunbian.adsscoring.scoring.service.AlgorithmConfigReadService;
import com.yunbian.adsscoring.scoring.service.AlgorithmScoreIdempotencyService;
import com.yunbian.adsscoring.scoring.util.AlgorithmUniqueMarkUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class AlgorithmScoreIdempotencyServiceImpl implements AlgorithmScoreIdempotencyService {

    private static final Set<String> SUPPORTED_BUSINESS_TYPES = Set.of("campaign", "adgroup", "bidword");

    @Resource
    private AlgorithmConfigReadService algorithmConfigReadService;

    @Resource
    private AlgorithmScoreMapper algorithmScoreMapper;

    @Override
    public List<AlgorithmScoreIdempotencyCheckItem> prepareIdempotencyChecks(
            Long enterpriseId,
            Long sid,
            String businessType,
            LocalDate logDate
    ) {
        if (enterpriseId == null || sid == null || logDate == null || businessType == null || businessType.isBlank()) {
            return Collections.emptyList();
        }

        String normalizedBusinessType = businessType.toLowerCase(Locale.ROOT);
        if (!SUPPORTED_BUSINESS_TYPES.contains(normalizedBusinessType)) {
            return Collections.emptyList();
        }

        List<AlgorithmConfigBindingAggregate> bindings =
                algorithmConfigReadService.readConfigBindings(enterpriseId, sid, normalizedBusinessType);
        if (bindings == null || bindings.isEmpty()) {
            return Collections.emptyList();
        }

        List<AlgorithmScoreIdempotencyCheckItem> result = new ArrayList<>();
        for (AlgorithmConfigBindingAggregate binding : bindings) {
            if (binding == null || binding.getScoreConfig() == null) {
                continue;
            }

            AlgorithmScoreConfig scoreConfig = binding.getScoreConfig();
            Long algorithmTemplateId = scoreConfig.getAlgorithmTemplateId();
            Long businessId = scoreConfig.getBusinessId();
            if (algorithmTemplateId == null || businessId == null) {
                continue;
            }

            Long uniqueEnterpriseId = scoreConfig.getEnterpriseId() != null
                    ? scoreConfig.getEnterpriseId()
                    : enterpriseId;
            Long uniqueSid = scoreConfig.getSid() != null
                    ? scoreConfig.getSid()
                    : sid;
            String uniqueBusinessType = scoreConfig.getBusinessType() != null && !scoreConfig.getBusinessType().isBlank()
                    ? scoreConfig.getBusinessType()
                    : normalizedBusinessType;

            String uniqueMark = AlgorithmUniqueMarkUtils.buildUniqueMark(
                    uniqueEnterpriseId,
                    uniqueSid,
                    algorithmTemplateId,
                    uniqueBusinessType,
                    businessId,
                    logDate
            );
            AlgorithmScore existingScore = algorithmScoreMapper.selectByUniqueMark(uniqueMark);

            AlgorithmScoreIdempotencyCheckItem item = new AlgorithmScoreIdempotencyCheckItem();
            item.setBinding(binding);
            item.setLogDate(logDate);
            item.setUniqueMark(uniqueMark);
            item.setScoreExists(existingScore != null);
            item.setExistingScore(existingScore);
            result.add(item);
        }

        return result;
    }
}
