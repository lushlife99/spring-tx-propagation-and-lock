package com.nhnacademy.springtxlab.repository;

import com.nhnacademy.springtxlab.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
