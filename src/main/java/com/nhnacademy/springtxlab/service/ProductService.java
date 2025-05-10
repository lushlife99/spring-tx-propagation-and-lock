package com.nhnacademy.springtxlab.service;

import com.nhnacademy.springtxlab.entity.OrderItem;
import com.nhnacademy.springtxlab.exception.NotEnoughProductStockException;
import com.nhnacademy.springtxlab.repository.OrderItemRepository;
import com.nhnacademy.springtxlab.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void decreaseOrderItemsStock(List<OrderItem> orderItems) {

        log.info("handle request : decreaseOrderItemStock");
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getProduct().getStock() < orderItem.getQuantity()) {
                throw new NotEnoughProductStockException(orderItem);
            }
            orderItem.getProduct().decreaseStock(orderItem.getQuantity());
        }
    }
}
