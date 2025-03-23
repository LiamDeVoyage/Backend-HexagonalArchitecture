package com.jaeseung.coffeedelivery.adapter.in.rest;

import com.jaeseung.coffeedelivery.application.port.out.OrdersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static domain.order.OrderTestFactory.anOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RestResourceTest
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrdersRepository ordersRepository;

    private final String orderJson = """
                        {
                            "location": "IN_STORE",
                            "items": [{
                                "drink": "LATTE",
                                "quantity": 1,
                                "milk": "WHOLE",
                                "size": "LARGE"
                            }]
                        }
                        """;

    @Test
    @DisplayName("/order API 테스트합니다.")
    void createOrder() throws Exception {
        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(orderJson))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("update /order/{id} API 테스트합니다")
    void updateOrder() throws Exception {
        var order = ordersRepository.save(anOrder());

        mockMvc.perform(post("/order/{id}", order.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(orderJson))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("delete /order/{id} API 테스트합니다.")
    void cancelOrder() throws Exception {
        var order = ordersRepository.save(anOrder());

        mockMvc.perform(delete("/order/{id}", order.getId()))
                .andExpect(status().isNoContent());
    }

}
