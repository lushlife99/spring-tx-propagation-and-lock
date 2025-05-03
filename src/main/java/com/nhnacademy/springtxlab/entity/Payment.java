package com.nhnacademy.springtxlab.entity;

import com.nhnacademy.springtxlab.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(value = EnumType.STRING) @Setter
    private PaymentStatus paymentStatus;

    public Payment() {
        this.paymentStatus = PaymentStatus.PENDING;
    }
}
