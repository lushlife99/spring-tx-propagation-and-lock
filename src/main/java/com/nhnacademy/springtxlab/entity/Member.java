package com.nhnacademy.springtxlab.entity;

import com.nhnacademy.springtxlab.exception.NotEnoughMemberMoneyException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private Long point;
    private Long money;
    private String email;

    @Builder
    public Member(String userName, long point, long money, String email) {
        this.userName = userName;
        this.point = point;
        this.money = money;
        this.email = email;
    }

    public void increasePoint(long amount) {
        this.point += amount;
    }

    public void decreaseMoney(long amount) {
        if (this.money < amount) {
            throw new NotEnoughMemberMoneyException(this.money, amount);
        }
        this.money -= amount;
    }

}
