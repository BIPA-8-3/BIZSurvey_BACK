package com.bipa.bizsurvey.global.config.jwt;

import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.dto.LoginInfoRequest;
import com.bipa.bizsurvey.domain.user.dto.LoginRequest;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.global.common.RedisService;
import com.bipa.bizsurvey.global.util.CustomResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private AuthenticationManager authenticationManager;
    private RedisService redisService;
    private UserRepository userRepository;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, RedisService redisService, UserRepository userRepository) {
        super(authenticationManager);
        setFilterProcessesUrl("/login");
        this.authenticationManager = authenticationManager;
        this.redisService = redisService;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.debug("디버그 : attemptAuthentication 호출됨");
        try {
            ObjectMapper om = new ObjectMapper();
            LoginRequest loginInfoRequestReqDto = om.readValue(request.getInputStream(), LoginRequest.class);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginInfoRequestReqDto.getEmail(), loginInfoRequestReqDto.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            return authentication;
        } catch (Exception e) {
            // unsuccessfulAuthentication 호출함
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    // 로그인 실패
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed){
        CustomResponseUtil.fail(response, "로그인 실패");
    }

    // return authentication 잘 작동하면 successfulAuthentication 메서드 호출
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        LoginUser loginUser = (LoginUser) authResult.getPrincipal();
        User user = userRepository.findById(loginUser.getId())
                .orElseThrow(() -> new UserException(UserExceptionType.NON_EXIST_USER));

        String dateTimeString = user.getForbiddenDate();

        if ("forbidden".equals(dateTimeString)) {
            CustomResponseUtil.forbidden(response, "영구 정지");
            return;
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (dateTimeString != null && !dateTimeString.equals("")) {
            LocalDateTime targetDateTime = LocalDateTime.parse(dateTimeString, formatter);
            if (currentDateTime.isBefore(targetDateTime)) {
                CustomResponseUtil.forbidden(response, "정지");
                return;
            }
        }

        user.forbiddenDateUpdate("");
        userRepository.save(user);

        String jwtToken = JwtProcess.create(loginUser);
        response.addHeader(JwtVO.HEADER, jwtToken);

        String refreshToken = JwtProcess.refreshCreate(loginUser, redisService);
        response.addHeader(JwtVO.REFRESH_HEADER, refreshToken);

        CustomResponseUtil.success(response, "로그인 성공", jwtToken, refreshToken);
    }
}
