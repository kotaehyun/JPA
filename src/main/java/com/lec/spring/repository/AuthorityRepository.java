package com.lec.spring.repository;

import com.lec.spring.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    // 특정 이름(name) 의 권한 정보 읽어보기
    Authority findByName(String name);

}
