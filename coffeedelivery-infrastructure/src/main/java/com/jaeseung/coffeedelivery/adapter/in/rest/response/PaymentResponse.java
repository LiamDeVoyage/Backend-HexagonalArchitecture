package com.jaeseung.coffeedelivery.adapter.in.rest.response;

import com.jaeseung.coffeedelivery.application.domain.payment.Payment;

public record PaymentResponse(String cardHolderName, String cardNumber, Integer expiryMonth, Integer expiryYear) {
    public static PaymentResponse fromDomain(Payment payment) {
        var creditCard = payment.creditCard();
        return new PaymentResponse(
                creditCard.cardHolderName(),
                creditCard.cardNumber(),
                creditCard.expiryMonth().getValue(),
                creditCard.expiryYear().getValue()
        );
    }
}
