package com.bipa.bizsurvey.domain.user.api;

import com.bipa.bizsurvey.domain.user.application.EmailSendService;
import com.bipa.bizsurvey.domain.user.application.UserService;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.user.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final EmailSendService emailSendService;

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

    @GetMapping("/test")
    public void checkAuthNumber(@AuthenticationPrincipal LoginUser loginUser){
        System.out.println(" ? " + loginUser.getUsername());
        System.out.println(" ? " + loginUser.getId());
        System.out.println(" ? " + loginUser.getNickname());
        System.out.println(" ? " +  loginUser.getAuthorities());
    }

    @PostMapping("/admin")
    public void getUserProfile(@AuthenticationPrincipal LoginUser loginUser) {
        System.out.println("test");
    }

}
