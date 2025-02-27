package org.example.shopdemo.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.shopdemo.dao.BasketsDao;
import org.example.shopdemo.dto.ConsumerDto;
import org.example.shopdemo.entity.Baskets;
import org.example.shopdemo.entity.Consumers;
import org.example.shopdemo.exception.EntityNotFoundException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BasketService {
    private final static BasketService INSTANCE = new BasketService();
    private final BasketsDao basketsDao = BasketsDao.getInstance();

    public boolean createBasket(ConsumerDto consumerDto){
        Consumers consumers = new Consumers(
                consumerDto.getConsumerId(),
                consumerDto.getConsumerName(),
                consumerDto.getSurname(),
                consumerDto.getEmail(),
                consumerDto.getPassword(),
                consumerDto.getTelephone(),
                consumerDto.getRole());
        Baskets basket = new Baskets(null, consumers);
        Baskets baskets2 = basketsDao.save(basket);
        return baskets2.getBasketId() != null;
    }

    public Long findByConsumerId(Long id){
        var basket = basketsDao.findByConsumerId(id);
        if (basket.isEmpty()){
            throw new EntityNotFoundException("Basket wasn't found for this consumer.");
        }
        return basket.get().getBasketId();
    }

    public static BasketService getInstance(){
        return INSTANCE;
    }
}
