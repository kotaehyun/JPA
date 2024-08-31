package com.lec.spring.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity(name = "t7_user")
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;    // 회원 아이디

    @Column(nullable = false)
    @JsonIgnore
    private String password;    // 회원 비밀번호

    @Transient  // Not reflected in DB jakarta.persistence.*
    @ToString.Exclude  // Lombok 에서 ToString에서 제외할 필드
    @JsonIgnore
    private String re_password;  // 비밀번호 확인 입력

    @Column(nullable = false)
    private String name;    // 회원 이름

    // User:Authority = N:M
    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore
    private List<Authority> authorities = new ArrayList<>();  //현재 로그인한 User information 확인할때 필요로 인해 작성

    // 추가된 정보를 ↑리스트 정보에 추가
    public  void addAuthority(Authority... authorities){
        if (authorities != null){
            Collections.addAll(this.authorities, authorities);
        }

    }
}
