package com.lec.spring.domain;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

// Validater 는 Controller 에 등록되어 있어야 동작한다.

public class PostValidator implements Validator {
    // 이 Validator가 제공된 Class의 인스턴스(clazz)를 유효성감시할 수 있는가?
    @Override
    public boolean supports(Class<?> clazz) {
        System.out.println("supports(" + clazz.getName() + ")");

        // ↓ 검증할 객체의 클래스 타입인지 확인: Post = clazz; 가능 여부
        boolean result = Post.class.isAssignableFrom(clazz);
        System.out.println(result);
        return result;
    }

    // 주어진 객체(target)에 유효성검사를 하고
    // 유효성검사에 오류가 있는 경우 주어진 객체에 이 오류들을 errors 에 등록 한다.
    // 아래 validate() 는 Spring 이 기본적인 binding이 수행한 이후에 호출된다.
    // 따라서, errors 에는 Spring이 수행한 기본적인 binding 에러 들이 이미 담겨 있고
    // target 에는 binding 이 완료한 커맨드 객체가 전달된다.

    @Override
    public void validate(Object target, Errors errors) {
        Post post = (Post)target;
        System.out.println("validate() 호출: " + post);

        // 바인당한 객체에 대한 검증 수행

        String subject = post.getSubject();
        if (subject == null || subject.trim().isEmpty()){
            errors.rejectValue("subject","글 제목은 필수입니다.");

        }

    }
}
