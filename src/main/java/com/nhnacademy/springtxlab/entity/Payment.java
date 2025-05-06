package com.nhnacademy.springtxlab.entity;

import com.nhnacademy.springtxlab.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;

    public Payment() {
        this.paymentStatus = PaymentStatus.PENDING;
    }
}
