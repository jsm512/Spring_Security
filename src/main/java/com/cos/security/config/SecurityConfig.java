package com.cos.security.config;

import com.cos.security.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity //시큐리티 활성화를 위한 어노테이션 -> 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다!
// secured 어노테이션 활성화 -> 간단하게 권한이 있는 역할을 걸러주는 역할, preAuthorize라는 어노테이션 활성화
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig{

    /*
    @EnableWebSecurity -> SecurityConfig에서 등록한 필터가 스프링에서 기본 제공하는 필터에 등록이 된다
     */

    //BCryptPasswordEncoder class를 따로 만들어서 컨테이너에 등록해줌 <- 순환참조 오류 방지
//    @Bean // -> 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다
//    public BCryptPasswordEncoder encodePwd(){
//        return new BCryptPasswordEncoder();
//    }


    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;
    // 비밀번호 암호화

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/user/**").authenticated()
                .requestMatchers("/manager/**").hasAnyAuthority("ADMIN", "MANAGER")
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .anyRequest().permitAll()
                )
                //위에서 권한이 없는 페이지로 접근하게 되면 error 대신 login 페이지로 이동
                .formLogin(login -> login
                        .loginPage("/loginForm")
                        //.usernameParameter("")로  input의 name을 바꿔서 받을 수 있음
                        .loginProcessingUrl("/login") // /login 이라는 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행한다 -> 컨트롤러에 /login을 만들필요 없음
                        .defaultSuccessUrl("/") // 로그인 성공 시 메인 페이지로 이동 // -> loginForm으로 와서 로그인하면 /로 보내주는데 특정 페이지에서 로그인 하면 특정 페이지로 바로 이동
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/loginForm") // 구글 로그인이 완료된 뒤의 후처리가 필요함 -> 코드를 받는게 아니라 엑세스 토큰 + 사용자 프로필 정보를 받아옴
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(principalOauth2UserService))
                );

        return http.build();
    }
}
