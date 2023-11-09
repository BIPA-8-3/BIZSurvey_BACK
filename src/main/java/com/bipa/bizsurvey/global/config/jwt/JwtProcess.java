package com.bipa.bizsurvey.global.config.jwt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.user.dto.LoginRequest;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class JwtProcess {
    private final Logger log = LoggerFactory.getLogger(getClass());

    // 토큰 생성
    public static String create(LoginUser loginUser) {
        String jwtToken = JWT.create()
                .withSubject("bank")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVO.EXPIRATION_TIME))
                .withClaim("id", loginUser.getLoginRequest().getId())
                .withClaim("nickname", loginUser.getLoginRequest().getNickname())
                .withClaim("email", loginUser.getLoginRequest().getEmail())
                .withClaim("role", loginUser.getLoginRequest().getPlanSubscribe() + "")
                .sign(Algorithm.HMAC512(JwtVO.SECRET));

        return JwtVO.TOKEN_PREFIX + jwtToken;
    }

    // 토큰 검증
    public static LoginUser verify(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtVO.SECRET)).build().verify(token);
        Long id = decodedJWT.getClaim("id").asLong();
        String nickname = decodedJWT.getClaim("nickname").asString();
        String role = decodedJWT.getClaim("role").asString();
        String email = decodedJWT.getClaim("email").asString();
        LoginRequest user = LoginRequest.builder().id(id).nickname(nickname).email(email).planSubscribe(Plan.valueOf(role)).build();
        return new LoginUser(user);
    }
}