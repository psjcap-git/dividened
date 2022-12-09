package com.zerobase.dividened.web;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zerobase.dividened.model.Company;
import com.zerobase.dividened.model.constant.CacheKey;
import com.zerobase.dividened.persist.entity.CompanyEntity;
import com.zerobase.dividened.service.CompanyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {
    private final CompanyService companyService;
    private final CacheManager redisCacheManager;

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autoComplete(@RequestParam String keyword) {
        return ResponseEntity.ok(companyService.getCompanyNamesByKeyword(keyword));
        //return ResponseEntity.ok(companyService.autoComplete(keyword));
    }

    @GetMapping()
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(final Pageable pageable) {
        Page<CompanyEntity> companies = companyService.getAllCompanies(pageable);
        return ResponseEntity.ok(companies);
    }

    @PostMapping()
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim();
        if(ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("ticker is empty");
        }

        Company company = companyService.save(ticker);
        companyService.addAutoCompleteKeyword(company.getName());

        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        String deletedCompanyName = companyService.deleteCompany(ticker);
        this.clearFinanceCache(deletedCompanyName);

        return ResponseEntity.ok(deletedCompanyName);
    }

    private void clearFinanceCache(String companyName) {
        Cache cache = redisCacheManager.getCache(CacheKey.KEY_FINANCE);
        if(cache != null) {
            cache.evict(companyName);
        }        
    }



}
