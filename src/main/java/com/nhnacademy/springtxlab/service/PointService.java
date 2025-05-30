package com.nhnacademy.springtxlab.service;

import com.nhnacademy.springtxlab.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increasePoint(Member member, int point) {
        member.increasePoint(point);
    }

    /**
     * increasePointV2에서 RuntimeException 발생
     * @param member
     * @param point
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void increasePointV2(Member member, int point) {
        member.increasePoint(point);
        throw new RuntimeException();
    }
}
