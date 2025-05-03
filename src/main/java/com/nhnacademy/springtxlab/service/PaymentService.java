package com.nhnacademy.springtxlab.service;

import com.nhnacademy.springtxlab.entity.Member;
import com.nhnacademy.springtxlab.entity.Order;
import com.nhnacademy.springtxlab.entity.OrderItem;
import com.nhnacademy.springtxlab.entity.Payment;
import com.nhnacademy.springtxlab.entity.enums.PaymentStatus;
import com.nhnacademy.springtxlab.exception.AlreadyProcessOrderException;
import com.nhnacademy.springtxlab.repository.OrderRepository;
import com.nhnacademy.springtxlab.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void pay(Order order) {
        if (!order.getPayment().getPaymentStatus().equals(PaymentStatus.PENDING)) {
            throw new AlreadyProcessOrderException();
        }

        Member member = order.getMember();
        long totalRequiredMoney = 0L;
        for (OrderItem orderItem : order.getOrderItems()) {
            totalRequiredMoney += (long) orderItem.getProduct().getPrice() * orderItem.getQuantity();
        }

        member.decreaseMoney(totalRequiredMoney);
        Payment payment = order.getPayment();
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
    }

}
