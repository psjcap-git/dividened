package com.zerobase.dividened.scrapper;

import java.io.IOException;
import java.time.LocalDateTime;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.zerobase.dividened.model.Company;
import com.zerobase.dividened.model.Dividened;
import com.zerobase.dividened.model.ScrapedResult;
import com.zerobase.dividened.model.constant.Month;

@Component
public class YahooFinanceScrapper implements Scrapper {
    private final static String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=capitalGain%%7Cdiv%%7Csplit&filter=div&frequency=1mo&includeAdjustedClose=true";
    private final static String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";

    private final static long START_TIME = 86400L;

    @Override
    public Company scrapCompanyByTicker(String ticker) {       

        try {
            String url = String.format(SUMMARY_URL, ticker, ticker);
            Document document = Jsoup.connect(url).get();

            Element element = document.getElementsByTag("h1").get(0);
            String title = element.text().split(" - ")[1].trim();            

            return Company.builder()
                            .ticker(ticker)
                            .name(title)
                            .build();
       } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ScrapedResult scrap(Company company) {
        ScrapedResult result = new ScrapedResult();
        
        try {
            long endTime = System.currentTimeMillis() / 1000L;
            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, endTime);

            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements elements = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableElement = elements.get(0);
            Element tbodyElement = tableElement.children().get(1);
            for(Element trElement : tbodyElement.children()) {
                Element tdElement0 = trElement.children().get(0);
                String dateText = tdElement0.text();
                String[] splits = dateText.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);

                if(month < 0) {
                    throw new RuntimeException("Unexpected Month enum Value");
                }
                LocalDateTime date = LocalDateTime.of(year, month, day, 0, 0);

                Element tdElement1 = trElement.children().get(1);                
                String dividenedText = tdElement1.text();

                result.setCompany(company);
                result.getDivideneds().add(Dividened.builder()
                                                    .date(date)
                                                    .dividened(dividenedText)
                                                    .build());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}