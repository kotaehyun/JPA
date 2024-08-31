package com.lec.spring.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class HomeController {

    public HomeController(){
        System.out.println(getClass().getName() + "()생성");
    }

    @RequestMapping("/")
    public String home(){
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public void home(Model model){}

    // 현재 로그인한 정보 Authentication 보기 (디버깅 등 용도로 활용)

    @RequestMapping("/auth")
    @ResponseBody
    public Authentication auth(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("test1")
    @ResponseBody
    public String test1(@RequestParam(defaultValue = "1234") String password){
        return password + " => " + passwordEncoder.encode(password);
    }




}
