package com.nhnacademy.springtxlab.service;

import com.nhnacademy.springtxlab.entity.*;
import com.nhnacademy.springtxlab.repository.MemberRepository;
import com.nhnacademy.springtxlab.repository.OrderRepository;
import com.nhnacademy.springtxlab.repository.PaymentRepository;
import com.nhnacademy.springtxlab.repository.ProductRepository;
import com.nhnacademy.springtxlab.service.handle.OrderHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
public class SpringTxLockTest {

    @Autowired
    private OrderHandler orderHandler;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductRepository productRepository;

    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {

        // Arrange Members
        Member testMember = Member.builder().userName("m1").money(20000).point(0).email("test@example.com").build();
        Member testMember2 = Member.builder().userName("m2").money(20000).point(0).email("test2@example.com").build();
        memberRepository.save(testMember);
        memberRepository.save(testMember2);

        // Arrange Payments
        Payment payment = new Payment();
        Payment payment2 = new Payment();
        paymentRepository.save(payment);
        paymentRepository.save(payment2);

        // Arrange Products
        Product product1 = Product.builder().productName("product1").price(1000).stock(10).build();
        Product product2 = Product.builder().productName("product2").price(2000).stock(10).build();
        productRepository.save(product1);
        productRepository.save(product2);

        // Arrange OrderItems, Orders
        List<OrderItem> orderItems1 = new ArrayList<>();
        orderItems1.add(OrderItem.builder().product(product1).quantity(1).build());
        orderItems1.add(OrderItem.builder().product(product2).quantity(1).build());

        order1 = Order.builder().member(testMember).payment(payment).build();
        for (OrderItem orderItem : orderItems1) {
            order1.addOrderItem(orderItem);
        }
        order1 = orderRepository.save(order1);

        List<OrderItem> orderItems2 = new ArrayList<>();
        orderItems2.add(OrderItem.builder().product(product1).quantity(1).build());
        orderItems2.add(OrderItem.builder().product(product2).quantity(1).build());

        order2 = Order.builder().member(testMember2).payment(payment2).build();
        for (OrderItem orderItem : orderItems2) {
            order2.addOrderItem(orderItem);
        }
        order2 = orderRepository.save(order2);
    }

    @Test
    @DisplayName("낙관적 락: 버전 충돌 발생 시 재시도 후 재고 정합성 보장")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void optimisticLockTest() throws InterruptedException {
        // given
        Runnable task1 = () -> {
            try {
                orderHandler.handleOrder(order1.getId());
            } catch (Exception e) {
                log.error("Task1 error", e);
            }
        };

        Runnable task2 = () -> {
            try {
                orderHandler.handleOrder(order2.getId());
            } catch (Exception e) {
                log.error("Task2 error", e);
            }
        };

        Thread thread1 = new Thread(task1, "order1");
        Thread thread2 = new Thread(task2, "order2");

        // when
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        // then
        Product p1 = productRepository.findById(order1.getOrderItems().get(0).getProduct().getId()).orElseThrow();
        Product p2 = productRepository.findById(order1.getOrderItems().get(1).getProduct().getId()).orElseThrow();

        log.info("Product1 stock: {}", p1.getStock());
        log.info("Product2 stock: {}", p2.getStock());

        // 총 2개씩 차감되어야 함 (product1, product2 각각 10 - 2 = 8)
        assertEquals(8, p1.getStock());
        assertEquals(8, p2.getStock());
    }


}
