package com.nhnacademy.springtxlab.entity;

import jakarta.persistence.*;
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

    @Version
    private Long version;

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
