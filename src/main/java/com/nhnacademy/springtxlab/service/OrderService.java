package com.nhnacademy.springtxlab.service;

import com.nhnacademy.springtxlab.entity.Order;
import com.nhnacademy.springtxlab.entity.enums.PaymentStatus;
import com.nhnacademy.springtxlab.exception.ExcessivePointIncreaseException;
import com.nhnacademy.springtxlab.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final PaymentService paymentService;
    private final ProductService productService;
    private final PointService pointService;

    /**
     * processOrder
     *
     * 1. 결제 진행
     * 2. 재고 차감
     * 3. 포인트 추가
     * @param order
     */
    @Transactional
    public void processOrder(Order order) {

        // 재고 차감
        productService.decreaseOrderItemsStock(order.getOrderItems());
        log.info("decrease stock success");

        // 결제 진행
        paymentService.pay(order);
        log.info("pay success");

        // 포인트 추가
        try {
            pointService.increasePoint(order.getMember(), 100);
            log.info("increase point success");
        } catch (RuntimeException e) {
            log.error("error : {}", e.getMessage());
        }
    }
}
