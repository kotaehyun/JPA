package com.lec.spring.service;

import com.lec.spring.domain.Authority;
import com.lec.spring.domain.User;
import com.lec.spring.repository.AuthorityRepository;
import com.lec.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setAuthorityRepository(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Autowired
    public UserServiceImpl(){
        System.out.println(getClass().getName() + "()생성");
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean isExist(String username) {
        User user = findByUsername(username);
        return (user != null) ? true : false ;
    }

    @Override
    public int register(User user) {
        user.setUsername(user.getUsername().toUpperCase()); // DB 에 회원아이디(username) 을 대문자로 저장
        user.setPassword(passwordEncoder.encode(user.getPassword()));   // password 는 암호화해서 저장
        userRepository.save(user);  // 새로이 회원(User) 저장. id값 받아옴

        // 신규회원은 ROLE_MEMBER 권한을 기본적으로 부여
        Authority auth =  authorityRepository.findByName("ROLE_MEMBER");
        user.addAuthority(auth);
        userRepository.save(user);
       // Long user_id = user.getId();
       // Long auth_id = auth.getId();
        // authorityRepository.addAuthority(user_id, auth_id);

        return 1;
    }

    @Override
    public List<Authority> selectAuthoritiesById(Long id) {
        User user = userRepository.findById(id).orElse(null);
//        return authorityRepository.findByUser(user);

        if (user != null)
            return user.getAuthorities();

        return new ArrayList<>();
    }
}
