# 목차

- [1. 레이어드 아키텍처의 문제점](#1레이어드-아키텍처의-문제점)
- [2. 클린아키텍처와 헥사고날 아키텍처](#2클린아키텍처와-헥사고날-아키텍처)
- [3. 의존성 역전 (Inverting Dependencies)](#3의존성-역전-inverting-dependencies)
- [4. 단위 테스트, 통합 테스트, 시스템 테스트](#4단위-테스트-통합-테스트-시스템-테스트)


# 1.레이어드 아키텍처의 문제점

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

# 2.클린아키텍처와 헥사고날 아키텍처

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
# 3.의존성 역전 (Inverting Dependencies)

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

---
# 4.단위 테스트, 통합 테스트, 시스템 테스트

<p align="center"><img width="450" alt="스크린샷 2025-03-24 15 32 30" src="https://github.com/user-attachments/assets/2c1b62a4-dd76-4615-9b22-5feef55f662e" /></p>

이 프로젝트의 테스트 구조는 다음과 같이 구성하였습니다.

<img width="880" alt="스크린샷 2025-04-07 12 49 55" src="https://github.com/user-attachments/assets/ec3ff83e-d9a8-40bb-874e-2528af68115a" />

## 단위 테스트(Unit Test)란?

단위 테스트는 소프트웨어 개발에서 개별적인 단위(보통 클래스나 메서드)를 검증하는 테스트 방식입니다. 주요 특징은 다음과 같습니다:

### (1) 개별성: 하나의 클래스나 메서드에 초점을 맞춰 테스트

**예시: OrderCostTest**

사용자의 코드에서 OrderCostTest 클래스는 Order 클래스의 getCost() 메서드를 테스트합니다. 이 테스트는 주문의 수량과 사이즈에 따라 비용이 올바르게 계산되는지를 검증합니다.

```jsx
@ParameterizedTest
@MethodSource("drinkCosts")
@DisplayName("주문 수량 및 사이즈에 대한 예상 금액이 맞는지 확인합니다.")
void orderCostBasedOnQuantityAndSize(int quantity, Size size, BigDecimal expectedCost) {
    var order = new Order(Location.TAKE_AWAY, List.of(
            new LineItem(Drink.LATTE, Milk.WHOLE, size, quantity)
    ));
    assertThat(order.getCost()).isEqualTo(expectedCost);
}
```

### (2) 인스턴스화: 테스트 대상 클래스를 인스턴스화하여 인터페이스(공개 메서드)를 호출

**예시 1. OrderCostTest에서의 Order 인스턴스화**

위의 orderCostBasedOnQuantityAndSize 테스트에서 Order 객체를 직접 생성합니다:

```jsx
var order = new Order(Location.TAKE_AWAY, List.of(
        new LineItem(Drink.LATTE, Milk.WHOLE, size, quantity)
));
```

이후 order.getCost()를 호출하여 결과를 확인합니다.

**예시 2. AcceptanceTests에서의 서비스 객체 호출**

```jsx
@Test
@DisplayName("소비자가 결제를 진행합니다.")
void customerCanPayTheOrder() {
    var existingOrder = ordersRespository.save(anOrder());
    var creditCard = aCreditCard();
    var payment = customerUsecase.payOrder(existingOrder.getId(), creditCard);
    assertThat(payment.orderId()).isEqualTo(existingOrder.getId());
}
```

여기서 customerUsecase는 CoffeeShopService의 인스턴스로, payOrder() 메서드를 호출하여 결제 동작을 테스트합니다. 이는 서비스 객체를 인스턴스화하고 공개 메서드를 호출하는 단위 테스트의 방식입니다.

- **의존성 관리**:단위 테스트에서는 테스트 대상 클래스가 의존하는 외부 객체(예: 데이터베이스)를 실제로 사용하지 않고, 이를 대체할 수 있는 모의 객체(Mock)나 스텁(Stub)을 활용합니다. 이를 통해 외부 시스템 의존성을 제거하고 테스트를 빠르고 안정적으로 만듭니다.
    
    
- **목적**: 코드의 올바른 동작을 보장하고, 리팩토링 시 회귀(regression)를 방지하며, 개발 속도를 높입니다.

### (3) 의존성 관리: 모의 객체(Mock)나 스텁(Stub)을 사용해 의존성 제어

단위 테스트에서는 테스트 대상 클래스가 의존하는 외부 객체(예: 데이터베이스)를 실제로 사용하지 않고, 이를 대체할 수 있는 모의 객체(Mock)나 스텁(Stub)을 활용합니다. 이를 통해 외부 시스템 의존성을 제거하고 테스트를 빠르고 안정적으로 만듭니다.

**예시: `InMemoryOrdersRepository`와 `InMemoryPaymentsRepository`**
사용자의 코드에서 `InMemoryOrdersRepository`는 `OrdersRepository` 인터페이스를 구현하며, 실제 데이터베이스 대신 메모리 내 `HashMap`을 사용합니다:

```java
public class InMemoryOrdersRepository implements OrdersRepository {
    private final Map<UUID, Order> entities = new HashMap<>();
    @Override
    public Order save(Order order) {
        entities.put(order.getId(), order);
        return order;
    }
}
```

이는 스텁(Stub) 역할을 하며, `Order` 객체를 저장하고 조회하는 기능을 단순화합니다. `AcceptanceTests`에서 이를 사용해 데이터베이스 없이 테스트를 진행합니다:

```java
@BeforeEach
void setup() {
    ordersRespository = new InMemoryOrdersRepository();
    paymentsRepository = new InMemoryPaymentsRepository();
}
```

이 방식은 외부 의존성을 제거하고, 테스트가 독립적이고 빠르게 실행되도록 합니다.

- **효과**`customerCanPayTheOrder` 테스트에서 `ordersRespository.save()`를 호출할 때 실제 데이터베이스 연결 없이 메모리에서 동작이 처리되므로, **"의존성 관리"** 원칙이 적용됩니다.

### (4) 목적: 코드의 올바른 동작 보장, 리팩토링 시 회귀 방지, 개발 속도 향상

단위 테스트는 코드가 의도대로 작동하는지 확인하고, 코드 변경 시 기존 기능이 깨지지 않도록 보호하며, 개발 과정을 효율적으로 만듭니다.

- **예시: `OrderCostTest`로 비용 계산 로직 보장**`orderCostBasedOnQuantityAndSize`와 `orderCostIsSumOfLineItemCosts`는 `getCost()` 메서드가 다양한 입력에 대해 올바른 값을 반환함을 보장합니다. 만약 `getCost()` 로직을 수정한다면, 이 테스트들이 실패하여 회귀(regression)를 감지할 수 있습니다.
- **예시: `AcceptanceTests`로 전체 워크플로우 검증**
    
    ```java
    @Test
    @DisplayName("바리스타는 소비자가 결제를 완료한 주문을 준비할 수 있습니다.")
    void baristaCanStartPreparingTheOrderWhenItIsPaid() {
        var existingOrder = ordersRespository.save(aPaidOrder());
        var orderInPreparation = baristaUsecase.startPreparingOrder(existingOrder.getId());
        assertThat(orderInPreparation.getStatus()).isEqualTo(Status.PREPARING);
    }
    ```
    
    이 테스트는 결제 후 주문 상태가 `PREPARING`으로 변경되는지를 확인하며, 시스템의 주요 기능이 올바르게 동작함을 보장합니다. 이는 개발자가 코드를 수정할 때 신뢰할 수 있는 안전망을 제공합니다.
    
- **테스트 팩토리의 역할**`OrderTestFactory`, `CreditCardTestFactory`, `PaymentTestFactory`는 테스트에 필요한 객체를 쉽게 생성합니다:
    
    ```java
    public static Order aPaidOrder() {
        return anOrder().markPaid();
    }
    ```
    
    이는 테스트 작성 속도를 높이고, 코드 가독성을 개선하여 개발 효율성을 증대시킵니다.

## 통합 테스트(Integration Test)란?

연결된 여러 유닛을 인스턴스화하고 시작점이 되는 클래스의 인터페이스로 데이터를 보낸 후 유닛들의 네트워크가 기대한대로 잘 작동되는지 검증한다.

### (1) 웹 어댑터의 역할과 테스트 방법

웹 어댑터는 클라이언트로부터 HTTP 요청을 받아 JSON 문자열을 처리하고, 이를 도메인 객체로 변환하여 유스케이스에 전달합니다. 이후 유스케이스에서 반환된 결과를 다시 JSON으로 변환해 응답합니다. 이 과정은 Spring 프레임워크의 `MockMvc`를 사용하여 테스트할 수 있으며, 이는 실제 HTTP 프로토콜 대신 프레임워크의 변환 및 매핑 로직을 신뢰합니다.

`MockMvc`를 활용한 테스트는 컨트롤러 단위로 보일 수 있지만, 요청 매핑, JSON 변환, 유효성 검증 등 Spring 프레임워크의 객체 네트워크를 포함하므로 통합 테스트로 분류됩니다. 이는 단위 테스트와 구분되며, 프레임워크와의 상호작용을 검증하는 데 유용합니다.

### 예시 1. OrderControllerTest

```java
@RestResourceTest
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

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
}

```

- **설명**: 이 테스트는 `/order` 엔드포인트로 POST 요청을 보내 주문 생성 기능을 검증합니다. `MockMvc`를 통해 JSON 형식의 요청 본문을 전달하며, 웹 어댑터는 이를 도메인 객체로 매핑하여 유스케이스를 호출합니다. 응답 상태가 `201 Created`인지 확인함으로써 결과 변환 과정도 검증됩니다.
- **통합 테스트 특징**: Spring MVC의 요청 처리 파이프라인(매핑, 변환, 유효성 검증)이 포함되어 실제 동작에 가까운 환경을 테스트합니다.

### 예시 2. PaymentControllerTest

```java
@RestResourceTest
public class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final String paymentJson = """
        {
            "cardHolderName": "Michael Faraday",
            "cardNumber": "11223344",
            "expiryMonth": 12,
            "expiryYear": 2023
        }
        """;

    @Test
    @DisplayName("/payment/{id} API 테스트합니다.")
    void payOrder() throws Exception {
        var order = ordersRepository.save(anOrder());
        mockMvc.perform(put("/payment/{id}", order.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(paymentJson))
                .andExpect(status().isOk());
    }
}

```

- **설명**: 이 테스트는 `/payment/{id}` 엔드포인트로 결제 요청을 보내는 과정을 검증합니다. `paymentJson`을 통해 결제 정보를 전달하며, 웹 어댑터는 이를 처리해 유스케이스를 호출합니다. `MockMvc`를 사용해 요청과 응답의 통합된 동작을 확인합니다.
- **통합 테스트 이점**: 실제 HTTP 요청과 유사한 환경에서 컨트롤러와 프레임워크의 상호작용을 검증합니다.

### (2) 영속성 어댑터의 역할과 테스트 방법

영속성 어댑터는 도메인 객체를 데이터베이스에 저장하거나 조회하는 역할을 담당합니다. 이를 테스트하려면 단위 테스트가 아닌 통합 테스트가 필요하며, 데이터베이스 매핑과 쿼리 실행까지 검증해야 합니다. Spring에서는 `@DataJpaTest`를 사용하여 JPA 리포지토리와 데이터베이스 환경을 초기화하며, 기본적으로 H2 인메모리 데이터베이스를 활용합니다. 그러나 프로덕션 환경과 동일한 데이터베이스(예: Testcontainers)를 사용하는 것이 더 안정적인 결과를 제공합니다.

### 예시: 사용자의 영속성 어댑터 테스트 코드

### OrdersJpaAdapterTest

```java
@PersistenceTest
public class OrdersJpaAdapterTest {
    @Autowired
    private OrdersRepository ordersRepository;

    @Test
    @DisplayName("ordersRepository에 save를 테스트합니다.")
    void creatingOrderReturnsPersistedOrder() {
        var order = new Order(Location.TAKE_AWAY, List.of(
                new LineItem(Drink.LATTE, Milk.WHOLE, Size.SMALL, 1)
        ));
        var persistedOrder = ordersRepository.save(order);
        assertThat(persistedOrder.getLocation()).isEqualTo(Location.TAKE_AWAY);
    }
}

```

- **설명**: 이 테스트는 `Order` 객체를 데이터베이스에 저장하는 기능을 검증합니다. `ordersRepository.save()`를 호출하여 저장한 후, 반환된 객체의 속성이 올바른지 확인합니다. 이는 영속성 어댑터의 데이터베이스 매핑 역할을 테스트합니다.
- **통합 테스트 특징**: JPA와 H2 데이터베이스를 포함하여 실제 데이터베이스 상호작용을 검증합니다.

### PaymentsJpaAdapterTest

```java
@SpringBootTest
public class PaymentsJpaAdapterTest {
    @Autowired
    private PaymentsRepository paymentsRepository;

    @Test
    @DisplayName("paymentsRepository save를 테스트합니다.")
    void creatingPaymentReturnsPersistedPayment() {
        var now = LocalDate.now();
        var creditCard = aCreditCard();
        var payment = new Payment(UUID.randomUUID(), creditCard, now);
        var persistedPayment = paymentsRepository.save(payment);
        assertThat(persistedPayment.creditCard()).isEqualTo(creditCard);
    }
}

```

- **설명**: 이 테스트는 `Payment` 객체를 데이터베이스에 저장하는 과정을 테스트합니다. 저장된 결제 정보가 정확히 반환되는지 확인하며, 영속성 어댑터의 역할을 검증합니다.
- **통합 테스트 이점**: 데이터베이스와의 매핑 및 쿼리 실행을 포함하여 프로덕션 환경과 유사한 동작을 보장합니다.

## 시스템 테스트(System Test)란?
전체 시스템 통합 테스트는 애플리케이션의 end-to-end 워크플로우를 검증하며, 실제 환경에서의 동작을 보장합니다. @SpringBootTest를 사용하여 전체 애플리케이션 컨텍스트를 로드하고, H2 또는 Testcontainers를 통해 데이터베이스를 설정할 수 있습니다.

### 예시 CoffeeDeliveryApplicationTests

```java
@SpringBootTest
@AutoConfigureMockMvc
class CoffeeDeliveryApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("주문을 접수하고, 주문을 결제하고, 주문을 준비하고, 주문 영수증을 읽고, 주문 완료하고, 배달을 하는 통합 테스트를 진행합니다.")
    void processNewOrder() throws Exception {
        var orderId = placeOrder();
        payOrder(orderId);
        prepareOrder(orderId);
        readReceipt(orderId);
        deliverOrder(orderId);
    }
}

```

- **설명**: 이 테스트는 주문 접수부터 배달까지의 전체 프로세스를 검증합니다. `MockMvc`를 통해 HTTP 요청을 시뮬레이션하고, 서비스 메서드를 호출하여 시스템의 통합된 동작을 확인합니다.
- **통합 테스트 특징**: 애플리케이션 컨텍스트와 데이터베이스 연결을 포함하여 실제 워크플로우를 테스트합니다.



