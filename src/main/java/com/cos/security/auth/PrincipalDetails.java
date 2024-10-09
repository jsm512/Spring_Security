package com.cos.security.auth;

/*
시큐리티가 /login 주소 요청을 낚아채서 로그인을 진행할 때,
login 진행이 완료가 되면 session을 만들어줘야됨
일반적인 session말고 시큐리티가 가지고 있는 session이 있다 -> 같은 session 공간에 시큐리티만의 session이 있음
key값으로 구분하게됨 -> Security ContextHolder라는 key값에 session 정보를 저장하게된다.
시큐리티가 가진 session에 들어갈 수 있는 Object는 정해져 있음 -> Authentication 타입의 객체
이 Authentication 객체 안에는 User 정보가 있어야된다
User 클래스도 정해져 있다 -> User Object의 타입은 UserDetails 타입 객체로 정해져있음

다시 말해, Security만의 session 영역에 session 정보를 저장할 때
저장되는 객체는 Authentication 객체로 정해져 있고, Authentication 객체에 User 정보를 저장할 때,
저장되는 객체는 UserDetails로 정해져 있다.

PrincipalDetails가 UserDetails 인터페이스를 상속받으면 -> PrincipalDetails 역시 Authentication 안에 저장할 수 있음


PrincipalDetails를 만드는 이유는 두 가지 목적이 있다
Security가 들고 있는 Session 정보로 들어가기 위해 (Authentication 객체를 통해)
Authentication 객체가 받을 수 있는 Type은 두가지 -> OAuth2User, UserDetails
그러나, OAuth2User와 UserDetails로는 User의 정보를 찾을 수 없음
그래서 PrincipalDetails라는 class를 만들고 UserDetails를 상속받아 User Object를 받음
UserDetails -> PrincipalDetails(User Object를 들고 있어 Session 정보를 가져올 수 있음)
마찬가지로 이유로 OAuth2User도 상속받음

이제 어떤 로그인이 들어와도 PrincipalDetails를 통해 User에 접근할 수 있음
 */

import com.cos.security.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

//@Service로 메모리에 띄우지 않는 이유 -> 나중에 강제로 띄워줄거라 미리 설정할 필요 없음
@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;
    private Map<String, Object> attributes;
    //일반 로그인 생성자
    public PrincipalDetails(User user){
        this.user = user;
    }

    //OAuth2 로그인 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes){
        this.user = user;
        this.attributes = attributes;
    }


    // 해당 User의 권한을 return하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //return 타입에 맞게 Collection<GrantedAuthority> 객체를 만들어줌
        Collection<GrantedAuthority> collect = new ArrayList<>();
        // 만들어진 객체에 GrantedAuthority가 들어가야되니까 GrantedAuthority 객체 만들어주고 user의 권한 리턴
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    @Override
    public boolean isEnabled(){
        /*
        우리 사이트에서 1년동안 회원이 로그인을 안하면 -> 휴면 계정으로 만들기로 함 (이때, User Model에 Timestamp로 loginDate를 가지고 있어야됨)
        user.getLoginDate();의 값과 현재 시간을 계산해 => 1년을 초과하면 return false, 아니면 true로 만들면 된다!!
         */
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return null;
    }
}
