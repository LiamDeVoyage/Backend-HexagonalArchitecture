package com.jaeseung.coffeedelivery.adapter.in.rest;

import com.jaeseung.coffeedelivery.application.port.out.OrdersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static domain.order.OrderTestFactory.anOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RestResourceTest
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrdersRepository ordersRepository;

    private final String paymentJson = """
                        {
                            "cardHolderName": "Michael Faraday",
                            "cardNumber": "11223344",
                            "expiryMonth": 12,
                            "expiryYear": 2023
                        }
                        """;

    @Test
    @DisplayName("/payment/{id} API 테스트합니다.")
    void  payOrder() throws Exception {

        // Given
        var order = ordersRepository.save(anOrder());

        // When
        mockMvc.perform(put("/payment/{id}", order.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(paymentJson))
                .andExpect(status().isOk()); // Then

    }


}
