package org.example.shopdemo.service;

import org.example.shopdemo.dao.ConsumersDao;
import org.example.shopdemo.dto.ConsumerDto;
import org.example.shopdemo.entity.Consumers;
import org.example.shopdemo.entity.Currencies;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
