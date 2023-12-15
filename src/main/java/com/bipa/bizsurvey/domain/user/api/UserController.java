package com.bipa.bizsurvey.domain.user.api;

import com.bipa.bizsurvey.domain.user.application.EmailSendService;
import com.bipa.bizsurvey.domain.user.application.UserService;
import com.bipa.bizsurvey.domain.user.dto.*;
import com.bipa.bizsurvey.domain.user.dto.mypage.*;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import com.bipa.bizsurvey.global.common.storage.Domain;
import com.bipa.bizsurvey.global.common.storage.StorageService;
import com.bipa.bizsurvey.global.config.jwt.JwtVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final EmailSendService emailSendService;
    private final StorageService storageService;


    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> singup(@Valid @RequestBody JoinRequest request){
        userService.join(request);
        return ResponseEntity.ok().body("회원가입이 정상적으로 처리되었습니다.");
    }

    //이메일 인증 번호 전송
    @PostMapping("/signup/send-email")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody EmailCheckRequest request) throws Exception {
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
    public ResponseEntity<?> userInfoUpdate(@AuthenticationPrincipal LoginUser loginUser,
                                            @Valid @RequestBody UserInfoUpdateRequest updateRequest,
                                            HttpServletResponse response){

        String token = userService.userInfoUpdate(loginUser, updateRequest);
        response.addHeader(JwtVO.HEADER, JwtVO.TOKEN_PREFIX + token);

        return ResponseEntity.ok().body("회원 정보가 수정되었습니다.");
    }

    //플랜 조회
    @GetMapping("/user/plan")
    public ResponseEntity<?> userPlan(@AuthenticationPrincipal LoginUser loginUser){
        UserPlanResponse planResponse = userService.userPlan(loginUser.getId());
        return ResponseEntity.ok().body(planResponse);
    }

    //내가 신고한 내역
    @GetMapping("/user/claim")
    public ResponseEntity<?> claimList(@AuthenticationPrincipal LoginUser loginUser){
        List<UserClaimResponse> claimList = userService.userClaim(loginUser.getId());
        return ResponseEntity.ok().body(claimList);
    }

    //구독 신청
    @PatchMapping ("/plan/{plan}")
    public ResponseEntity<?> planUpdate(@AuthenticationPrincipal LoginUser loginUser,
                                        @PathVariable Plan plan, HttpServletResponse response){
        String token = userService.planUpdate(loginUser, plan);
        response.addHeader(JwtVO.HEADER, JwtVO.TOKEN_PREFIX + token);
        return ResponseEntity.ok().body(plan.getValue() + "으로 변경되었습니다.");
    }

    //Tokken 재발급 요청
    @GetMapping("/refresh")
    public void accessTokenRefresh(HttpServletRequest request, HttpServletResponse response){
        String authorizationHeader = request.getHeader("refreshAuthorization");
        String token = userService.accessTokenRefresh(authorizationHeader);
        response.addHeader(JwtVO.HEADER, JwtVO.TOKEN_PREFIX + token);
    }

    //비밀번호 변경 > 이메일 존재 확인
    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@Valid @RequestBody EmailCheckRequest request) throws Exception {
        emailSendService.checkEmail(request.getEmail());
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

    //프로필 업로드

    //마이페이지 > 설문 게시물 조회
    @GetMapping("/user/s-community/list")
    public ResponseEntity<?> userSurveyCommunityList(@PageableDefault(size = 10) Pageable pageable,
                                               @AuthenticationPrincipal LoginUser loginUser){

        return ResponseEntity.ok().body(userService.getSurveyPostList(pageable, loginUser.getId())); // 200 OK
    }


    //마이페이지 > 커뮤니티 게시물 조회
    @GetMapping("/user/community/list")
    public ResponseEntity<?> userCommunityList(@PageableDefault(size = 10) Pageable pageable,
                                         @AuthenticationPrincipal LoginUser loginUser){

        return ResponseEntity.ok().body(userService.getPostList(pageable, loginUser.getId())); // 200 OK
    }



    @PatchMapping("/signup/additional")
    public ResponseEntity<?> additional(@Valid @RequestBody UserAdditionalJoinRequest updateRequest,
                                            HttpServletResponse response){

        Map<String, String> token = userService.additionalJoin(updateRequest);

        response.addHeader(JwtVO.HEADER, JwtVO.TOKEN_PREFIX + token.get("token"));
        response.addHeader(JwtVO.REFRESH_HEADER, JwtVO.TOKEN_PREFIX + token.get("refreshToken"));



        return ResponseEntity.ok().body("회원 정보가 저장되었습니다.");
    }


    @PostMapping("/user/profile-picture")
    public ResponseEntity<String> updateProfilePicture(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam("file") MultipartFile file) {

        try {
            String url = storageService.uploadFile(file, Domain.USER, null);
            System.out.println(url);
            return ResponseEntity.ok("프로필 이미지가 업데이트되었습니다.");
        } catch (Exception e) {
            e.printStackTrace(); // 예외를 로그로 출력합니다.
            return ResponseEntity.status(500).body("프로필 이미지 업데이트 중 오류가 발생했습니다.");
        }
    }

//    public void getAccessToken(String autorize_code) {
//
//        final String RequestUrl = "https://kauth.kakao.com/oauth/token";
//        final List<NameValuePair> postParams = new ArrayList<NameValuePair>();
//        postParams.add(new BasicNameValuePair("grant_type", "authorization_code"));
//        postParams.add(new BasicNameValuePair("client_id", clientId));
//        postParams.add(new BasicNameValuePair("redirect_uri", redirectUri));
//        postParams.add(new BasicNameValuePair("code", autorize_code));
//
//
//        final HttpClient client = HttpClientBuilder.create().build();
//        final HttpPost post = new HttpPost(RequestUrl);
//        JsonNode returnNode = null;
//
//        try {
//
//            post.setEntity(new UrlEncodedFormEntity(postParams)); // RequestUrl 접근 후 사용자의 정보를 담은 postParams 객체를 전달
//
//            final HttpResponse response = client.execute(post);
//            final int responseCode = response.getStatusLine().getStatusCode();
//
//            // JSON 형태 반환값 처리
//            ObjectMapper mapper = new ObjectMapper();
//            returnNode = mapper.readTree(response.getEntity().getContent());
//
//        } catch (UnsupportedEncodingException e) {
//
//            e.printStackTrace();
//
//        } catch (ClientProtocolException e) {
//
//            e.printStackTrace();
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//
//        } finally {
//            // clear resources
//        }
//
//    }

}
