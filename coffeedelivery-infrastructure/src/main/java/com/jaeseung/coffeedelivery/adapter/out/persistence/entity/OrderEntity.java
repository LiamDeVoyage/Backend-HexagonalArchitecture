package com.jaeseung.coffeedelivery.adapter.out.persistence.entity;

import com.jaeseung.coffeedelivery.application.domain.order.Order;
import com.jaeseung.coffeedelivery.application.domain.shared.Location;
import com.jaeseung.coffeedelivery.application.domain.shared.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
public class OrderEntity {
    @Id
    private UUID id;

    @Enumerated
    @NotNull
    private Location location;

    @Enumerated
    @NotNull
    private Status status;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<LineItemEntity> items;

    public Order toDomain() {
        return new Order(
                id,
                location,
                items.stream().map(LineItemEntity::toDomain).toList(),
                status
        );
    }

    public static OrderEntity fromDomain(Order order) {
        var entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setLocation(order.getLocation());
        entity.setStatus(order.getStatus());
        entity.setItems(order.getItems().stream().map(LineItemEntity::fromDomain).toList());
        return entity;
    }
}
