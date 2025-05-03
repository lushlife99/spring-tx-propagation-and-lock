package com.nhnacademy.springtxlab.repository;

import com.nhnacademy.springtxlab.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
