package com.jaeseung.coffeedelivery.application.port.in;

import com.jaeseung.coffeedelivery.application.domain.order.Order;

import java.util.UUID;

public interface DeliveringCoffeeUseCase {
    Order startDeliveringOrder(UUID orderId);
    Order finishDeliveringOrder(UUID orderId);
}
