package port.out;

import com.jaeseung.coffeedelivery.application.domain.order.Order;
import com.jaeseung.coffeedelivery.application.port.out.OrderNotFound;
import com.jaeseung.coffeedelivery.application.port.out.OrdersRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryOrdersRepository implements OrdersRepository {
    private final Map<UUID, Order> entities = new HashMap<>();

    @Override
    public Order findOrderById(UUID orderId) throws OrderNotFound {
        var order = entities.get(orderId);
        if (order == null) {
            throw new OrderNotFound();
        }
        return order;
    }

    @Override
    public Order save(Order order) {
        entities.put(order.getId(), order);
        return order;
    }

    @Override
    public void deleteById(UUID orderId) {
        entities.remove(orderId);
    }
}
