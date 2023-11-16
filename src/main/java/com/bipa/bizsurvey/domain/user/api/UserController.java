package com.bipa.bizsurvey.domain.user.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bipa.bizsurvey.domain.user.application.ClaimService;
import com.bipa.bizsurvey.domain.user.application.EmailSendService;
import com.bipa.bizsurvey.domain.user.application.UserService;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.user.dto.*;
import com.bipa.bizsurvey.domain.user.dto.mypage.ClaimResponse;
import com.bipa.bizsurvey.domain.user.dto.mypage.UserInfoResponse;
import com.bipa.bizsurvey.domain.user.dto.mypage.UserInfoUpdateRequest;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import com.bipa.bizsurvey.domain.user.repository.ClaimRepository;
import com.bipa.bizsurvey.global.config.jwt.JwtVO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final EmailSendService emailSendService;
    private final ClaimService claimService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> singup(@Valid @RequestBody JoinRequest request){
        userService.join(request);
        return ResponseEntity.ok().body("회원가입이 정상적으로 처리되었습니다.");
    }

    //이메일 인증 번호 전송
    @PostMapping("/signup/send-email")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody EmailCheckRequest request){
        emailSendService.authEmail(request);
        return ResponseEntity.ok().body("이메일로 인증번호가 전송되었습니다.");
    }

    //닉네임 중복 확인
    @PostMapping("/signup/check-nickname")
    public ResponseEntity<?> checkNickname(@Valid @RequestBody NicknameCheckRequest request){
        userService.nickNameCheck(request.getNickname());
        return ResponseEntity.ok().body("사용 가능한 닉네임입니다.");
    }

    // 이메일 인증
    @PostMapping("/signup/check-authnumber")
    public ResponseEntity<?> checkAuthNumber(@Valid @RequestBody MailAuthRequest request){
        emailSendService.authCheck(request);
        return ResponseEntity.ok().body("인증되었습니다.");
    }

    //마이페이지 > 내정보 조회
    @GetMapping("/user/info")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal LoginUser loginUser){
        UserInfoResponse infoResponse = userService.userInfo(loginUser.getId());
        return ResponseEntity.ok().body(infoResponse);
    }

    //마이페이지 > 내정보수정
    @PatchMapping("/user/info")
    public ResponseEntity<?> userInfoUpdate(@Valid @RequestBody UserInfoUpdateRequest request){
        userService.userInfoUpdate(request);
        return ResponseEntity.ok().body("회원 정보가 수정되었습니다.");
    }

    // 내가 신고한 내역
    @GetMapping("/user/claim")
    public ResponseEntity<?> claimList(@AuthenticationPrincipal LoginUser loginUser){
        List<ClaimResponse> claimList = claimService.claimList(loginUser.getId());
        return ResponseEntity.ok().body(claimList);
    }

    //구독 신청
    @PatchMapping ("/plan/{plan}")
    public ResponseEntity<?> planUpdate(@AuthenticationPrincipal LoginUser loginUser,
                                        @PathVariable Plan plan, HttpServletResponse response){
        String token = userService.planUpdate(loginUser, plan);
        response.addHeader(JwtVO.HEADER, JwtVO.TOKEN_PREFIX + token);
        return ResponseEntity.ok().body(plan + "으로 구독 신청되었습니다.");
    }

    //Tokken 재발급 요청
    @GetMapping("/refresh")
    public void accessTokenRefresh(HttpServletRequest request, HttpServletResponse response){
        String authorizationHeader = request.getHeader("refreshAuthorization");

        String token = userService.accessTokenRefresh(authorizationHeader);
        response.addHeader(JwtVO.HEADER, JwtVO.TOKEN_PREFIX + token);
    }

    //비밀번호 변경 > 이메일 존재 확인
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@Valid @RequestBody EmailCheckRequest request) throws Exception {
        emailSendService.sendPasswordEmail(request.getEmail());
        return ResponseEntity.ok().body("메일을 전송하였습니다.");
    }

    //비민번호 재설정 링크가 유효한지 체크
    @GetMapping("/email-validation/{key}")
    public ResponseEntity<?> checkEmail(@PathVariable String key){
        String email = emailSendService.emailValidation(key);
        return ResponseEntity.ok().body(email);
    }

    //비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<?> passwordUpdate(@Valid @RequestBody PasswordUpdateRequest request){
        userService.passwordUpdate(request);
        return ResponseEntity.ok().body("비밀번호를 재설정하였습니다.");
    }

}
