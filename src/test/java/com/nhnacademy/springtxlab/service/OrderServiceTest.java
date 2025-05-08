package com.nhnacademy.springtxlab.service;

import com.nhnacademy.springtxlab.entity.*;
import com.nhnacademy.springtxlab.entity.Order;
import com.nhnacademy.springtxlab.entity.enums.PaymentStatus;
import com.nhnacademy.springtxlab.exception.AlreadyProcessOrderException;
import com.nhnacademy.springtxlab.repository.*;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockitoSpyBean
    private PointService pointService;

    @MockitoSpyBean
    private EmailService emailService;

    @MockitoSpyBean
    private PaymentService paymentService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @PersistenceContext
    private EntityManager em;

    private Order order;

    @BeforeEach
    void setUp() {
        order = createTestOrder();
    }

    @Test
    @DisplayName("포인트 결제가 실패했을 때 전체 로직은 롤백되지 않아야 한다.")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void point_fail_does_not_rollback_payment_and_stock() {

        log.info("handle test");
        // given
        doThrow(new RuntimeException("포인트 적립 실패"))
                .when(pointService).increasePoint(any(), anyInt());

        int[] originalStocks = order.getOrderItems().stream()
                .mapToInt(item -> item.getProduct().getStock())
                .toArray();

        // when
        Assertions.assertDoesNotThrow(() -> orderService.processOrder(order));


        // then
        // 1. 재고 차감 확인
        List<Product> products = order.getOrderItems().stream()
                .map(item -> productRepository.findById(item.getProduct().getId()).orElseThrow())
                .toList();

        Assertions.assertAll("재고 차감 확인",
                () -> {
                    for (int i = 0; i < products.size(); i++) {
                        int expectedStock = originalStocks[i] - order.getOrderItems().get(i).getQuantity();
                        int actualStock = productRepository.findById(products.get(i).getId()).orElseThrow().getStock();

                        Assertions.assertEquals(expectedStock, actualStock,
                                "Product " + products.get(i).getId() + " stock mismatch");
                    }
                }
        );

        // 2. 포인트 차감 확인
        List<OrderItem> orderItems = order.getOrderItems();
        long expectMemberMoney = order.getMember().getMoney();
        for (OrderItem orderItem : orderItems) {
            expectMemberMoney -= (long) orderItem.getProduct().getPrice() * orderItem.getQuantity();
        }

        Member member = memberRepository.findById(order.getMember().getId()).orElseThrow();
        Assertions.assertEquals(expectMemberMoney, member.getMoney());
    }


    @Test
    @DisplayName("결제 실패 시 주문도 롤백되어야 한다 (분리된 트랜잭션 검증)")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void should_rollback_order_when_payment_fails() {
        // given
        int initialStock = order.getOrderItems().get(0).getProduct().getStock();
        order.getPayment().setPaymentStatus(PaymentStatus.COMPLETED);
        doThrow(new AlreadyProcessOrderException())
                .when(paymentService).pay(any());

        // when
        Assertions.assertThrows(AlreadyProcessOrderException.class, () -> {
            orderService.processOrder(order);
        });

        // then: 서비스 트랜잭션이 끝나고 롤백되었는지 검증
        // product 재고 롤백 검증
        Product product = productRepository.findById(order.getOrderItems().get(0).getProduct().getId())
                .orElseThrow();
        Assertions.assertEquals(initialStock, product.getStock(), "재고가 롤백되지 않았습니다");

    }


    @Test
    @DisplayName("이메일 전송 오류 시 롤백이 되어야 한다 (rollbackFor 테스트)")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void should_rollback_checked_exception() throws MessagingException {
        // given
        doThrow(new MessagingException("이메일 전송 오류")).when(emailService).sendEmail(anyString());
        int initialStock = order.getOrderItems().getFirst().getProduct().getStock();

        // when
        Assertions.assertThrows(MessagingException.class, () -> {
            orderService.processOrderV2(order);
        });

        // then
        // product 재고 롤백 검증
        Product product = productRepository.findById(order.getOrderItems().getFirst().getProduct().getId()).orElseThrow();
        Assertions.assertEquals(initialStock, product.getStock(), "재고가 롤백되지 않았습니다");
    }

    @Test
    @DisplayName("(REQUIRED -> REQUIRED 전파 수준) 부모 트랜잭션에서 try catch로 자식 트랜잭션에서 발생한 예외를 잡았어도 롤백이 되어야 한다.")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldRollbackParent_whenChildSetsRollbackOnly_evenIfExceptionCaught() {
        // given
        int initialStock = order.getOrderItems().getFirst().getProduct().getStock();

        // when
        Assertions.assertThrows(RuntimeException.class, () -> {
            orderService.processOrderV3(order);
        });

        // then
        // product 재고 롤백 검증
        Product product = productRepository.findById(order.getOrderItems().getFirst().getProduct().getId()).orElseThrow();
        Assertions.assertEquals(initialStock, product.getStock(), "재고가 롤백되지 않았습니다");
    }

    public Order createTestOrder() {
        Member testMember = Member.builder().userName("testMember").money(20000).point(0).email("test@example.com").build();
        memberRepository.save(testMember);

        Payment payment = new Payment();
        paymentRepository.save(payment);

        Product product1 = Product.builder().productName("product1").price(1000).stock(10).build();
        Product product2 = Product.builder().productName("product2").price(2000).stock(10).build();
        productRepository.save(product1);
        productRepository.save(product2);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(OrderItem.builder().product(product1).quantity(5).build());
        orderItems.add(OrderItem.builder().product(product2).quantity(5).build());

        Order testOrder = Order.builder().member(testMember).payment(payment).build();
        for (OrderItem orderItem : orderItems) {
            testOrder.addOrderItem(orderItem);
        }
        orderRepository.save(testOrder);

        return testOrder;
    }
}
