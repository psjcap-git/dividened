package com.zerobase.dividened.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScrapedResult {
    private Company company;
    private List<Dividened> divideneds;

    public ScrapedResult() {
        divideneds = new ArrayList<>();
    }
}