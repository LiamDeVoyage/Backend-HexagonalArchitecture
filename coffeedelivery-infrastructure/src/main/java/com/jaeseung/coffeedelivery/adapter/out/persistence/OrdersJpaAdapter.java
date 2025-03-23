package com.jaeseung.coffeedelivery.adapter.out.persistence;

import com.jaeseung.coffeedelivery.adapter.out.persistence.entity.OrderEntity;
import com.jaeseung.coffeedelivery.application.domain.order.Order;
import com.jaeseung.coffeedelivery.application.port.out.OrderNotFound;
import com.jaeseung.coffeedelivery.application.port.out.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrdersJpaAdapter implements OrdersRepository {
    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order findOrderById(UUID orderId) {
        return orderJpaRepository.findById(orderId)
                .map(OrderEntity::toDomain)
                .orElseThrow(OrderNotFound::new);
    }

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(OrderEntity.fromDomain(order)).toDomain();
    }

    @Override
    public void deleteById(UUID orderId) {
        orderJpaRepository.deleteById(orderId);
    }
}
