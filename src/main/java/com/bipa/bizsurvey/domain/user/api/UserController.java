package com.bipa.bizsurvey.domain.user.api;

import com.bipa.bizsurvey.domain.user.application.UserService;
import com.bipa.bizsurvey.domain.user.dto.RequestJoinDto;
import com.bipa.bizsurvey.domain.user.dto.Response;
import com.bipa.bizsurvey.domain.user.dto.ResponseJoinDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> singup(@Valid @RequestBody RequestJoinDto request){
        ResponseJoinDto joinRespDto = userService.join(request);
        return new ResponseEntity<>(new Response<>("회원가입 성공", joinRespDto), HttpStatus.CREATED);
    }

}
