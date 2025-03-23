package com.jaeseung.coffeedelivery.application.domain.order;

import com.jaeseung.coffeedelivery.application.domain.shared.Drink;
import com.jaeseung.coffeedelivery.application.domain.shared.Milk;
import com.jaeseung.coffeedelivery.application.domain.shared.Size;

import java.math.BigDecimal;

public record LineItem(Drink drink, Milk milk, Size size, int quantity) {
    // 간단하게 Small 사이즈는 4 / Large 사이즈는 5
    BigDecimal getCost() {
        var price = BigDecimal.valueOf(4.0);
        if (size == Size.LARGE) {
            price = price.add(BigDecimal.ONE);
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}