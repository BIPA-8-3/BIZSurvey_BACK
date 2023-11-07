package com.bipa.bizsurvey.domain.user.application;

import com.bipa.bizsurvey.domain.user.dao.UserRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.dto.RequestJoinDto;
import com.bipa.bizsurvey.domain.user.dto.ResponseJoinDto;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ResponseJoinDto join(RequestJoinDto joinDto){
        Optional<User> userOptional = userRepository.findByEmail(joinDto.getEmail());

        if(userOptional.isPresent()){
            
        }

        User user = userRepository.save(joinDto.toEntity(passwordEncoder));
        return new ResponseJoinDto(user);
    }

    // 닉네임 중복 확인
    public boolean nickNameCheck(String nickname){
        return true;
    }

}