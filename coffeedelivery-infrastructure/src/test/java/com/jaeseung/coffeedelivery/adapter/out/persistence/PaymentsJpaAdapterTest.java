package com.jaeseung.coffeedelivery.adapter.out.persistence;

import com.jaeseung.coffeedelivery.application.domain.payment.CreditCard;
import com.jaeseung.coffeedelivery.application.domain.payment.Payment;
import com.jaeseung.coffeedelivery.application.port.out.PaymentsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.NoSuchElementException;
import java.util.UUID;

import static domain.payment.CreditCardTestFactory.aCreditCard;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class PaymentsJpaAdapterTest {

    @Autowired
    private PaymentsRepository paymentsRepository;

    @Test
    @DisplayName("paymentsRepository save를 테스트합니다.")
    void creatingPaymentReturnsPersistedPayment() {
        // Given
        var now = LocalDate.now();
        var creditCard = aCreditCard();
        var payment = new Payment(UUID.randomUUID(), creditCard, now);

        // When
        var persistedPayment = paymentsRepository.save(payment);

        // Then
        assertThat(persistedPayment.creditCard()).isEqualTo(creditCard);
        assertThat(persistedPayment.paid()).isEqualTo(now);
    }

    @Test
    @Sql("classpath:data/payment.sql")
    @DisplayName("paymentsRepository 조회를 테스트합니다.")
    void findingPreviouslyPersistedPaymentReturnsDetails() {

        //When
        var payment = paymentsRepository.findPaymentByOrderId(UUID.fromString("a41c9394-3aa6-4484-b0b4-87de55fa2cf4"));
        // Given
        var expectedCreditCard = new CreditCard("Michael Faraday", "11223344", Month.JANUARY, Year.of(2023));
        // Then
        assertThat(payment.creditCard()).isEqualTo(expectedCreditCard);
    }

    @Test
    @DisplayName("paymentsRepository 예외처리를 테스트합니다.")
    void findingNonExistingPaymentThrowsException() {
        // Then
        assertThatThrownBy(() -> paymentsRepository.findPaymentByOrderId(UUID.randomUUID())).isInstanceOf(NoSuchElementException.class);
    }

}
