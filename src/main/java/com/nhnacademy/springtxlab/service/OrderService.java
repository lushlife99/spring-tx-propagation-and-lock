package com.nhnacademy.springtxlab.service;

import com.nhnacademy.springtxlab.entity.Order;
import com.nhnacademy.springtxlab.entity.OrderItem;
import com.nhnacademy.springtxlab.repository.OrderRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.MailException;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final PaymentService paymentService;
    private final ProductService productService;
    private final PointService pointService;
    private final EmailService emailService;
    private final OrderRepository orderRepository;

    /**
     * processOrder
     *
     * 1. 결제 진행
     * 2. 재고 차감
     * 3. 포인트 추가
     * @param orderId
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOrder(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow();

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

    /**
     * V2에서 EmailSend 로직을 추가.
     * 기본 전략은 롤백이 안됨.
     * 이메일 전송에 오류가 발생했을 때 롤백이 되어야 함. (rollbackFor = MessagingException.class)
     *
     * @throws MessagingException
     */

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOrderV2(Long orderId) throws MessagingException {

        Order order = orderRepository.findById(orderId).orElseThrow();
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

        // email 보내는 도중 오류 발생 (checked exception)
        throw new MessagingException(); // 예외 던지기
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = MessagingException.class)
    public void processOrderV3(Long orderId) throws MessagingException {

        Order order = orderRepository.findById(orderId).orElseThrow();

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

        emailService.sendEmail(order.getMember().getEmail());
        log.info("send email success");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOrderV4(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow();

        // 재고 차감
        productService.decreaseOrderItemsStock(order.getOrderItems());
        log.info("decrease stock success");

        // 결제 진행
        paymentService.pay(order);
        log.info("pay success");

        // 포인트 추가
        try {
            pointService.increasePointV2(order.getMember(), 100);
            log.info("increase point success");
        } catch (RuntimeException e) {
            log.error("error : {}", e.getMessage());
        }

    }

}
