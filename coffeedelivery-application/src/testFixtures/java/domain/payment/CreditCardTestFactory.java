package domain.payment;

import com.jaeseung.coffeedelivery.application.domain.payment.CreditCard;

import java.time.Month;
import java.time.Year;

public class CreditCardTestFactory {
    // 신용카드 객체
    public static CreditCard aCreditCard() {
        return new CreditCard("Michael Faraday", "11223344", Month.JANUARY, Year.of(2023));
    }
}
