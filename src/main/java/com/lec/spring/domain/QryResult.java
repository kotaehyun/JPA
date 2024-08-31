package com.lec.spring.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QryResult {

    int count;
    String status;

    @JsonIgnore // test3 --> JSON 변환할때 빼주세요 할 기능
    public String getToday(){
        return "Today Friday";
    }
}
