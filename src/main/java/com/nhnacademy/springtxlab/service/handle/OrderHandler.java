package com.nhnacademy.springtxlab.service.handle;

import com.nhnacademy.springtxlab.entity.Order;
import com.nhnacademy.springtxlab.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderHandler {

    private final int MAX_RETRIES = 3;

    private final OrderService orderService;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void handleOrder(Long orderId) {
        int attempts = 0;
        boolean success = false;

        while (attempts < MAX_RETRIES && !success) {
            try {
                orderService.processOrderV5(orderId);
                log.info("processOrderV5 end");
                success = true;
            } catch (OptimisticLockingFailureException e) {
                attempts++;
                log.warn("Optimistic lock conflict on attempt {}/{}", attempts, MAX_RETRIES);
                if (attempts == MAX_RETRIES) {
                    log.error("재시도 횟수 초과로 주문 처리 실패");
                    throw e; // 재시도 초과 시 propagate
                }
            } catch (RuntimeException e) {
                log.error("Other error: {}", e.getMessage());
            }
        }
    }
}
