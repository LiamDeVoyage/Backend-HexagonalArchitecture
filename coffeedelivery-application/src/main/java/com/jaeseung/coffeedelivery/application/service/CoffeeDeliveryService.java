package com.jaeseung.coffeedelivery.application.service;

import com.jaeseung.architecture.UseCase;
import com.jaeseung.coffeedelivery.application.domain.order.Order;
import com.jaeseung.coffeedelivery.application.port.in.DeliveringCoffeeUseCase;
import com.jaeseung.coffeedelivery.application.port.out.OrdersRepository;

import java.util.UUID;

@UseCase
public class CoffeeDeliveryService implements DeliveringCoffeeUseCase {

    private final OrdersRepository orders;

    public CoffeeDeliveryService(OrdersRepository orders) { this.orders = orders; }

    @Override
    public Order startDeliveringOrder(UUID orderId) {
        var order = orders.findOrderById(orderId);

        return orders.save(order.markBeingDelivered());
    }

    @Override
    public Order finishDeliveringOrder(UUID orderId) {
        var order = orders.findOrderById(orderId);

        return orders.save(order.markDelivered());
    }
}
