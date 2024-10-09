package com.cos.security.auth;

import com.cos.security.model.User;
import com.cos.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
시큐리티 설정에서 loginProcessingUrl("/login");
/login 요청이 들어오면 -> 자동으로 UserDetailsService 타입으로 IoC 되어 있는 loadUserByUsername이 실행이된다 <- 규칙임~!
 */
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    //함수 종료 시 @AuthenticationPrincipal 어노테이션이 만들어진다
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByUsername(username);
        if(userEntity != null){
            // Authentication 내부의 UserDetails로 들어가게됨 -> Authentication은 시큐리티 session 내부로 들어가게됨
            return new PrincipalDetails(userEntity);
        }
        return null;
    }
}
