package com.nhnacademy.springtxlab.entity.enums;

public enum PaymentStatus {
    PENDING,      // 결제 대기 중
    PROCESSING,   //결제 중
    COMPLETED,    // 결제 완료
    FAILED,       // 결제 실패
    CANCELLED,    // 결제 취소
    REFUNDED;     // 결제 환불됨

}
