package port.out;

import com.jaeseung.coffeedelivery.application.domain.payment.Payment;
import com.jaeseung.coffeedelivery.application.port.out.PaymentsRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryPaymentsRepository implements PaymentsRepository {

    private final Map<UUID, Payment> entities = new HashMap<>();

    @Override
    public Payment findPaymentByOrderId(UUID orderId) {
        return entities.get(orderId);
    }

    @Override
    public Payment save(Payment payment) {
        entities.put(payment.orderId(), payment);
        return payment;
    }
}
