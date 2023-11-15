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
import com.bipa.bizsurvey.domain.user.repository.ClaimRepository;
import com.bipa.bizsurvey.global.config.jwt.JwtVO;
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


    @PostMapping("/signup")
    public ResponseEntity<?> singup(@Valid @RequestBody JoinRequest request){
        ResponseJoinDto joinRespDto = userService.join(request);
        return ResponseEntity.ok().body("회원가입이 정상적으로 처리되었습니다.");
    }

    @PostMapping("/signup/send-email")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody EmailCheckRequest request){
        emailSendService.authEmail(request);
        return ResponseEntity.ok().body("이메일로 인증번호가 전송되었습니다.");
    }

    @PostMapping("/signup/check-nickname")
    public ResponseEntity<?> checkNickname(@Valid @RequestBody NicknameCheckRequest request){
        userService.nickNameCheck(request.getNickname());
        return ResponseEntity.ok().body("사용 가능한 닉네임입니다.");
    }

    @PostMapping("/signup/check-authnumber")
    public ResponseEntity<?> checkAuthNumber(@Valid @RequestBody MailAuthRequest request){
        emailSendService.authCheck(request);
        return ResponseEntity.ok().body("인증되었습니다.");
    }

    @GetMapping("/user/info")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal LoginUser loginUser){
        UserInfoResponse infoResponse = userService.userInfo(loginUser.getId());
        return ResponseEntity.ok().body(infoResponse);
    }

    @PatchMapping("/user/info")
    public ResponseEntity<?> userInfoUpdate(@Valid @RequestBody UserInfoUpdateRequest request){
        System.out.println(request.toString());
        userService.userInfoUpdate(request);
        return ResponseEntity.ok().body("");
    }

    // 내가 신고한 내역
    @GetMapping("/user/claim")
    public ResponseEntity<?> claimList(@AuthenticationPrincipal LoginUser loginUser){
        List<ClaimResponse> claimList = claimService.claimList(loginUser.getId());
        return ResponseEntity.ok().body(claimList);
    }

    //구독 신청
    @PatchMapping ("/plan/{plan}")
    public ResponseEntity<?> planUpdate(@AuthenticationPrincipal LoginUser loginUser, @PathVariable Plan plan, HttpServletResponse response){
        String token = userService.planUpdate(loginUser, plan);
        response.addHeader(JwtVO.HEADER, JwtVO.TOKEN_PREFIX + token);
        return ResponseEntity.ok().body("");
    }

    @PostMapping("/admin")
    public void getUserProfile(@AuthenticationPrincipal LoginUser loginUser) {
        System.out.println("test");
    }


    @GetMapping("/test")
    public void checkAuthNumber(@AuthenticationPrincipal LoginUser loginUser){
        System.out.println(" ? " + loginUser.getUsername());
        System.out.println(" ? " + loginUser.getId());
        System.out.println(" ? " + loginUser.getNickname());
        System.out.println(" ? " +  loginUser.getAuthorities());
    }

    @GetMapping("/test2")
    public void checkAuthNumber2(){
    }

}
