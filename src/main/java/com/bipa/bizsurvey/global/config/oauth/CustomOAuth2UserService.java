package com.bipa.bizsurvey.global.config.oauth;

import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.dto.LoginInfoRequest;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.user.enums.Gender;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService{

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.ofKakao(oAuth2User.getAttributes());
        Optional<User> userOp =  userRepository.findByEmail(oAuth2UserInfo.getEmail());

        if(userOp.isPresent()){
            LoginInfoRequest loginInfoRequest = LoginInfoRequest.builder()
                    .id(userOp.get().getId())
                    .email(oAuth2UserInfo.getEmail())
                    .nickname("")
                    .name(oAuth2UserInfo.getName())
                    .gender(Gender.valueOf((oAuth2UserInfo.getGender()).toUpperCase()))
                    .planSubscribe(Plan.COMMUNITY)
                    .build();
            loginInfoRequest.setId(userOp.get().getId());
            return new LoginUser(loginInfoRequest, oAuth2User.getAttributes());
        }else{
            LoginInfoRequest loginInfoRequest = LoginInfoRequest.builder()
                    .id(0L)
                    .email(oAuth2UserInfo.getEmail())
                    .nickname("")
                    .name(oAuth2UserInfo.getName())
                    .gender(Gender.valueOf((oAuth2UserInfo.getGender()).toUpperCase()))
                    .planSubscribe(Plan.COMMUNITY)
                    .build();
            User user = loginInfoRequest.toEntity();
            User saveUser = userRepository.save(user);
            return new LoginUser(new LoginInfoRequest(saveUser), oAuth2User.getAttributes());
        }



    }

}
