package com.yunbian.adsscoring.controller;

import com.yunbian.adsscoring.common.ApiResponse;
import com.yunbian.adsscoring.controller.request.EchoRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/validation")
public class ValidationController {

    @PostMapping("/echo")
    public ApiResponse<Map<String, Object>> echo(@Valid @RequestBody EchoRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", request.getMessage());
        result.put("sid", request.getSid());
        return ApiResponse.success(result);
    }
}