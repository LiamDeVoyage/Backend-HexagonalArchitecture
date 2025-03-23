package com.jaeseung.coffeedelivery.adapter.in.rest.request;

import com.jaeseung.coffeedelivery.application.domain.order.Order;
import com.jaeseung.coffeedelivery.application.domain.shared.Location;

import java.util.List;

public record OrderRequest(Location location, List<LineItemRequest> items) {
    public Order toDomain() {
        return new Order(location, items.stream().map(LineItemRequest::toDomain).toList());
    }
}
