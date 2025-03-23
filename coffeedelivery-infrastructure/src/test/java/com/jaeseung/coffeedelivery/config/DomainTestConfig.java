package com.jaeseung.coffeedelivery.config;

import com.jaeseung.coffeedelivery.application.port.out.OrdersRepository;
import com.jaeseung.coffeedelivery.application.port.out.PaymentsRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import port.out.InMemoryOrdersRepository;
import port.out.InMemoryPaymentsRepository;

@TestConfiguration
@Import(DomainConfig.class)
public class DomainTestConfig {
    @Bean
    OrdersRepository ordersRepository() {
        return new InMemoryOrdersRepository();
    }

    @Bean
    PaymentsRepository paymentsRepository() {
        return new InMemoryPaymentsRepository();
    }
}

