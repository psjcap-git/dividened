package com.zerobase.dividened.scheduler;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.zerobase.dividened.model.Company;
import com.zerobase.dividened.model.ScrapedResult;
import com.zerobase.dividened.model.constant.CacheKey;
import com.zerobase.dividened.persist.entity.CompanyEntity;
import com.zerobase.dividened.persist.entity.DividenedEntity;
import com.zerobase.dividened.persist.repository.CompanyRepository;
import com.zerobase.dividened.persist.repository.DividenedRepository;
import com.zerobase.dividened.scrapper.Scrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableCaching
@RequiredArgsConstructor
@Slf4j
public class ScrapperScheduler {
    private final CompanyRepository companyRepository;
    private final DividenedRepository dividenedRepository;
    private final Scrapper yahooFinanceScrapper;

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "0 0 1 * * *")    
    public void yahooFinanceScheduling() {
        List<CompanyEntity> companyEntities = companyRepository.findAll();
        for(CompanyEntity ce : companyEntities) {            
            Company company = Company.builder()
                                        .name(ce.getName())
                                        .ticker(ce.getTicker())
                                        .build();
            ScrapedResult scrapedResult = yahooFinanceScrapper.scrap(company);

            scrapedResult.getDivideneds().stream()
                          .map(e -> new DividenedEntity(ce.getId(), e))
                          .forEach(e -> {
                                boolean exists = dividenedRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                                if(!exists) {
                                    dividenedRepository.save(e);
                                    log.info("insert new dividened -> " + e.toString());
                                }                        
                          });
                                      
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                Thread.currentThread().interrupt();
            }
        }
    }
}