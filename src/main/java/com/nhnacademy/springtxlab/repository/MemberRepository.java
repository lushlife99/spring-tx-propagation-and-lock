package com.nhnacademy.springtxlab.repository;

import com.nhnacademy.springtxlab.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
