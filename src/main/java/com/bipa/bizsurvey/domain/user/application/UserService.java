package com.bipa.bizsurvey.domain.user.application;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bipa.bizsurvey.domain.user.dto.mypage.ClaimResponse;
import com.bipa.bizsurvey.domain.user.dto.mypage.UserInfoResponse;
import com.bipa.bizsurvey.domain.user.dto.mypage.UserInfoUpdateRequest;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.dto.*;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import com.bipa.bizsurvey.global.config.jwt.JwtProcess;
import com.bipa.bizsurvey.global.config.jwt.JwtVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.Id;
import javax.transaction.Transactional;
import java.util.*;

import static java.util.stream.Collectors.toList;

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

    //내정보 조회
    public UserInfoResponse userInfo(Long id){
        Optional<User> user = userRepository.findById(id);
        return user.map(UserInfoResponse::new).orElse(null);
    }
    //내정보 수정
    public void userInfoUpdate(UserInfoUpdateRequest request){
        User user = userRepository.findById(request.getId()).orElseThrow();
        user.userInfoUpdate(request);
        userRepository.save(user);
    }

    //플랜 조회

    //일반 플랜 가입
    public String planUpdate(LoginUser loginUser, Plan plan){
        User user = userRepository.findById(loginUser.getId()).orElseThrow();
        user.userPlanUpdate(plan);
        userRepository.save(user);

        String jwtToken = JWT.create()
                .withSubject("bank")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVO.EXPIRATION_TIME))
                .withClaim("id", loginUser.getLoginRequest().getId())
                .withClaim("nickname", loginUser.getLoginRequest().getNickname())
                .withClaim("email", loginUser.getLoginRequest().getEmail())
                .withClaim("role", plan + "")
                .sign(Algorithm.HMAC512(JwtVO.SECRET));
        return jwtToken;
    }

    //기업 플랜 가입

    //받은 신고 조회








}