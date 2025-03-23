package com.jaeseung.coffeedelivery.application.port.in;

import com.jaeseung.coffeedelivery.application.domain.order.Order;
import com.jaeseung.coffeedelivery.application.domain.payment.CreditCard;
import com.jaeseung.coffeedelivery.application.domain.payment.Payment;
import com.jaeseung.coffeedelivery.application.domain.payment.Receipt;

import java.util.UUID;

public interface OrderingCoffeeUseCase {
    Order placeOrder(Order order);
    Order updateOrder(UUID orderId, Order order);
    void cancelOrder(UUID orderId);
    Payment payOrder(UUID orderId, CreditCard creditCard);
    Receipt readReceipt(UUID orderId);
    Order takeOrder(UUID orderId);
}
