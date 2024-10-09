package com.cos.security.config.oauth;

import com.cos.security.auth.PrincipalDetails;
import com.cos.security.config.CustomBCryptPasswordEncoder;
import com.cos.security.config.oauth.provider.FacebookUserInfo;
import com.cos.security.config.oauth.provider.GoogleUserInfo;
import com.cos.security.config.oauth.provider.NaverUserInfo;
import com.cos.security.config.oauth.provider.OAuth2UserInfo;
import com.cos.security.model.User;
import com.cos.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public PrincipalOauth2UserService(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Autowired
    private UserRepository userRepository;

    //후처리 되는 함수 -> 구글로 부터 받은 userRequest 데이터에 대한 후처리를 담당함
    //함수 종료 시 @AuthenticationPrincipal 어노테이션이 만들어진다
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
        System.out.println("getAttributes : " + oAuth2User.getAttributes());

        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
            System.out.println("페이스북 로그인 요청");
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        }
        else{
            System.out.println("구글과 페이스북만 지원해요");
        }

        String provider = oAuth2UserInfo.getProvider(); // google
        //provider에 따라 Id의 key값이 다름 -> google은 sub, facebook은 id 등 ...
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider+"_"+providerId; //google_sub
        String password = bCryptPasswordEncoder.encode("머야머야");
        String email = oAuth2UserInfo.getEmail();
        String role = "USER";

        //이미 회원가입이 되어 있는 경우
        User userEntity = userRepository.findByUsername(username);

        if(userEntity == null){
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes()); //Authentication 객체로 리턴
    }
}
