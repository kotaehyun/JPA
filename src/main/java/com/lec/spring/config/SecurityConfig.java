package com.lec.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ↓ Security 를 동작 시키지 않기
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer(){
//        return web -> web.ignoring().anyRequest();  // 어떠한 request 도 security 가 무시함
//
//    }

    // PasswordEncoder 를 bean 으로 IoC 에 등록
    // IoC 에 등록된다, IoC 내에선 '어디서든' 가져디가 사용할수 있다.
    @Bean
    public PasswordEncoder encoder(){
        System.out.println("PasswordEncoder bean 생성");
        return new BCryptPasswordEncoder();
    }

    // ↓ SecurityFilterChain 을 Bean 으로 등록해서 사용
    // Security 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(csrf -> csrf.disable())   // csrf 비활성화

                /**********************************************
                 * ① request URL 에 대한 접근 권한 세팅  : authorizeHttpRequests()
                 * .authorizeHttpRequests( AuthorizationManagerRequestMatcherRegistry)
                 **********************************************/
                .authorizeHttpRequests(auth -> auth
                        // URL 과 접근관한 세팅(들)
                        // ↓ /board/detail/** URL로 들어오는 요청은 '인증'만 필요.
                        .requestMatchers("/board/detail/**").authenticated()
                        // ↓"/board/write/**" ,"/board/update/**","/board/delete/**" URL로 들어오는 요청은 '인증'뿐 아니라 ROLE_MEMBER 나 ROLE_ADMIN 권한을 갖고 있어야 한다. ('인가')
                        .requestMatchers("/board/write/**","/board/update/**","/board/delete/**").hasAnyRole("MEMBER","ADMIN")
                        // ↓ 그 밖의 다른 요청은 모두 permit!
                        .anyRequest().permitAll())

                /********************************************
                 * ② 폼 로그인 설정
                 * .formLogin(HttpSecurityFormLoginConfigurer)
                 *  form 기반 인증 페이지 활성화.
                 *  만약 .loginPage(url) 가 세팅되어 있지 않으면 '디폴트 로그인' form 페이지가 활성화 된다
                 ********************************************/
                .formLogin(form -> form
                        .loginPage("/user/login") // 로그인 필요한 상황 발생시 매개변수의 url (로그인 폼) 으로 request 발생
                        .loginProcessingUrl("/user/login") // "/user/login" url 로 POST request 가 들어오면 시큐리티가 낚아채서 처리, 대신 로그인을 진행해준다(인증).
                                                           // 이와 같이 하면 Controller 에서 /user/login (POST) 를 굳이 만들지 않아도 된다!
                                                           // 위 요청이 오면 자동으로 UserDetailsService 타입 빈객체의 loadUserByUsername() 가 실행되어 인증여부 확인진행 <- 이를 제공해주어야 한다.
                        .defaultSuccessUrl("/") // '직접 /login' → /loginOk 에서 성공하면 "/" 로 이동시키기
                                                // 만약 다른 특정페이지에 진입하려다 로그인 하여 성공하면 해당 페이지로 이동 (너무 편리!)

                        // 로그인 성공직후 수행할 코드
                        // .successHandler(AuthenticationSuccessHandler)   // 로그인 성공후 수행할 코드.
                        .successHandler(new CustomLoginSuccessHandler("/home"))

                        // 로그인 실패하면 수행할 코드
                        // .failureHandler(AuthenticationFailureHandler)
                        .failureHandler(new CustomLoginFailureHandler())
                )
                /*******************************************
                * ③ 로그아웃 설정
                * .logout(LogoutConfiqurer)
                ********************************************/
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .logoutUrl("/user/logout")      // 로그아웃 수행 url
                       //.logoutSuccessUrl("/login?logout")  // 로그아웃 성공후 redirect url
                        .invalidateHttpSession(false)   // session invalidate (디폴트 true)
                       // 이따가 CustomLogoutSuccessHandler 에서 꺼낼 정보가 있기 때문에
                       // false 로 세팅한다

                       // .deleteCookies("JSESSIONID") // 쿠기 제거

                        // 로그아웃 성공후 수행할 코드
                        // .logoutSuccessHandler(LogoutSuccessHandler)
                        . logoutSuccessHandler(new CustomLogoutSuccessHandler())
                )
                /*********************************************
                * ④ 예외처리 설정
                * .exceptionHandling(ExceptionHandlingConfigure)
                *********************************************/
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        // 권한(Authorization) 오류 발생시 수행할 코드
                        // .accessDeniedHandler(AccessDeniedHandler)
                        .accessDeniedHandler(new CustomAccessDeniedHandler())


                )




                .build();
    }



}
