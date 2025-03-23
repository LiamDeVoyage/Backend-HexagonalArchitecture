package com.jaeseung.coffeedelivery.adapter.in.rest;

import com.jaeseung.coffeedelivery.adapter.in.rest.request.PaymentRequest;
import com.jaeseung.coffeedelivery.adapter.in.rest.response.PaymentResponse;
import com.jaeseung.coffeedelivery.application.domain.payment.CreditCard;
import com.jaeseung.coffeedelivery.application.port.in.OrderingCoffeeUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Month;
import java.time.Year;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PaymentController {
    private final OrderingCoffeeUseCase orderingCoffeeUseCase;

    @PutMapping("/payment/{id}")
    ResponseEntity<PaymentResponse> payOrder(@PathVariable UUID id, @Valid @RequestBody PaymentRequest request) {
        var payment = orderingCoffeeUseCase.payOrder(id,
                new CreditCard(
                        request.cardHolderName(),
                        request.cardNumber(),
                        Month.of(request.expiryMonth()),
                        Year.of(request.expiryYear())
                )
        );
        return ResponseEntity.ok(PaymentResponse.fromDomain(payment));
    }
}