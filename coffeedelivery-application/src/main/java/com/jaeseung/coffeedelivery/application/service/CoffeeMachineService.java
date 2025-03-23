package com.jaeseung.coffeedelivery.application.service;

import com.jaeseung.architecture.UseCase;
import com.jaeseung.coffeedelivery.application.domain.order.Order;
import com.jaeseung.coffeedelivery.application.port.in.PreparingCoffeeUseCase;
import com.jaeseung.coffeedelivery.application.port.out.OrdersRepository;

import java.util.UUID;

@UseCase
public class CoffeeMachineService implements PreparingCoffeeUseCase {

    private final OrdersRepository orders;

    public CoffeeMachineService(OrdersRepository orders) { this.orders = orders; }

    @Override
    public Order startPreparingOrder(UUID orderId) {
        var order = orders.findOrderById(orderId);

        return orders.save(order.markBeingPrepared());
    }

    @Override
    public Order finishPreparingOrder(UUID orderId) {
        var order = orders.findOrderById(orderId);

        return orders.save(order.markPrepared());
    }
}
