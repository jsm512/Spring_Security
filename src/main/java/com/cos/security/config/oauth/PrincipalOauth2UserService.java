package com.cos.security.config.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    //후처리 되는 함수 -> 구글로 부터 받은 userRequest 데이터에 대한 후처리를 담당함
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //RegistrationId로 어떤 OAuth로 로그인 했는지 확인 가능
        System.out.println("userRequest : " + userRequest.getClientRegistration());
        System.out.println("getAccessToken : " + userRequest.getAccessToken().getTokenValue());
        /*
        super.loadUser가 하는 역할 ->
        우리가 제일 처음에 구글 로그인 버튼을 클릭하면 -> 구글 로그인 창이 뜸 -> 로그인 진행 -> 완료
        code를 return 받는다(OAuth Client 라이브러리가 받아줌) -> code를 통해 엑세스토큰을 요청
        엑세스토큰을 받음 : 여기까지가 userRequest 정보임
        userRequest로 뭘 하냐? -> 회원 프로필을 받음 -> 이때 사용되는 함수가 loadUser()함수임 : loadUser() 함수를 통해 구글로 부터 회원 프로필을 받을 수 있다
         */
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("getAttributes : " + super.loadUser(userRequest).getAttributes());


        return super.loadUser(userRequest);
    }
}
