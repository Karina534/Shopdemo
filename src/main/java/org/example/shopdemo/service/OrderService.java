package org.example.shopdemo.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.dao.ConsumersDao;
import org.example.shopdemo.dao.OrdersDao;
import org.example.shopdemo.dto.OrdersDto;
import org.example.shopdemo.entity.Orders;
import org.example.shopdemo.entity.Statuses;
import org.example.shopdemo.exception.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderService {
    private final static OrderService INSTANCE = new OrderService();
    private final OrdersDao ordersDao = OrdersDao.getInstance();
    private final ConsumersDao consumersDao = ConsumersDao.getInstance();

    public Orders saveOrder(OrdersDto ordersDto){
        log.info("Start finding ConsumerDto by id: {} using consumerService.", ordersDto.getConsumerId());
        var consumer = consumersDao.findById(ordersDto.getConsumerId());

        if (consumer.isEmpty()){
            log.debug("Consumer wasn't found by this id: {}", ordersDto.getConsumerId());
            throw new EntityNotFoundException("Consumer with id: " + ordersDto.getConsumerId() + " wasn't found.");
        }

        Orders orders = new Orders(null,
                ordersDto.getTotalPrice(),
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                Statuses.CREATED,
                consumer.get(),
                ordersDto.getAddress());

        log.info("Order object successfully built and send to dao for saving.");
        return ordersDao.save(orders);
    }

    public List<Orders> findAllByConsumerId(Long consumerId){
        return ordersDao.findOrdersByConsumerId(consumerId);
    }

    public List<OrdersDto> findAllOrders(){
        List<Orders> orders = ordersDao.findAll();
        return orders.stream().map(o -> OrdersDto.builder()
                .orderId(o.getOrderId())
                .totalPrice(o.getTotalPrice())
                .creationDate(o.getCreationDate())
                .endDate(o.getEndDate())
                .statusId(o.getStatus().getId())
                .consumerId(o.getConsumer().getConsumerId())
                .address(o.getAddress())
                .build()).collect(Collectors.toList());
    }

    public OrdersDto getByOrderId(Long orderId){
        log.info("Start finding order by id: {}", orderId);
        Optional<Orders> order = ordersDao.findById(orderId);

        if (order.isEmpty()){
            log.debug("Order wasn't found in db by id: {}", orderId);
            throw new EntityNotFoundException("Order wasn't found by given id: " + orderId);
        }

        log.info("Order was successfully found in db by id {}, sending orderDto to servlet.", orderId);
        return OrdersDto.builder()
                .orderId(order.get().getOrderId())
                .totalPrice(order.get().getTotalPrice())
                .creationDate(order.get().getCreationDate())
                .endDate(order.get().getEndDate())
                .statusId(order.get().getStatus().getId())
                .consumerId(order.get().getConsumer().getConsumerId())
                .address(order.get().getAddress()).build();
    }

    public boolean updateOrderById(Long orderId, Integer statusId){
        log.info("Getting order by order id: {}", orderId);
        Optional<Orders> order = ordersDao.findById(orderId);

        if (order.isEmpty()){
            log.debug("Order wasn't found in db by id: {}", orderId);
            throw new EntityNotFoundException("Order wasn't found by given id: " + orderId);
        }

        log.info("Order by id {} was successfully found. Setting status with id: {}", orderId, statusId);
        order.get().setStatus(Statuses.fromId(statusId));
        return ordersDao.update(order.get());
    }

    public static OrderService getInstance(){
        return INSTANCE;
    }
}
