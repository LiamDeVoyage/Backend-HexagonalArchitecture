package com.jaeseung.coffeedelivery.adapter.in.rest.response;

import com.jaeseung.coffeedelivery.application.domain.order.Order;
import com.jaeseung.coffeedelivery.application.domain.shared.Location;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(Location location, List<LineItemResponse> items, BigDecimal cost) {
    public static OrderResponse fromDomain(Order order) {
        return new OrderResponse(
                order.getLocation(),
                order.getItems().stream().map(LineItemResponse::fromDomain).toList(),
                order.getCost()
        );
    }
}
