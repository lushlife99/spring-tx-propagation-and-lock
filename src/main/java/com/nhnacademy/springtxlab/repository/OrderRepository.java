package com.nhnacademy.springtxlab.repository;

import com.nhnacademy.springtxlab.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
