package com.jaeseung.coffeedelivery.application.port.in;

import com.jaeseung.coffeedelivery.application.domain.order.Order;

import java.util.UUID;

public interface PreparingCoffeeUseCase {
    Order startPreparingOrder(UUID orderId);
    Order finishPreparingOrder(UUID orderId);
}
