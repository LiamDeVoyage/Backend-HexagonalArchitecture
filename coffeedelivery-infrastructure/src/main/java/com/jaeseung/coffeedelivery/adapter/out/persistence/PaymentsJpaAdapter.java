package com.jaeseung.coffeedelivery.adapter.out.persistence;

import com.jaeseung.coffeedelivery.adapter.out.persistence.entity.PaymentEntity;
import com.jaeseung.coffeedelivery.application.domain.payment.Payment;
import com.jaeseung.coffeedelivery.application.port.out.PaymentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentsJpaAdapter implements PaymentsRepository {
    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment findPaymentByOrderId(UUID orderId) {
        return paymentJpaRepository.findByOrderId(orderId)
                .map(PaymentEntity::toDomain)
                .orElseThrow();
    }

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(PaymentEntity.fromDomain(payment)).toDomain();
    }
}
