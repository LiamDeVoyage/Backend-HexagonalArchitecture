## 1. 레이어드 아키텍처의 문제점

레이어드 아키텍처는 소프트웨어 개발에서 널리 사용되는 패턴으로, 웹 계층, 도메인 계층, 영속성 계층으로 나뉘어 구조화됩니다. 하지만 시간이 지나면서 유지보수성과 확장성을 저해하는 여러 문제가 드러납니다. 

1. **데이터베이스 중심 설계를 촉진한다.**
2. **단축 경로(Shortcuts)를 쉽게 허용한다.**
3. **테스트가 어려워진다.**
4. **유스케이스를 숨긴다.**
5. **병렬 작업을 어렵게 만든다.**

---

### 실무를 통해 느낀점

기획이 완전하지 않고, 사용자의 피드백을 통해 빠른 개발이 필요할 때는 레이어드 아키텍처가 쉽고,빠르게 개발의 용의성을 가져다줍니다. 

그러나 프로젝트가 진행됨에 따라 기능을 추가할때는 단축경로를 통해 웹 컨트롤러 계층에서 영속성 계층을 불러와 사용하는 등 단축 경로를 쉽게 사용하게 되고 그에 따라 코드의 유지보수성이 떨어지게 됩니다. 

테스트 코드 작성이 쉽지 않고 직접 API 호출이나 DB를 조작하여 테스트 하는 경우가 많아지는데, 복잡한 기능이 추가되면 테스트가 길어지고 어려워지게 됩니다.

함께 작업하는 경우에는 다른 사람이 개발한 기능의 로직을 코드 한줄한줄 읽으며 파악해야하고 코드가 길어지면 파악하기 더욱 어려워지게됩니다. 뿐만 아니라 하나의 기능을 함께 작업하는 것도 불가능해집니다.

## 2. 클린아키텍처와 헥사고날 아키텍처

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

헥사고날 아키텍처는 애플리케이션 코어(도메인 + 유스케이스)를 중심에 두고, 외부 시스템(웹, 데이터베이스 등)과의 상호작용을 **포트(Port)**와 **어댑터(Adapter)**로 관리합니다. 

이는 클린 아키텍처를 구체적으로 구현한 방식 중 하나입니다.

### 구성 요소

- **애플리케이션 코어**: 도메인 엔티티와 유스케이스
- **포트**: 코어와 외부를 연결하는 인터페이스(
- **어댑터**: 포트를 구현하거나 사용하는 외부 시스템과의 연결
<img width="1200" alt="스크린샷 2025-03-24 15 32 30" src="https://github.com/user-attachments/assets/ba3ebd42-3c3e-4b61-808e-517aeb0ba423" />

