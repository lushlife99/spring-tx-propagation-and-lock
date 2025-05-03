package com.nhnacademy.springtxlab.repository;

import com.nhnacademy.springtxlab.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
