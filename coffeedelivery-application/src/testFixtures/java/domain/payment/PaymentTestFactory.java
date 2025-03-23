package domain.payment;

import com.jaeseung.coffeedelivery.application.domain.order.Order;
import com.jaeseung.coffeedelivery.application.domain.payment.Payment;

import java.time.LocalDate;

import static domain.payment.CreditCardTestFactory.aCreditCard;

public class PaymentTestFactory {
    // 주문에 대한 지불 객체
    public static Payment aPaymentForOrder(Order order) {
        return new Payment(order.getId(), aCreditCard(), LocalDate.now());
    }
}
