package com.bipa.bizsurvey.global.config;

import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.global.common.RedisService;
import com.bipa.bizsurvey.global.config.jwt.AdminJwtAuthenticationFilter;
import com.bipa.bizsurvey.global.config.jwt.JwtAuthenticationFilter;
import com.bipa.bizsurvey.global.config.jwt.JwtAuthorizationFilter;
import com.bipa.bizsurvey.global.util.CustomResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {
    private final RedisService redisService;
    private final UserRepository userRepository;
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // JWT 필터 등록
    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager, redisService, userRepository));
            builder.addFilter(new JwtAuthorizationFilter(authenticationManager));
            //admin login
            builder.addFilter(new AdminJwtAuthenticationFilter(authenticationManager, redisService, userRepository));
            super.configure(builder);
        }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin();
        http.csrf().disable();
        http.cors().configurationSource(configurationSource());
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.formLogin().disable();
        http.httpBasic().disable();
        http.apply(new CustomSecurityFilterManager());
        // 인증 실패 (인증되지 않은 사용자가 접근했을때)
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            CustomResponseUtil.noLogin(response, "로그인을 진행해 주세요.");
        });

        // 권한 실패(인가되지 않은 사용자가 접근했을때)
        http.exceptionHandling().accessDeniedHandler((request, response, e) -> {
            CustomResponseUtil.forbidden(response, "접근 권한이 없습니다");
        });

        http.authorizeRequests(
                authorize -> authorize.antMatchers("/user/**").authenticated()
                        .antMatchers("/plan/**").authenticated()
                        .antMatchers("/admin/**").access("hasRole('ADMIN')")
                        .antMatchers("/signup/**", "/login/**", "/refresh/**", "/oauth2/**").permitAll()
        );

//        http.oauth2Login()
//                .loginProcessingUrl("/oauth2/authorization/kakao")
//                .userInfoEndpoint().userService(customOAuth2UserService)
//                .and()
//                .defaultSuccessUrl("http://localhost:3000/", true);
                //.failureHandler(oAuth2AuthenticationFailureHandler);
        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("PATCH");
        configuration.addAllowedMethod("DELETE");
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Refreshauthorization");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

