package com.lec.spring.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
@Entity(name = "t7_post")
@DynamicInsert  // insert 시 null 인 필드 제외
@DynamicUpdate  // update 시 null 인 필드 제외
public class Post extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // 글 id(pk)

    @Column(nullable = false)
    private String subject;

    @Column(
            // Specify a large column to use Web Editor
            // ↓ ddl-auto: It does not work in update, so it should be applied as create-drop.
            columnDefinition = "LONGTEXT"   // ← MySQL, Postgre 의 경우
            // , length=10000  // ← Oracle 의 경우(varchar2(10000) 으로 지정됨)
    )
    private String content;

    @ColumnDefault(value = "0")
    private long viewCnt;

    // Post:User = N:1  // The action of inquiring the author of the post when inquiring about the post.
    @ManyToOne
    @ToString.Exclude
    private User user;  // 글 작성자 (FK)


    // The attached file
    // Post:File = 1:N
    // cascade = CascadeType.ALL : Delete children as well when the actions of the deletion occur
//    @Transient
    @OneToMany(/*mappedBy = "post" , */cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    @ToString.Exclude
    @Builder.Default
    private List<Attachment> fileList = new ArrayList<>();

    public void addFiles(Attachment... files){
        Collections.addAll(fileList, files);
    }


    // review
    // Post : Comment = 1:N
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    @ToString.Exclude
    @Builder.Default
    private List<Comment> commentList = new ArrayList<>();

    public void addComments(Comment... comments){
        Collections.addAll(commentList, comments);
    }

}
