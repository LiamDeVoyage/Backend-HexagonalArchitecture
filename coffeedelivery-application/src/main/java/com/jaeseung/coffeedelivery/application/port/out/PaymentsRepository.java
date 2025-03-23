package com.jaeseung.coffeedelivery.application.port.out;

import com.jaeseung.coffeedelivery.application.domain.payment.Payment;

import java.util.UUID;

public interface PaymentsRepository {
    Payment findPaymentByOrderId(UUID orderId);
    Payment save(Payment payment);
}
