package com.jaeseung.coffeedelivery.adapter.in.rest.response;

import com.jaeseung.coffeedelivery.application.domain.payment.Receipt;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReceiptResponse(BigDecimal amount, LocalDate paid) {
    public static ReceiptResponse fromDomain(Receipt receipt) {
        return new ReceiptResponse(receipt.amount(), receipt.paid());
    }
}
