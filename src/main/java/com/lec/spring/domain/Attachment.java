package com.lec.spring.domain;


import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "t7_attachment")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // pk

    @Column(nullable = false)
    private String sourcename; // 원본 파일명
    @Column(nullable = false)
    private String filename;    // 저장된 파일명(rename 된 파일명)

//    @ToString.Exclude
//    @ManyToOne
    @Column(name = "post_id")
    private Long post;  // 어느글의 첨부파일? (FK)

    @Transient
    private boolean isImage; // 이미지 여부

}
