package domain.order;

import com.jaeseung.coffeedelivery.application.domain.order.LineItem;
import com.jaeseung.coffeedelivery.application.domain.order.Order;
import com.jaeseung.coffeedelivery.application.domain.shared.Drink;
import com.jaeseung.coffeedelivery.application.domain.shared.Location;
import com.jaeseung.coffeedelivery.application.domain.shared.Milk;
import com.jaeseung.coffeedelivery.application.domain.shared.Size;

import java.util.List;

public class OrderTestFactory {

    // Order 객체
    public static Order anOrder() {
        return new Order(Location.TAKE_AWAY, List.of(new LineItem(Drink.LATTE, Milk.WHOLE, Size.LARGE, 1)));
    }

    // 결제 완료된 Order 객체
    public static Order aPaidOrder() {
        return anOrder()
                .markPaid();
    }

    // 준비중인 Order 객체
    public static Order anOrderInPreparation() {
        return aPaidOrder()
                .markBeingPrepared();
    }

    // 준비 완료된 Order 객체
    public static Order aReadyOrder() {
        return anOrderInPreparation()
                .markPrepared();
    }
}