package com.bipa.bizsurvey.global.config.jwt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bipa.bizsurvey.domain.user.dto.LoginInfoRequest;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import com.bipa.bizsurvey.global.common.RedisService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
@RequiredArgsConstructor
public class JwtProcess {
    private final Logger log = LoggerFactory.getLogger(getClass());

    // 토큰 생성
    public static String create(LoginUser loginUser) {

        String jwtToken = JWT.create()
                .withSubject("biz")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVO.EXPIRATION_TIME))
                .withClaim("id", loginUser.getLoginInfoRequest().getId())
                .withClaim("role", loginUser.getLoginInfoRequest().getPlanSubscribe() + "")
                .sign(Algorithm.HMAC512(JwtVO.SECRET));
        return JwtVO.TOKEN_PREFIX + jwtToken;
    }

    public static String refreshCreate(LoginUser loginUser, RedisService redisService) {
        String jwtToken = JWT.create()
                .withSubject("biz")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVO.REFRESH_EXPIRATION_TIME))
                .withClaim("id", loginUser.getLoginInfoRequest().getId())
                .sign(Algorithm.HMAC512(JwtVO.SECRET));

        //Refresh Token Redis 저장
        redisService.saveData(String.valueOf(loginUser.getLoginInfoRequest().getId()), jwtToken, 30L * 24 * 60 * 60);

        return JwtVO.TOKEN_PREFIX + jwtToken;
    }


    // 토큰 검증
    public static LoginUser verify(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtVO.SECRET)).build().verify(token);
        Long id = decodedJWT.getClaim("id").asLong();
        String role = decodedJWT.getClaim("role").asString();
        LoginInfoRequest user = LoginInfoRequest.builder().id(id).planSubscribe(Plan.valueOf(role)).build();
        return new LoginUser(user);
    }
}