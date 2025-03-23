package com.jaeseung.coffeedelivery.adapter.in.rest.request;

import com.jaeseung.coffeedelivery.application.domain.order.LineItem;
import com.jaeseung.coffeedelivery.application.domain.shared.Drink;
import com.jaeseung.coffeedelivery.application.domain.shared.Milk;
import com.jaeseung.coffeedelivery.application.domain.shared.Size;

public record LineItemRequest(Drink drink, Milk milk, Size size, Integer quantity) {
    public LineItem toDomain() {
        return new LineItem(drink, milk, size, quantity);
    }
}
