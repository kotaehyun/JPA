package com.lec.spring.repository;

import com.lec.spring.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("build")
class JpaTest {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private CommentRepository commentRepository;


    @Test
    public void init(){
        System.out.println("[init]");

        // Create Authority
        Authority auth_member = Authority.builder()
                .name("ROLE_MEMBER")
                .build();
        Authority auth_admin = Authority.builder()
                .name("ROLE_ADMIN")
                .build();

        authorityRepository.saveAndFlush(auth_member);  // INSERT
        authorityRepository.saveAndFlush(auth_admin);   // INSERT

        authorityRepository.findAll().forEach(System.out::println); // SELECT

        // Create User
        User user1 = User.builder()
                .username("USER1")
                .password(passwordEncoder.encode("1234"))
                .name("회원1")
                .build();

        User user2 = User.builder()
                .username("USER2")
                .password(passwordEncoder.encode("1234"))
                .name("회원2")
                .build();

        User admin1 = User.builder()
                .username("ADMIN1")
                .password(passwordEncoder.encode("1234"))
                .name("관리자1")
                .build();

        user1.addAuthority(auth_member);
        admin1.addAuthority(auth_member, auth_admin);


        userRepository.saveAll(List.of(user1, user2, admin1));

        userRepository.findAll().forEach(System.out::println);
//        System.out.println(user1);

        //  Writing Post
        Post p1 = Post.builder()
                .subject("It's title 1")
                .content("It's about it 1")
                .user(user1)    // FK
                .build();

        Post p2 = Post.builder()
                .subject("It's title 2")
                .content("It's about it 2")
                .user(user2)    // FK
                .build();

        Post p3 = Post.builder()
                .subject("It's title 3")
                .content("It's about it 3")
                .user(admin1)
                .build();

        Post p4 = Post.builder()
                .subject("It's title 4")
                .content("It's about it 4")
                .user(admin1)
                .build();

        postRepository.saveAll(List.of(p1, p2, p3, p4));
        System.out.println("\n[Post]");
        postRepository.findAll().forEach(System.out::println);

        // The attached file
        Attachment attachment1 = Attachment.builder()
                .filename("face01.png")
                .sourcename("face01.png")
                .post(p1.getId())
                .build();
        Attachment attachment2 = Attachment.builder()
                .filename("face02.png")
                .sourcename("face02.png")
                .post(p1.getId())
                .build();
        Attachment attachment3 = Attachment.builder()
                .filename("face03.png")
                .sourcename("face03.png")
                .post(p2.getId())
                .build();
        Attachment attachment4 = Attachment.builder()
                .filename("face04.png")
                .sourcename("face04.png")
                .post(p2.getId())
                .build();
        Attachment attachment5 = Attachment.builder()
                .filename("face05.png")
                .sourcename("face05.png")
                .post(p3.getId())
                .build();
        Attachment attachment6 = Attachment.builder()
                .filename("face06.png")
                .sourcename("face06.png")
                .post(p3.getId())
                .build();
        Attachment attachment7 = Attachment.builder()
                .filename("face07.png")
                .sourcename("face07.png")
                .post(p4.getId())
                .build();
        Attachment attachment8 = Attachment.builder()
                .filename("face08.png")
                .sourcename("face08.png")
                .post(p4.getId())
                .build();

        attachmentRepository.saveAll(List.of(attachment1, attachment2
                , attachment3, attachment4
                , attachment5, attachment6
                , attachment7, attachment8));

        attachmentRepository.findAll().forEach(System.out::println);


        // review comment
        Comment c1 = Comment.builder()
                .content("1. user1이 1번글에 댓글 작성")
                .user(user1)
                .post(p1.getId())
                .build();
        Comment c2 = Comment.builder()
                .content("2. user1이 1번글에 댓글 작성.")
                .user(user1)
                .post(p1.getId())
                .build();
        Comment c3 = Comment.builder()
                .content("3. user1이 2번글에 댓글 작성.")
                .user(user1)
                .post(p2.getId())
                .build();
        Comment c4 = Comment.builder()
                .content("4. user1이 2번글에 댓글 작성.")
                .user(user1)
                .post(p2.getId())
                .build();
        Comment c5 = Comment.builder()
                .content("5. user1이 3번글에 댓글 작성.")
                .user(user1)
                .post(p3.getId())
                .build();
        Comment c6 = Comment.builder()
                .content("6. user1이 3번글에 댓글 작성.")
                .user(user1)
                .post(p3.getId())
                .build();
        Comment c7 = Comment.builder()
                .content("7. user1이 4번글에 댓글 작성.")
                .user(user1)
                .post(p4.getId())
                .build();
        Comment c8 = Comment.builder()
                .content("8. user1이 4번글에 댓글 작성.")
                .user(user1)
                .post(p4.getId())
                .build();
        Comment c9 = Comment.builder()
                .content("9. admin1이 1번글에 댓글 작성.")
                .user(admin1)
                .post(p1.getId())
                .build();
        Comment c10 = Comment.builder()
                .content("10. admin1이 1번글에 댓글 작성.")
                .user(admin1)
                .post(p1.getId())
                .build();
        Comment c11 = Comment.builder()
                .content("11. admin1이 2번글에 댓글 작성.")
                .user(admin1)
                .post(p2.getId())
                .build();
        Comment c12 = Comment.builder()
                .content("12. admin1이 2번글에 댓글 작성.")
                .user(admin1)
                .post(p2.getId())
                .build();
        Comment c13 = Comment.builder()
                .content("13. admin1이 3번글에 댓글 작성.")
                .user(admin1)
                .post(p3.getId())
                .build();
        Comment c14 = Comment.builder()
                .content("14. admin1이 3번글에 댓글 작성.")
                .user(admin1)
                .post(p3.getId())
                .build();
        Comment c15 = Comment.builder()
                .content("15. admin1이 4번글에 댓글 작성.")
                .user(admin1)
                .post(p4.getId())
                .build();
        Comment c16 = Comment.builder()
                .content("16. admin1이 4번글에 댓글 작성.")
                .user(admin1)
                .post(p4.getId())
                .build();

        commentRepository.saveAll(List.of(
                c1, c2, c3, c4,
                c5, c6, c7, c8,
                c9, c10, c11, c12,
                c13, c14, c15, c16
        ));

        commentRepository.findAll().forEach(System.out::println);

    }// end init()

}// end Test