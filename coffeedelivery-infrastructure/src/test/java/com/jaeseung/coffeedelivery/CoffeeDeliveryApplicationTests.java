package com.jaeseung.coffeedelivery;

import com.jaeseung.coffeedelivery.application.service.CoffeeMachineService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CoffeeDeliveryApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(CoffeeDeliveryApplicationTests.class);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CoffeeMachineService coffeeMachineService;

    @Test
    @DisplayName("주문을 접수하고, 주문을 결제하고, 주문을 준비하고, 주문 영수증을 읽고, 주문 완료하는 통합 테스트를 진행합니다.")
    void processNewOrder() throws Exception {
        var orderId = placeOrder();
        payOrder(orderId);
        prepareOrder(orderId);
        readReceipt(orderId);
        takeOrder(orderId);
    }

    @Test
    void cancelOrderBeforePayment() throws Exception {
        var orderId = placeOrder();
        cancelOrder(orderId);
    }

    private UUID placeOrder() throws Exception {
        var location = mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                        {
                            "location": "IN_STORE",
                            "items": [{
                                "drink": "LATTE",
                                "quantity": 1,
                                "milk": "WHOLE",
                                "size": "LARGE"
                            }]
                        }
                        """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader(HttpHeaders.LOCATION);

        return location == null ? null : UUID.fromString(location.substring(location.lastIndexOf("/") + 1));
    }

    private void cancelOrder(UUID orderId) throws Exception {
        mockMvc.perform(delete("/order/{id}", orderId))
                .andExpect(status().isNoContent());
    }

    private void payOrder(UUID orderId) throws Exception {
        mockMvc.perform(put("/payment/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                        {
                            "cardHolderName": "Michael Faraday",
                            "cardNumber": "11223344",
                            "expiryMonth": 12,
                            "expiryYear": 2023
                        }
                        """))
                .andExpect(status().isOk());
    }

    private void prepareOrder(UUID orderId) {
        coffeeMachineService.startPreparingOrder(orderId);
        coffeeMachineService.finishPreparingOrder(orderId);
    }

    private void readReceipt(UUID orderId) throws Exception {
        mockMvc.perform(get("/receipt/{id}", orderId))
                .andExpect(status().isOk());
    }

    private void takeOrder(UUID orderId) throws Exception {
        mockMvc.perform(delete("/receipt/{id}", orderId))
                .andExpect(status().isOk());
    }

}
