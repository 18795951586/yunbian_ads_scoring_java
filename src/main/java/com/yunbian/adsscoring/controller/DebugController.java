package com.yunbian.adsscoring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {

    @GetMapping("/debug/error")
    public String error() {
        throw new RuntimeException("debug error");
    }
}