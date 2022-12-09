package com.zerobase.dividened.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.zerobase.dividened.exception.impl.NoCompanyException;
import com.zerobase.dividened.model.Company;
import com.zerobase.dividened.model.Dividened;
import com.zerobase.dividened.model.ScrapedResult;
import com.zerobase.dividened.model.constant.CacheKey;
import com.zerobase.dividened.persist.entity.CompanyEntity;
import com.zerobase.dividened.persist.entity.DividenedEntity;
import com.zerobase.dividened.persist.repository.CompanyRepository;
import com.zerobase.dividened.persist.repository.DividenedRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividenedRepository dividenedRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividenedByCompanyName(String companyName) {
        CompanyEntity companyEntity = companyRepository.findByName(companyName)
                                            .orElseThrow(() -> new NoCompanyException());

        List<DividenedEntity> dividenedEntities = dividenedRepository.findAllByCompanyId(companyEntity.getId());
        List<Dividened> dividenedList = dividenedEntities.stream().map(c -> new Dividened(c.getDate(), c.getDividened())).collect(Collectors.toList());

        return new ScrapedResult(
            Company.builder()
                .name(companyEntity.getName())
                .ticker(companyEntity.getTicker())
                .build(),        
                dividenedList);
        
    }
}
