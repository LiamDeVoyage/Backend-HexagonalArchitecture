package com.jaeseung.coffeedelivery.adapter.out.persistence.entity;

import com.jaeseung.coffeedelivery.application.domain.order.LineItem;
import com.jaeseung.coffeedelivery.application.domain.shared.Drink;
import com.jaeseung.coffeedelivery.application.domain.shared.Milk;
import com.jaeseung.coffeedelivery.application.domain.shared.Size;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
public class LineItemEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated
    @NotNull
    private Drink drink;

    @Enumerated
    @NotNull
    private Size size;

    @Enumerated
    @NotNull
    private Milk milk;

    @NotNull
    private Integer quantity;

    public LineItem toDomain() {
        return new LineItem(
                drink,
                milk,
                size,
                quantity
        );
    }

    public static LineItemEntity fromDomain(LineItem lineItem) {
        var entity = new LineItemEntity();
        entity.setDrink(lineItem.drink());
        entity.setQuantity(lineItem.quantity());
        entity.setMilk(lineItem.milk());
        entity.setSize(lineItem.size());
        return entity;
    }
}

