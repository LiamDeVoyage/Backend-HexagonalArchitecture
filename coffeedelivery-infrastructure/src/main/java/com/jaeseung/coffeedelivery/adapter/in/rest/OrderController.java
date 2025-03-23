package com.jaeseung.coffeedelivery.adapter.in.rest;

import com.jaeseung.coffeedelivery.adapter.in.rest.request.OrderRequest;
import com.jaeseung.coffeedelivery.adapter.in.rest.response.OrderResponse;
import com.jaeseung.coffeedelivery.application.port.in.OrderingCoffeeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderingCoffeeUseCase orderingCoffeeUseCase;

    @PostMapping("/order")
    ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request, UriComponentsBuilder uriComponentsBuilder) {
        var order = orderingCoffeeUseCase.placeOrder(request.toDomain());
        var location = uriComponentsBuilder.path("/order/{id}")
                .buildAndExpand(order.getId())
                .toUri();
        return ResponseEntity.created(location).body(OrderResponse.fromDomain(order));
    }

    @PostMapping("/order/{id}")
    ResponseEntity<OrderResponse> updateOrder(@PathVariable UUID id, @RequestBody OrderRequest request) {
        var order = orderingCoffeeUseCase.updateOrder(id, request.toDomain());
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }

    @DeleteMapping("/order/{id}")
    ResponseEntity<Void> cancelOrder(@PathVariable UUID id) {
        orderingCoffeeUseCase.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}
