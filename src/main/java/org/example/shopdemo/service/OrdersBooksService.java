package org.example.shopdemo.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.shopdemo.dao.OrdersBooksDao;
import org.example.shopdemo.entity.OrdersBooks;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrdersBooksService {
    private final static OrdersBooksService INSTANCE = new OrdersBooksService();
    private final OrdersBooksDao ordersBooksDao = OrdersBooksDao.getInstance();

    public void saveOrderBook(OrdersBooks ordersBooks){
        ordersBooksDao.save(ordersBooks);
    }

    public List<OrdersBooks> findAllByOrderId(Long orderId){
        return ordersBooksDao.findAllByOrderId(orderId);
    }

    public static OrdersBooksService getInstance(){
        return INSTANCE;
    }
}
