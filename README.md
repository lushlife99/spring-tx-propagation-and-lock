# 🛠 Spring Transaction Propagation & Locking Examples

이 프로젝트는 Spring Framework의 **트랜잭션 전파 속성(Transaction Propagation Behavior)** 과 **락 전략(Locking Strategies)** 을 실제 코드로 실습하며 학습하기 위한 레포지토리입니다.

---

## 📌 Transaction 전파 속성에 따른 예외 처리 전략

| 시나리오 번호 | 설명 | 전파 속성 & 옵션                         | 결과                                |
| --- | --- |------------------------------------|-----------------------------------|
| 1 | 포인트 적립에서 예외가 발생했을 때 포인트 적립은 롤백되고 주문로직은 커밋 | REQUIRED → REQUIRES_NEW            | 기본 로직 롤백 X                        |
| 2 | 결제 실패 시 주문도 롤백 | REQUIRED → REQUIRED                | 기본 로직 롤백 O                        |
| 3 | Checked Exception 롤백되지 않음 | REQUIRED → REQUIRED rollbackFor 옵션 | 롤백 X (기본설정). rollbackFor 옵션으로 달라짐 |
| 4 | 포인트 적립에서 예외가 발생하고 부모 트랜잭션에서 try-catch | REQUIRED → REQUIRED                | 롤백 O                              |
---

## 🔐 Locking Strategies

### 1. 🔄 낙관적 락 (Optimistic Lock)

- 동시성 충돌 가능성이 낮은 경우 사용
- `@Version` 필드를 활용한 버전 관리
- 예외: `ObjectOptimisticLockingFailureException`

### 2. 🔒 비관적 락 (Pessimistic Lock)

- 충돌 가능성이 높은 경우 사용
- DB 수준에서 row-level 락 사용
- JPQL: `@Lock(LockModeType.PESSIMISTIC_WRITE)`

---

## 🧪 테스트 코드

각 전파 속성 및 락 전략에 대한 JUnit 테스트 포함
- 트랜잭션의 **부분 커밋/롤백 여부 확인**
