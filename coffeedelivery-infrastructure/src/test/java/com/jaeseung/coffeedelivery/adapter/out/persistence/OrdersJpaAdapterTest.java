package com.jaeseung.coffeedelivery.adapter.out.persistence;

import com.jaeseung.coffeedelivery.application.domain.order.LineItem;
import com.jaeseung.coffeedelivery.application.domain.order.Order;
import com.jaeseung.coffeedelivery.application.domain.shared.Drink;
import com.jaeseung.coffeedelivery.application.domain.shared.Location;
import com.jaeseung.coffeedelivery.application.domain.shared.Milk;
import com.jaeseung.coffeedelivery.application.domain.shared.Size;
import com.jaeseung.coffeedelivery.application.port.out.OrderNotFound;
import com.jaeseung.coffeedelivery.application.port.out.OrdersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@PersistenceTest
public class OrdersJpaAdapterTest {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;


    @Test
    @DisplayName("ordersRepository에 save를 테스트합니다.")
    void creatingOrderReturnsPersistedOrder() {
        // Given
        var order = new Order(Location.TAKE_AWAY, List.of(
                new LineItem(Drink.LATTE, Milk.WHOLE, Size.SMALL, 1)
        ));

        // When
        var persistedOrder = ordersRepository.save(order);

        // Then
        assertThat(persistedOrder.getLocation()).isEqualTo(Location.TAKE_AWAY);
        assertThat(persistedOrder.getItems()).containsExactly(
                new LineItem(Drink.LATTE, Milk.WHOLE, Size.SMALL, 1)
        );
    }

    @Test
    @Sql("classpath:data/order.sql")
    @DisplayName("ordersRepository에 조회를 테스트합니다.")
    void findingPreviouslyPersistedOrderReturnsDetails() {
        // When
        var order = ordersRepository.findOrderById(UUID.fromString("757d9c0f-400f-4137-9aea-83e64ba3efb2"));
        // Then
        assertThat(order.getLocation()).isEqualTo(Location.IN_STORE);
        assertThat(order.getItems()).containsExactly(new LineItem(Drink.ESPRESSO, Milk.SKIMMED, Size.LARGE, 1));
    }

    @Test
    @DisplayName("ordersRepository에 예외처리를 테스트합니다.")
    void findingNonExistingOrderThrowsException() {
        // Then
        assertThatThrownBy(() -> ordersRepository.findOrderById(UUID.randomUUID())).isInstanceOf(OrderNotFound.class);
    }

    @Test
    @Sql("classpath:data/order.sql")
    @DisplayName("ordersRepository에 delete를 테스트합니다.")
    void deletesPreviouslyPersistedOrder() {
        ordersRepository.deleteById(UUID.fromString("757d9c0f-400f-4137-9aea-83e64ba3efb2"));

        assertThat(orderJpaRepository.findById(UUID.fromString("757d9c0f-400f-4137-9aea-83e64ba3efb2"))).isEmpty();
    }

}
