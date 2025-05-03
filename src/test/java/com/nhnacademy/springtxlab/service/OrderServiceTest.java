package com.nhnacademy.springtxlab.service;

import com.nhnacademy.springtxlab.entity.*;
import com.nhnacademy.springtxlab.repository.MemberRepository;
import com.nhnacademy.springtxlab.repository.OrderRepository;
import com.nhnacademy.springtxlab.repository.PaymentRepository;
import com.nhnacademy.springtxlab.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @MockitoSpyBean
    private OrderService orderService;

    @MockitoSpyBean
    private PointService pointService;

    @MockitoSpyBean
    private PaymentService paymentService;

    @MockitoSpyBean
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductRepository productRepository;


    @Test
    void point_fail_does_not_rollback_payment_and_stock() {
        // given
        Order order = createTestOrder();

        List<Product> products33 = order.getOrderItems().stream()
                .map(item -> {
                    return productRepository.findById(item.getProduct().getId())
                            .orElseThrow();
                }).toList();

        int[] originalStocks = order.getOrderItems().stream()
                .mapToInt(item -> item.getProduct().getStock())
                .toArray();

        doThrow(new RuntimeException("포인트 적립 실패"))
                .when(pointService).increasePoint(any(), anyInt());

        // when
        Assertions.assertDoesNotThrow(() -> orderService.processOrder(order));

        // then
        verify(paymentService).pay(order);
        verify(productService).decreaseOrderItemsStock(order.getOrderItems());

        // DB에서 최신 상태로 불러온 product 목록
        List<Product> products = order.getOrderItems().stream()
                .map(item -> {
                    return productRepository.findById(item.getProduct().getId())
                            .orElseThrow();
                }).toList();

        // 재고 차감 검증
        Assertions.assertAll("재고 차감 확인",
                () -> {
                    for (int i = 0; i < products.size(); i++) {
                        int expectedStock = originalStocks[i] - products.get(i).getStock();
                        int actualStock = products.get(i).getStock();
                        Assertions.assertEquals(expectedStock, actualStock,
                                "Product " + products.get(i).getId() + " stock mismatch");
                    }
                }
        );

    }



    private Order createTestOrder() {

        Member testMember = Member.builder().userName("testMember").money(20000).point(0).build();
        memberRepository.save(testMember);

        Payment payment = new Payment();
        paymentRepository.save(payment);

        Product product1 = Product.builder().productName("product1").price(1000).stock(10).build();
        Product product2 = Product.builder().productName("product2").price(2000).stock(10).build();

        productRepository.save(product1);
        productRepository.save(product2);

        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem1 = OrderItem.builder().product(product1).quantity(5).build();
        OrderItem orderItem2 = OrderItem.builder().product(product2).quantity(5).build();
        orderItems.add(orderItem1);
        orderItems.add(orderItem2);

        Order testOrder = Order.builder().member(testMember).payment(payment).orderItems(orderItems).build();
        orderRepository.save(testOrder);

        return testOrder;
    }
}
