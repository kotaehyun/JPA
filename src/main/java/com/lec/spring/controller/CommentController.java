package com.lec.spring.controller;

import com.lec.spring.domain.Comment;
import com.lec.spring.domain.QryCommentList;
import com.lec.spring.domain.QryResult;
import com.lec.spring.domain.User;
import com.lec.spring.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController // data 를 response 한다
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    public CommentController(){
        System.out.println(getClass().getName() + "() 생성");
    }

    @GetMapping("/test1")
    public String test(){
        return "Hello World";
    }

    @GetMapping("/test2")
    public QryResult test2(){           // Property를
        return QryResult.builder()      // JSON 변환 웹화면
                .count(34)              //
                .status("OK")           //  getCount 메소드 이름으로 나옴
                .build();               //

    }

    @GetMapping("/test3")
    public QryCommentList test3(){
        QryCommentList list = new QryCommentList();
        list.setCount(1);
        list.setStatus("OK");

        Comment cmt = Comment.builder()
                .user(User.builder().username("고윤정").id(34L).name("한소희").build())
                .content("라이벌")
                //.regDate(LocalDateTime.now())
                .build();

        List<Comment> cmtList = new ArrayList<>();
        cmtList.add(cmt);
        list.setList(cmtList);

        return list;
    }

    // Java 의 배열 => Json의 배열
    @GetMapping("/test4")
    public List<Integer> test4(){
        return Arrays.asList(100, 200, 300);
    }

    // Java 의 class 나 Map 은 => JSON 의 object 로 변환
    @GetMapping("/test5")
    public Map<Integer, String> test5(){
        return Map.of(100, "숫자 백", 200, "숫자 이백");
    }

    // ---------------------------------------------------------------------------------------------------------------------

    @GetMapping("/list")
    public QryCommentList list(Long id){
        return commentService.list(id);
    }

    @PostMapping("/write")
    public QryResult write(
            @RequestParam("post_id") Long postId,
            @RequestParam("user_id") Long userId,
            String content){
        return  commentService.write(postId, userId, content);
    }

    @PostMapping("/delete")
    public QryResult delete(Long id){
        return commentService.delete(id);
    }



}
