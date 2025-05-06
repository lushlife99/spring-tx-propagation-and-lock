# 🛠 Spring Transaction Propagation & Locking Examples

이 프로젝트는 Spring Framework의 **트랜잭션 전파 속성(Transaction Propagation Behavior)** 과 **락 전략(Locking Strategies)** 을 실제 코드로 실습하며 학습하기 위한 레포지토리입니다.

---

## 📌 Transaction 전파 속성에 따른 예외 처리 전략

| 시나리오 번호 | 설명 | 전파 속성 | 결과 |
|---------|------|-----------|------|
| 1       | 포인트 적립 실패는 무시하고 주문은 성공 | REQUIRED → REQUIRES_NEW | 주문 성공, 포인트 적립 실패 |
| 2       | 결제 실패 시 주문도 롤백 | REQUIRED → REQUIRED | 주문 실패 (롤백) |
| 3       | Checked Exception 롤백되지 않음 | `Exception` 발생 | 명시적으로 `rollbackFor = Exception.class` 지정 필요 |
| 4       | 자식 트랜잭션의 rollbackOnly 설정 → 전체 롤백 | REQUIRED → REQUIRED | 전체 트랜잭션 롤백됨 |

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
