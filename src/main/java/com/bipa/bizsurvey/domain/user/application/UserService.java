package com.bipa.bizsurvey.domain.user.application;

import com.bipa.bizsurvey.domain.user.dao.UserRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.dto.*;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    
    public ResponseJoinDto join(JoinRequest joinDto){

        User user = userRepository.save(joinDto.toEntity(passwordEncoder));
        return new ResponseJoinDto(user);
    }

    // 닉네임 중복 확인
    public void nickNameCheck(String nickname){
        Optional<User> nicknameUser = userRepository.findByNickname(nickname);
        if(nicknameUser.isPresent()){
            throw new UserException(UserExceptionType.ALREADY_EXIST_NICKNAME);
        }
    }





}