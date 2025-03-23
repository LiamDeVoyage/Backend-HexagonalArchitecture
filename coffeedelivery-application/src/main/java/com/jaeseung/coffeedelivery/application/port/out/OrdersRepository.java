package com.jaeseung.coffeedelivery.application.port.out;

import com.jaeseung.coffeedelivery.application.domain.order.Order;

import java.util.UUID;

public interface OrdersRepository {
    Order findOrderById(UUID orderId) throws OrderNotFound;
    Order save(Order order);
    void deleteById(UUID orderId);
}
