package com.zerobase.dividened.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zerobase.dividened.model.ScrapedResult;
import com.zerobase.dividened.service.FinanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/finance")
public class FinanceController {
    private final FinanceService financeService;

    @GetMapping("dividened/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable String companyName) {
        ScrapedResult scrapResult = financeService.getDividenedByCompanyName(companyName);
        return ResponseEntity.ok(scrapResult);
    }
}
