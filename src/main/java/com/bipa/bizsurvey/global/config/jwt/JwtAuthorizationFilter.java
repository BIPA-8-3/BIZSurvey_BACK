package com.bipa.bizsurvey.global.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.global.util.CustomResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * 모든 주소에서 동작함 (토큰 검증)
 */
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    // JWT 토큰 헤더를 추가하지 않아도 해당 필터는 통과는 할 수 있지만, 결국 시큐리티단에서 세션 값 검증에 실패함.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        isHeaderVerify(request, response);
        System.out.println("작동하는가??");
        System.out.println(isHeaderVerify(request, response));
        if (isHeaderVerify(request, response)) {
            try {
                log.debug("디버그 : 토큰이 존재함");
                String token = request.getHeader(JwtVO.HEADER).replace(JwtVO.TOKEN_PREFIX, "");
                System.out.println(token);
                LoginUser loginUser = JwtProcess.verify(token);
                log.debug("디버그 : 토큰이 검증이 완료됨");
                // 임시 세션 (UserDetails 타입 or username)
                Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser, null,
                        loginUser.getAuthorities()); // id, role 만 존재
                SecurityContextHolder.getContext().setAuthentication(authentication); //Authentication 객체 저장
                log.debug("디버그 : 임시 세션이 생성됨");
            }catch (TokenExpiredException e){
                CustomResponseUtil.noLogin(response, "Access 토큰이 만료되었습니다.");
            }


        }
        chain.doFilter(request, response);
    }

    private boolean isHeaderVerify(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader(JwtVO.HEADER);
        if (header == null || !header.startsWith(JwtVO.TOKEN_PREFIX)) {
            return false;
        } else {
            return true;
        }
    }
}
