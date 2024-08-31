package com.lec.spring.domain;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        System.out.println("supports(" + clazz.getName() + ")");
        // ↓ 검증할 객체의 클래스 타입인지 확인
        boolean result = User.class.isAssignableFrom(clazz);
        return result;
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User)target;

        String username = user.getUsername();
        if (username == null || username.trim().isEmpty()){
            errors.rejectValue("username", "username 은 필수입니다.");    // rejectValue(field, errorcode)
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name 은 필수입니다");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password 는 필수입니다");

        // 입력 password, re_password 가 동일한지 비교
        if (!user.getPassword().equals(user.getRe_password())){
            errors.rejectValue("re_password","비밀번호와 비밀번호확인 입력값은 같아야 합니다.");
        }

    }
}
