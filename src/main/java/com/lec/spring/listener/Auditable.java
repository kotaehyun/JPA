package com.lec.spring.listener;


import java.time.LocalDateTime;

public interface Auditable {
    LocalDateTime getRegDate();

    void setRegDate(LocalDateTime regDate);
}
