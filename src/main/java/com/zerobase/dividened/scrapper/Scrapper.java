package com.zerobase.dividened.scrapper;

import com.zerobase.dividened.model.Company;
import com.zerobase.dividened.model.ScrapedResult;

public interface Scrapper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);    
}
