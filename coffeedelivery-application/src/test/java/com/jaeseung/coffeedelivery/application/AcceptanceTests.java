package com.jaeseung.coffeedelivery.application;

import com.jaeseung.coffeedelivery.application.domain.order.LineItem;
import com.jaeseung.coffeedelivery.application.domain.order.Order;
import com.jaeseung.coffeedelivery.application.domain.shared.*;
import com.jaeseung.coffeedelivery.application.port.in.DeliveringCoffeeUseCase;
import com.jaeseung.coffeedelivery.application.port.in.OrderingCoffeeUseCase;
import com.jaeseung.coffeedelivery.application.port.in.PreparingCoffeeUseCase;
import com.jaeseung.coffeedelivery.application.port.out.OrdersRepository;
import com.jaeseung.coffeedelivery.application.port.out.PaymentsRepository;
import com.jaeseung.coffeedelivery.application.service.CoffeeDeliveryService;
import com.jaeseung.coffeedelivery.application.service.CoffeeMachineService;
import com.jaeseung.coffeedelivery.application.service.CoffeeShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import port.out.InMemoryOrdersRepository;
import port.out.InMemoryPaymentsRepository;

import java.util.List;

import static domain.order.OrderTestFactory.*;
import static domain.payment.CreditCardTestFactory.aCreditCard;
import static domain.payment.PaymentTestFactory.aPaymentForOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AcceptanceTests {

    private OrdersRepository ordersRespository;
    private PaymentsRepository paymentsRepository;
    private OrderingCoffeeUseCase customerUsecase;
    private PreparingCoffeeUseCase baristaUsecase;
    private DeliveringCoffeeUseCase deliveryUsecase;

    @BeforeEach
    void setup() {
        ordersRespository = new InMemoryOrdersRepository();
        paymentsRepository = new InMemoryPaymentsRepository();
        customerUsecase = new CoffeeShopService(ordersRespository, paymentsRepository);
        baristaUsecase = new CoffeeMachineService(ordersRespository);
        deliveryUsecase = new CoffeeDeliveryService(ordersRespository);
    }

    @Test
    @DisplayName("소비자가 음료 주문 리스트 , 테이크 아웃 유무 , 수량을 입력하여 주문한다.")
    void customerCanOrderCoffee() {
        // Given
        var orderToMake = new Order(Location.IN_STORE, List.of(new LineItem(Drink.CAPPUCCINO, Milk.SKIMMED, Size.SMALL, 1)));

        // When
        var order = customerUsecase.placeOrder(orderToMake);

        // Then
        assertThat(order.getLocation()).isEqualTo(Location.IN_STORE);
        assertThat(order.getItems()).containsExactly(new LineItem(Drink.CAPPUCCINO, Milk.SKIMMED, Size.SMALL, 1));
        assertThat(order.getStatus()).isEqualTo(Status.PAYMENT_EXPECTED);
    }

    @Test
    @DisplayName("소비자가 결제하기전 주문 변경한다.")
    void customerCanUpdateTheOrderBeforePaying() {

        // Given
        var orderWithOneItem = new Order(Location.TAKE_AWAY, List.of(new LineItem(Drink.LATTE, Milk.WHOLE, Size.LARGE, 1)));
        var orderWithTwoItems = new Order(Location.TAKE_AWAY, List.of(new LineItem(Drink.LATTE, Milk.WHOLE, Size.LARGE, 2)));

        // When
        var order = customerUsecase.placeOrder(orderWithOneItem);
        var updatedOrder = customerUsecase.updateOrder(order.getId(), orderWithTwoItems);

        // Then
        assertThat(updatedOrder.getItems()).containsExactly(new LineItem(Drink.LATTE, Milk.WHOLE, Size.LARGE, 2));
    }

    @Test
    @DisplayName("소비자가 결제를 진행합니다.")
    void customerCanPayTheOrder() {

        // Given
        var existingOrder = ordersRespository.save(anOrder());
        var creditCard = aCreditCard();

        // When
        var payment = customerUsecase.payOrder(existingOrder.getId(), creditCard);

        // Then
        assertThat(payment.orderId()).isEqualTo(existingOrder.getId());
        assertThat(payment.creditCard()).isEqualTo(creditCard);
        assertThat(ordersRespository.findOrderById(existingOrder.getId()).getStatus()).isEqualTo(Status.PAID);
    }

    @Test
    @DisplayName("소비자가 결제를 완료하였다면 주문을 변경할 수 없습니다.(예외처리)")
    void noChangesAllowedWhenOrderIsPaid() {

        // Given
        var existingOrder = ordersRespository.save(aPaidOrder());

        assertThatThrownBy(
                // When
                () -> customerUsecase.updateOrder(existingOrder.getId(), anOrder())

          // Then
        ).isInstanceOf(
                IllegalStateException.class
        );
    }

    @Test
    @DisplayName("소비자가 결제를 완료하였다면 영수증을 받을 수 있습니다.")
    void customerCanGetReceiptWhenOrderIsPaid() {

        // Given
        var existingOrder = ordersRespository.save(aPaidOrder());
        var existingPayment = paymentsRepository.save(aPaymentForOrder(existingOrder));

        // When
        var receipt = customerUsecase.readReceipt(existingOrder.getId());

        // Then
        assertThat(receipt.amount()).isEqualTo(existingOrder.getCost());
        assertThat(receipt.paid()).isEqualTo(existingPayment.paid());
    }

    @Test
    @DisplayName("바리스타는 소비자가 결제를 완료한 주문을 준비할 수 있습니다.")
    void baristaCanStartPreparingTheOrderWhenItIsPaid() {

        // Given
        var existingOrder = ordersRespository.save(aPaidOrder());

        // When
        var orderInPreparation = baristaUsecase.startPreparingOrder(existingOrder.getId());

        // Then
        assertThat(orderInPreparation.getStatus()).isEqualTo(Status.PREPARING);
    }

    @Test
    @DisplayName("바리스타는 주문 준비가 끝나면 알릴 수 있습니다.")
    void baristaCanMarkTheOrderReadyWhenSheIsFinishedPreparing() {
        // Given
        var existingOrder = ordersRespository.save(anOrderInPreparation());

        // When
        var preparedOrder = baristaUsecase.finishPreparingOrder(existingOrder.getId());

        // Then
        assertThat(preparedOrder.getStatus()).isEqualTo(Status.READY);
    }

    @Test
    @DisplayName("배달원은 준비완료된 주문을 배달하기 시작합니다.")
    void deliveryCanStartPreparingTheOrderWhenItIsReady() {
        // Given
        var existingOrder = ordersRespository.save(aReadyOrder());

        // When
        var orderInDeliveration = deliveryUsecase.startDeliveringOrder(existingOrder.getId());

        // Then
        assertThat(orderInDeliveration.getStatus()).isEqualTo(Status.DELIVERING);

    }

    @Test
    @DisplayName("배달원은 배달을 완료합니다.")
    void customerCanTakeTheOrderWhenItIsReady() {
        // Given
        var existingOrder = ordersRespository.save(anOrderInDelivery());

        // When
        var deliveredOrder = deliveryUsecase.finishDeliveringOrder(existingOrder.getId());

        // Then
        assertThat(deliveredOrder.getStatus()).isEqualTo(Status.DELIVERED);

    }

}
