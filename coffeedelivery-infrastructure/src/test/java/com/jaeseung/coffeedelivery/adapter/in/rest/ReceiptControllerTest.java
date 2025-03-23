package com.jaeseung.coffeedelivery.adapter.in.rest;

import com.jaeseung.coffeedelivery.application.port.out.OrdersRepository;
import com.jaeseung.coffeedelivery.application.port.out.PaymentsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static domain.order.OrderTestFactory.aReadyOrder;
import static domain.order.OrderTestFactory.anOrder;
import static domain.payment.PaymentTestFactory.aPaymentForOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RestResourceTest
public class ReceiptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private PaymentsRepository paymentsRepository;

    @Test
    @DisplayName("get /receipt/{id} API 테스트합니다.")
    void readReceipt() throws Exception {

        // Given
        var order = ordersRepository.save(anOrder());

        // When
        paymentsRepository.save(aPaymentForOrder(order));

        mockMvc.perform(get("/receipt/{id}", order.getId()))
        .andExpect(status().isOk());
    }

    @Test
    @DisplayName("delete /receipt/{id} API 테스트합니다.")
    void completeOrder() throws Exception {

        var order = ordersRepository.save(aReadyOrder());

        mockMvc.perform(delete("/receipt/{id}", order.getId()))
                .andExpect(status().isOk());

    }



}
