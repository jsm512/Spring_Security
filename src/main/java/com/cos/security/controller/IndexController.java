package com.cos.security.controller;

import com.cos.security.auth.PrincipalDetails;
import com.cos.security.model.User;
import com.cos.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // -> View를 return하겠다
public class IndexController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /* 일반적인 로그인과 OAuth2 로그인을 진행할 때 UserDetails, OAuth2User로 각각 가져와야되는 문제가 발생함
     -> 이때 X라는 class를 만들어 UserDetails와 OAuth2를 상속받는다면 X class로만 일반, OAuth2 로그인이 가능

    */
    //일반적인 로그인을 진행할 때 PrincipalDetails 객체로 받을 수 있음
    @GetMapping("/test/login")
    public @ResponseBody String testLogin(
//            Authentication authentication,
            @AuthenticationPrincipal PrincipalDetails userDetails){ //DI(의존성 주입)
        System.out.println("/test/login ====================== ");
//        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//        System.out.println("authentication : " + principalDetails.getUser());

        System.out.println("userDetails : " + userDetails.getUser());
        return "세션 정보 확인하기";
    }

    // OAuth2 로그인을 진행할 때 OAuth2User 객체로 받음
    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(
//            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oauth){ //DI(의존성 주입)
        System.out.println("/test/oauth/login ====================== ");
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        System.out.println("authentication : " + oAuth2User.getAttributes());

        System.out.println("oauth2User : " + oauth.getAttributes());
        return "OAuth 세션 정보 확인하기";
    }
    @GetMapping()
    public String index(){
        //머스테치 -> 기본 폴더 : src/main/resources/
        //뷰리졸버 설정 : templates (prefix), .mustache (suffix)
        return "index";
    }

    /*
    OAuth2 로그인을 해도 PrincipalDetails
    일반 로그인을 해도 PrincipalDetails로 받아올 수 있음
     */
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails : " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin(){
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager(){
        return "manager";
    }

    // 스프링시큐리티가 해당주소를 가지고 있어서 접근이 안됨 -> SecurityConfig 파일 생성 후 작동안함
    @GetMapping("/loginForm")
    public String loginForm(){
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm";
    }
    @PostMapping("/join")
    public String join(User user){
        System.out.println(user);
        user.setRole("USER");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @Secured("ADMIN") //특정 메소드에 간단하게 권한을 부여하고 싶으면 어노테이션으로 가능
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')") //data 메소드가 실행되기 전에 실행됨 //여러 명의 권한 설정을 원할 때 Secured 대신 사용
    @GetMapping("/data")
    public @ResponseBody String data(){
        return "데이터정보";
    }
}
