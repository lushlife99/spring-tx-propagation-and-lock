package com.nhnacademy.springtxlab.exception;

import com.nhnacademy.springtxlab.entity.OrderItem;

public class NotEnoughProductStockException extends RuntimeException {

    public NotEnoughProductStockException(OrderItem orderItem) {
        super("상품 재고 부족: " +
                orderItem.getProduct().getId() + " - 요청 수량: " +
                orderItem.getQuantity() + ", 남은 재고: " +
                orderItem.getProduct().getStock());
    }
}
