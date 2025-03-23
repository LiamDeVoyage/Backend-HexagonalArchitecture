package com.jaeseung.coffeedelivery.adapter.in.rest;

import com.jaeseung.coffeedelivery.adapter.in.rest.response.OrderResponse;
import com.jaeseung.coffeedelivery.adapter.in.rest.response.ReceiptResponse;
import com.jaeseung.coffeedelivery.application.port.in.OrderingCoffeeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ReceiptController {
    private final OrderingCoffeeUseCase orderingCoffeeUseCase;

    @GetMapping("/receipt/{id}")
    ResponseEntity<ReceiptResponse> readReceipt(@PathVariable UUID id) {
        var receipt = orderingCoffeeUseCase.readReceipt(id);
        return ResponseEntity.ok(ReceiptResponse.fromDomain(receipt));
    }

    @DeleteMapping("/receipt/{id}")
    ResponseEntity<OrderResponse> completeOrder(@PathVariable UUID id) {
        var order = orderingCoffeeUseCase.takeOrder(id);
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }
}
