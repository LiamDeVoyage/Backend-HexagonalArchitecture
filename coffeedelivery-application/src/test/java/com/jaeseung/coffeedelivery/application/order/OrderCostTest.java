package com.jaeseung.coffeedelivery.application.order;

import com.jaeseung.coffeedelivery.application.domain.order.LineItem;
import com.jaeseung.coffeedelivery.application.domain.order.Order;
import com.jaeseung.coffeedelivery.application.domain.shared.Drink;
import com.jaeseung.coffeedelivery.application.domain.shared.Location;
import com.jaeseung.coffeedelivery.application.domain.shared.Milk;
import com.jaeseung.coffeedelivery.application.domain.shared.Size;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class OrderCostTest {

    // 음료 가격의 테스트 데이터
    private static Stream<Arguments> drinkCosts() {
        return Stream.of(
                arguments(1, Size.SMALL, BigDecimal.valueOf(4.0)),
                arguments(1, Size.LARGE, BigDecimal.valueOf(5.0)),
                arguments(2, Size.SMALL, BigDecimal.valueOf(8.0))
        );
    }

    @ParameterizedTest(name = "{0} drinks of size {1} cost {2}")
    @MethodSource("drinkCosts")
    @DisplayName("주문 수량 및 사이즈에 대한 예상 금액이 맞는지 확인합니다.")
    void orderCostBasedOnQuantityAndSize(int quantity, Size size, BigDecimal expectedCost) {

        // Given
        var order = new Order(Location.TAKE_AWAY, List.of(
                new LineItem(Drink.LATTE, Milk.WHOLE, size, quantity)
        ));

        assertThat(// When
                order.getCost()
        //Then
        ).isEqualTo(expectedCost);
    }

    @Test
    @DisplayName("주문 가격을 확인합니다.")
    void orderCostIsSumOfLineItemCosts() {

        // Given
        var order = new Order(Location.TAKE_AWAY, List.of(
                new LineItem(Drink.LATTE, Milk.SKIMMED, Size.LARGE, 1),
                new LineItem(Drink.ESPRESSO, Milk.SOY, Size.SMALL, 1)
        ));

        assertThat(// When
                order.getCost())
                // Then
                .isEqualTo(BigDecimal.valueOf(9.0));
    }



}
