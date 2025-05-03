package com.nhnacademy.springtxlab.exception;

public class NotEnoughMemberMoneyException extends RuntimeException{

    public NotEnoughMemberMoneyException(long money, long amount) {
        super(String.format("NotEnoughMoney : member's money : %s, payAmount : %s", money, amount));
    }
}
