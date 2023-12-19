package com.bipa.bizsurvey.domain.user.application;

import com.bipa.bizsurvey.domain.user.dto.LoginInfoRequest;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    //

    private final UserRepository userRepository;

    // 시큐리티로 로그인이 될때, 시큐리티가 loadUserByUsername() 실행해서 username을 체크!!
    // 없으면 오류
    // 있으면 정상적으로 시큐리티 컨텍스트 내부 세션에 로그인된 세션이 만들어진다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userPS = userRepository.findByEmail(username).orElseThrow(
                () -> new InternalAuthenticationServiceException("인증 실패"));

        LoginInfoRequest loginInfoRequestDTO = LoginInfoRequest.builder()
                .id(userPS.getId())
                .email(userPS.getEmail())
                .nickname(userPS.getNickname())
                .password(userPS.getPassword())
                .planSubscribe(userPS.getPlanSubscribe())
                .build();
        return new LoginUser(loginInfoRequestDTO);
    }

}