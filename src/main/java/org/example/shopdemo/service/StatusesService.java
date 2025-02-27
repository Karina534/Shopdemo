package org.example.shopdemo.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.shopdemo.entity.Statuses;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatusesService {
    private final static StatusesService INSTANCE = new StatusesService();

    public List<Statuses> findAll(){
        return Arrays.asList(Statuses.values());
    }

    public static StatusesService getInstance(){
        return INSTANCE;
    }
}
