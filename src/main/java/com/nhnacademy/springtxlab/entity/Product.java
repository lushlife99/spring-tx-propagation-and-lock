package com.nhnacademy.springtxlab.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private int price;
    private int stock;

    public void decreaseStock(int capacity) {
        this.stock -= capacity;
    }

    @Builder
    public Product(String productName, int price, int stock) {
        this.productName = productName;
        this.price = price;
        this.stock = stock;
    }
}
