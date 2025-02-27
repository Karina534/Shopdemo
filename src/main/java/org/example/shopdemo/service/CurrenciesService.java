package org.example.shopdemo.service;

import org.example.shopdemo.entity.Currencies;

import java.util.Arrays;
import java.util.List;

public class CurrenciesService {
    private final static CurrenciesService INSTANCE = new CurrenciesService();

    public List<Currencies> findAll(){
        return Arrays.asList(Currencies.values());
    }

    public static CurrenciesService getInstance(){
        return INSTANCE;
    }

    private CurrenciesService() {
    }
}
