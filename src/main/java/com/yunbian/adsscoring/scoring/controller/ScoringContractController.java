package com.yunbian.adsscoring.scoring.controller;

import com.yunbian.adsscoring.common.ApiResponse;
import com.yunbian.adsscoring.scoring.dto.ScoringContractTemplateResponse;
import com.yunbian.adsscoring.scoring.dto.ScoringSchemeCreatePreviewResponse;
import com.yunbian.adsscoring.scoring.request.ScoringSchemeCreateRequest;
import com.yunbian.adsscoring.scoring.service.ScoringContractService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scoring/contract")
public class ScoringContractController {

    @Resource
    private ScoringContractService scoringContractService;

    @GetMapping("/template")
    public ApiResponse<ScoringContractTemplateResponse> template() {
        return ApiResponse.success(scoringContractService.buildTemplate());
    }

    @PostMapping("/create-request/preview")
    public ApiResponse<ScoringSchemeCreatePreviewResponse> previewCreateRequest(
            @Valid @RequestBody ScoringSchemeCreateRequest request
    ) {
        return ApiResponse.success(scoringContractService.previewCreateRequest(request));
    }
}