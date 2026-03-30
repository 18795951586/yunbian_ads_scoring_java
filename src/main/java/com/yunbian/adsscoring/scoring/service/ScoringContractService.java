package com.yunbian.adsscoring.scoring.service;

import com.yunbian.adsscoring.scoring.dto.ScoringContractTemplateResponse;
import com.yunbian.adsscoring.scoring.dto.ScoringSchemeCreatePreviewResponse;
import com.yunbian.adsscoring.scoring.request.ScoringSchemeCreateRequest;

public interface ScoringContractService {

    ScoringContractTemplateResponse buildTemplate();

    ScoringSchemeCreatePreviewResponse previewCreateRequest(ScoringSchemeCreateRequest request);
}