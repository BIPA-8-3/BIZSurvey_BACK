package com.bipa.bizsurvey.domain.user.api;

import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.dto.LoginInfoRequest;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.user.enums.Gender;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.global.common.RedisService;
import com.bipa.bizsurvey.global.config.jwt.JwtProcess;
import com.bipa.bizsurvey.global.config.oauth.OAuth2UserInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class OAuth2Controller {

    //
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final UserRepository userRepository;
    private final RedisService redisService;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @GetMapping("/kakao/clientId")
    public ResponseEntity<Map<String, String>> clientIdResponse(){

        Map<String, String> oauthProperties = new HashMap<>();
        oauthProperties.put("clientId", clientId);
        oauthProperties.put("redirectUri", redirectUri);

        return ResponseEntity.ok().body(oauthProperties);
    }

    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<LoginUser> oauth2LoginCallback(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // OAuth 2.0 인증 과정에서 사용된 ClientRegistration 정보 가져오기
        String clientRegistrationId = getClientRegistrationId(request);

        // 토큰 요청
        String tokenResponse = requestToken(code, response);

        LoginUser loginUser = oauthUserInfo(tokenResponse, response);
        String jwtToken = JwtProcess.create(loginUser);
        String refreshJwtToken = JwtProcess.refreshCreate(loginUser, redisService);

        HttpHeaders header = new HttpHeaders();

        // jwtToken 및 refreshJwtToken을 헤더에 추가
        header.add("Authorization", jwtToken);
        header.add("RefreshAuthorization", refreshJwtToken);
        System.out.println("test : : : " + jwtToken);
        return ResponseEntity.ok()
                .headers(header)
                .body(loginUser);
    }

    private String getClientRegistrationId(HttpServletRequest request) {
        // OAuth 2.0 인증 요청에 사용된 ClientRegistration 정보 가져오기
        ClientRegistration clientRegistration = clientRegistrationRepository
                .findByRegistrationId("kakao");

        if (clientRegistration != null) {
            return clientRegistration.getRegistrationId();
        } else {
            return "unknown";
        }
    }

    private String requestToken(String code, HttpServletResponse response) throws IOException {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "authorization_code");
        parameters.add("client_id", clientId);
        parameters.add("client_secret", clientSecret);
        parameters.add("redirect_uri", redirectUri);
        parameters.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.postForObject(tokenUrl, request, String.class);
        JSONObject jsonObject = new JSONObject(result.substring(result.indexOf('{')));
        String accessToken = jsonObject.getString("access_token");

        oauthUserInfo(accessToken, response);
        return accessToken;
    }


    public LoginUser oauthUserInfo(String accessToken, HttpServletResponse response) throws IOException {
        final String requestUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String responseBody = responseEntity.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> attributes = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
            OAuth2UserInfo userInfo =  OAuth2UserInfo.ofKakao(attributes);
            Optional<User> userOp =  userRepository.findByEmail(userInfo.getEmail());

            LoginInfoRequest loginInfoRequest;
            if(userOp.isPresent()){
                loginInfoRequest = LoginInfoRequest.builder()
                        .id(userOp.get().getId())
                        .email(userInfo.getEmail())
                        .nickname("")
                        .name(userInfo.getName())
                        .gender(Gender.valueOf((userInfo.getGender()).toUpperCase()))
                        .planSubscribe(userOp.get().getPlanSubscribe())
                        .provider("kakao")
                        .build();
                loginInfoRequest.setId(userOp.get().getId());
            }else{
                loginInfoRequest = LoginInfoRequest.builder()
                        .id(0L)
                        .email(userInfo.getEmail())
                        .nickname("")
                        .name(userInfo.getName())
                        .gender(Gender.valueOf((userInfo.getGender()).toUpperCase()))
                        .planSubscribe(Plan.COMMUNITY)
                        .provider("kakao")
                        .build();
                User user = loginInfoRequest.toEntity();
                User saveUser = userRepository.save(user);
            }
            return new LoginUser(loginInfoRequest, attributes);
        }
        return null;
    }

    @GetMapping("/nickname/existence")
    public ResponseEntity<?> existence(@RequestParam Long id){
        User user = userRepository.findById(id).orElseThrow();
        boolean check = !user.getNickname().isEmpty();
        return ResponseEntity.ok().body(check);
    }


}
