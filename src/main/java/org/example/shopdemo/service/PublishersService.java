package org.example.shopdemo.service;

import org.example.shopdemo.dao.PublishersDao;
import org.example.shopdemo.dto.PublishersDto;
import org.example.shopdemo.entity.Publishers;

import java.util.ArrayList;
import java.util.List;

public class PublishersService {
    private final static PublishersService INSTANCE = new PublishersService();
    private final PublishersDao publishersDao = PublishersDao.getInstance();

    public List<PublishersDto> findAll(){
        List<Publishers> publishersAll = publishersDao.findAll();
        List<PublishersDto> publishersDtoList = new ArrayList<>();
        for (Publishers publisher : publishersAll){
            publishersDtoList.add(
                    PublishersDto
                            .builder()
                            .publisherId(publisher.getPublisherId())
                            .name(publisher.getName())
                            .build()
            );
        }
        return publishersDtoList;
    }

    public static PublishersService getInstance(){
        return INSTANCE;
    }
    private PublishersService() {
    }
}
