package com.jaeseung.coffeedelivery.application.service;

import com.jaeseung.architecture.UseCase;
import com.jaeseung.coffeedelivery.application.domain.order.Order;
import com.jaeseung.coffeedelivery.application.domain.payment.CreditCard;
import com.jaeseung.coffeedelivery.application.domain.payment.Payment;
import com.jaeseung.coffeedelivery.application.domain.payment.Receipt;
import com.jaeseung.coffeedelivery.application.port.in.OrderingCoffeeUseCase;
import com.jaeseung.coffeedelivery.application.port.out.OrdersRepository;
import com.jaeseung.coffeedelivery.application.port.out.PaymentsRepository;

import java.time.LocalDate;
import java.util.UUID;

@UseCase
public class CoffeShopService implements OrderingCoffeeUseCase {

    private final OrdersRepository orders;
    private final PaymentsRepository payments;

    public CoffeShopService(OrdersRepository orders, PaymentsRepository payments) {
        this.orders = orders;
        this.payments = payments;
    }


    @Override
    public Order placeOrder(Order order) {
        return orders.save(order);
    }

    @Override
    public Order updateOrder(UUID orderId, Order order) {
        var existingOrder = orders.findOrderById(orderId);

        return orders.save(existingOrder.update(order));
    }

    @Override
    public void cancelOrder(UUID orderId) {
        var order = orders.findOrderById(orderId);

        if (!order.canBeCancelled()) {
            throw new IllegalStateException("Order is already paid");
        }

        orders.deleteById(orderId);
    }

    @Override
    public Payment payOrder(UUID orderId, CreditCard creditCard) {
        var order = orders.findOrderById(orderId);

        orders.save(order.markPaid());

        return payments.save(new Payment(orderId, creditCard, LocalDate.now()));
    }

    @Override
    public Receipt readReceipt(UUID orderId) {
        var order = orders.findOrderById(orderId);
        var payment = payments.findPaymentByOrderId(orderId);

        return new Receipt(order.getCost(), payment.paid());
    }

    @Override
    public Order takeOrder(UUID orderId) {
        var order = orders.findOrderById(orderId);

        return orders.save(order.markTaken());
    }
}
