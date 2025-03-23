package com.jaeseung.coffeedelivery.adapter.out.persistence;

import com.jaeseung.coffeedelivery.adapter.out.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
}

