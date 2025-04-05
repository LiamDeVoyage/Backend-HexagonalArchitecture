## 1. 레이어드 아키텍처의 문제점

레이어드 아키텍처는 소프트웨어 개발에서 널리 사용되는 패턴으로, 웹 계층, 도메인 계층, 영속성 계층으로 나뉘어 구조화됩니다. 하지만 시간이 지나면서 유지보수성과 확장성을 저해하는 여러 문제가 드러납니다. 

1. **데이터베이스 중심 설계를 촉진한다.**
2. **단축 경로(Shortcuts)를 쉽게 허용한다.**
3. **테스트가 어려워진다.**
4. **유스케이스를 숨긴다.**
5. **병렬 작업을 어렵게 만든다.**

### 실무를 통해 느낀점

기획이 완전하지 않고, 사용자의 피드백을 통해 빠른 개발이 필요할 때는 레이어드 아키텍처가 쉽고,빠르게 개발의 용의성을 가져다줍니다. 

그러나 프로젝트가 진행됨에 따라 기능을 추가할때는 단축경로를 통해 웹 컨트롤러 계층에서 영속성 계층을 불러와 사용하는 등 단축 경로를 쉽게 사용하게 되고 그에 따라 코드의 유지보수성이 떨어지게 됩니다. 

테스트 코드 작성이 쉽지 않고 직접 API 호출이나 DB를 조작하여 테스트 하는 경우가 많아지는데, 복잡한 기능이 추가되면 테스트가 길어지고 어려워지게 됩니다.

함께 작업하는 경우에는 다른 사람이 개발한 기능의 로직을 코드 한줄한줄 읽으며 파악해야하고 코드가 길어지면 파악하기 더욱 어려워지게됩니다. 뿐만 아니라 하나의 기능을 함께 작업하는 것도 불가능해집니다.

---

## 2. 클린아키텍처와 헥사고날 아키텍처

이 프로젝트의 구조는 다음과 같이 구성하였습니다.
![스크린샷 2025-04-05 12 21 18](https://github.com/user-attachments/assets/8c16f0ac-7485-4539-b7c1-6b2ea305ad8f)

### 클린 아키텍처

클린 아키텍처는 도메인 로직을 중심에 두고, 모든 의존성이 도메인을 향하도록 설계합니다. 

이를 통해 도메인 로직이 외부 프레임워크, 데이터베이스, UI 등에 의존하지 않게 되어 유연성과 독립성이 높아집니다.

### 클린 아키텍처의 계층

- **엔티티(Entity)**: 도메인 객체
- **유스케이스(Use Case)**: 비즈니스 로직
- **인터페이스 어댑터(Interface Adapters)**: 웹, UI, 데이터베이스와의 연결.
- **프레임워크와 드라이버(Frameworks & Drivers)**: 외부 도구와 프레임워크.

<img width="1200" alt="스크린샷 2025-03-24 15 31 27" src="https://github.com/user-attachments/assets/7b873f46-4092-45ed-954c-bf7e1cd6cf87" />
<img width="1200" alt="스크린샷 2025-03-24 15 31 57" src="https://github.com/user-attachments/assets/6929f8e3-9d4b-4159-9a14-482f73e45d3d" />

### 헥사고날 아키텍처

헥사고날 아키텍처는 애플리케이션 코어(도메인 + 유스케이스)를 중심에 두고, 외부 시스템(웹, 데이터베이스 등)과의 상호작용을 **포트(Port)** 와 **어댑터(Adapter)** 로 관리합니다. 

이는 클린 아키텍처를 구체적으로 구현한 방식 중 하나입니다.

### 구성 요소

- **애플리케이션 코어**: 도메인 엔티티와 유스케이스
- **포트**: 코어와 외부를 연결하는 인터페이스
- **어댑터**: 포트를 구현하거나 사용하는 외부 시스템과의 연결
<img width="1200" alt="스크린샷 2025-03-24 15 32 30" src="https://github.com/user-attachments/assets/ba3ebd42-3c3e-4b61-808e-517aeb0ba423" />



---
## 3. 의존성 역전 (Inverting Dependencies)

### **CoffeeShopService 클래스로 보는** 의존성 역전 원칙과 단일 책임 원칙

```jsx
@UseCase
public class CoffeeShopService implements OrderingCoffeeUseCase {

    private final OrdersRepository orders;
    private final PaymentsRepository payments;

    public CoffeeShopService(OrdersRepository orders, PaymentsRepository payments) {
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

        return orders.save(order.markBeingDelivered());
    }
}
```

### 의존성 역전 원칙 (The Dependency Inversion Principle)

### 개념 설명

의존성 역전 원칙(DIP)은 고수준 모듈(도메인 로직)이 저수준 모듈(데이터베이스, UI 등)에 의존하지 않도록 의존성의 방향을 뒤집는 것입니다. 이를 통해 도메인 로직이 외부 변화(예: 데이터베이스 교체)에 영향을 받지 않게 됩니다.

### 예시 설명

 CoffeeShopService가 구체적인 구현 클래스(예: OrdersRepositoryImpl)가 아닌 추상화된 인터페이스에 의존한다면, 이는 의존성 역전 원칙을 따르는 설계입니다. 헥사고날 아키텍처에서는 '포트' 역할을 하는 인터페이스를 정의하고, 외부 어댑터가 이를 구현하는 방식이 표준이므로, 이 코드가 DIP를 준수한다고 볼 수 있습니다

- 고수준 모듈(CoffeeShopService)이 저수준 모듈의 구체적인 구현에 의존하지 않고, 추상화(OrdersRepository, PaymentsRepository)에 의존함.
- 외부 구현 세부 사항(데이터베이스, 파일 시스템 등)이 변경되더라도 CoffeeShopService는 영향을 받지 않음.

### 단일 책임 원칙 (The Single Responsibility Principle)

### 개념 설명

단일 책임 원칙(SRP)은 흔히 "한 컴포넌트는 한 가지 일만 해야 한다"로 해석되지만, 이는 정확하지 않습니다. SRP의 진정한 의미는 **"한 컴포넌트는 변경할 이유가 하나여야 한다"** 입니다. 즉, 컴포넌트가 여러 이유로 수정되지 않도록 설계해야 합니다. 이렇게 하면 코드 변경 시 다른 부분에 영향을 주지 않아 유지보수가 쉬워집니다.

### 예시 설명

CoffeeShopService의 메서드들을 살펴보면:

- **주문 생성(placeOrder)**, **수정(updateOrder)**, **취소(cancelOrder)** : 주문 상태 관리.
- **결제(payOrder)**: 주문을 결제 상태로 변경하고 결제 기록 생성.
- **영수증 조회(readReceipt)**: 주문과 결제 정보를 조회.
- **배달 상태 변경(takeOrder)**: 주문 상태 업데이트.

이 모든 기능은 **주문 관리**라는 큰 맥락에서 동작합니다. 각 메서드는 주문의 생명주기(생성 → 수정 → 결제 → 배달 → 조회)를 관리하는 역할을 합니다.

CoffeeShopService는 **주문 관리**라는 단일 책임을 수행한다고 볼 수 있습니다. 결제와 영수증 조회도 주문의 생명주기와 상태를 관리하는 과정의 일부로 간주할 수 있으므로, SRP를 위반하지 않습니다.

모든 메서드가 주문의 생성부터 완료까지의 생명주기를 다루며, 이는 하나의 책임으로 통합됨. 클래스가 변경되는 이유는 '주문 관리 로직의 변경' 하나뿐임.

- 모든 메서드가 주문의 생성부터 완료까지의 생명주기를 다루며, 이는 하나의 책임으로 통합됨.
- 클래스가 변경되는 이유는 '주문 관리 로직의 변경' 하나뿐임.



