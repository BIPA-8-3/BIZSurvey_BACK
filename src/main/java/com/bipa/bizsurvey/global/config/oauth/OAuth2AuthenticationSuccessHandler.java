package com.bipa.bizsurvey.global.config.oauth;


import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.global.common.RedisService;
import com.bipa.bizsurvey.global.config.jwt.JwtProcess;
import com.bipa.bizsurvey.global.config.jwt.JwtVO;
import com.bipa.bizsurvey.global.util.CustomResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RedisService redisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String jwtToken = JwtProcess.create(loginUser);
        String refreshJwtToken = JwtProcess.refreshCreate(loginUser, redisService);
        response.addHeader(JwtVO.HEADER, jwtToken);
        response.addHeader(JwtVO.REFRESH_HEADER, refreshJwtToken);
        CustomResponseUtil.success(response, "로그인 성공", jwtToken, refreshJwtToken);
    }
}