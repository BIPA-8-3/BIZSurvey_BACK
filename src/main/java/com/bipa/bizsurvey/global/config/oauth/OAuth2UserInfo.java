package com.bipa.bizsurvey.global.config.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuth2UserInfo {

    private Map<String, Object> attributes;
    private String name;
    private String email;
    private String gender;
    private String ageRange;
    private String profileImageUrl;
    private String birthday;

    @Builder
    public OAuth2UserInfo(Map<String, Object> attributes,
                           String name, String email, String gender, String ageRange, String profileImageUrl, String birthday) {
        this.attributes = attributes;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.ageRange = ageRange;
        this.profileImageUrl = profileImageUrl;
        this.birthday = birthday;
    }

    public static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuth2UserInfo.builder()
                .name(String.valueOf(kakaoProfile.get("nickname")))
                .email(String.valueOf(kakaoAccount.get("email")))
                .gender(String.valueOf(kakaoAccount.get("gender")))
                .ageRange(String.valueOf(kakaoAccount.get("age_range")))
                .birthday(String.valueOf(kakaoAccount.get("birthday")))
                .profileImageUrl(String.valueOf(kakaoProfile.get("profile_image_url")))
                .attributes(attributes)
                .build();
    }
}
