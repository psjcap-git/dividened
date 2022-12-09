package com.zerobase.dividened.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.zerobase.dividened.exception.impl.AlreadyExistTickerException;
import com.zerobase.dividened.exception.impl.NoCompanyException;
import com.zerobase.dividened.model.Company;
import com.zerobase.dividened.model.ScrapedResult;
import com.zerobase.dividened.persist.entity.CompanyEntity;
import com.zerobase.dividened.persist.entity.DividenedEntity;
import com.zerobase.dividened.persist.repository.CompanyRepository;
import com.zerobase.dividened.persist.repository.DividenedRepository;
import com.zerobase.dividened.scrapper.YahooFinanceScrapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final DividenedRepository dividenedRepository;
    private final YahooFinanceScrapper scrapper;
    private final Trie<String, String> trie;
    
    public Company save(String ticker) {
        boolean exists = companyRepository.existsByTicker(ticker);
        if(exists) {
            //throw new RuntimeException("already exists ticker -> " + ticker);
            throw new AlreadyExistTickerException();
        }

        return storeCompanyAndDividened(ticker);
    }

    @Transactional
    private Company storeCompanyAndDividened(String ticker) {
        Company company = scrapper.scrapCompanyByTicker(ticker);
        if(ObjectUtils.isEmpty(company)) {
            //throw new RuntimeException("failed to scrap ticker ->" + ticker);
            throw new NoCompanyException();
        }        

        ScrapedResult scrapResult = scrapper.scrap(company);

        CompanyEntity companyEntity = companyRepository.save(new CompanyEntity(company));

        List<DividenedEntity> dividenedEntities = scrapResult.getDivideneds().stream().map(e -> new DividenedEntity(companyEntity.getId(), e)).collect(Collectors.toList());
        dividenedRepository.saveAll(dividenedEntities);

        return company;
    }

    public Page<CompanyEntity> getAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    public void addAutoCompleteKeyword(String keyword) {
        trie.put(keyword, null);
    }

    public List<String> autoComplete(String prefix) {
        return trie.prefixMap(prefix).keySet().stream().collect(Collectors.toList());
    }

    public void deleteAutoCompleteKeyword(String keyword) {
        trie.remove(keyword);
    }

    public List<String> getCompanyNamesByKeyword(String keyword) {        
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntities = companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream().map(c -> c.getName()).collect(Collectors.toList());
    }

    @Transactional
    public String deleteCompany(String ticker) {
        CompanyEntity companyEntity = companyRepository.findByTicker(ticker).orElseThrow(() -> new NoCompanyException());

        dividenedRepository.deleteAllByCompanyId(companyEntity.getId());
        companyRepository.delete(companyEntity);

        this.deleteAutoCompleteKeyword(companyEntity.getName());        
        
        return companyEntity.getName();
    }
}
