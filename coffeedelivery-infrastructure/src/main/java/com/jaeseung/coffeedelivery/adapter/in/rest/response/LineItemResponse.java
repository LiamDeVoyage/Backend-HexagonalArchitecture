package com.jaeseung.coffeedelivery.adapter.in.rest.response;

import com.jaeseung.coffeedelivery.application.domain.order.LineItem;
import com.jaeseung.coffeedelivery.application.domain.shared.Drink;
import com.jaeseung.coffeedelivery.application.domain.shared.Milk;
import com.jaeseung.coffeedelivery.application.domain.shared.Size;

public record LineItemResponse(Drink drink, Milk milk, Size size, Integer quantity) {
    public static LineItemResponse fromDomain(LineItem lineItem) {
        return new LineItemResponse(
                lineItem.drink(),
                lineItem.milk(), lineItem.size(), lineItem.quantity()
        );
    }
}

