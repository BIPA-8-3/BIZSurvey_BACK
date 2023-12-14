package com.bipa.bizsurvey.domain.admin.api;

import com.bipa.bizsurvey.domain.admin.application.AdminClaimService;
import com.bipa.bizsurvey.domain.admin.application.AdminUserService;
import com.bipa.bizsurvey.domain.admin.dto.claim.ClaimDetailResponse;
import com.bipa.bizsurvey.domain.admin.dto.claim.ClaimListResponse;
import com.bipa.bizsurvey.domain.admin.dto.claim.ClaimUnProcessingRequest;
import com.bipa.bizsurvey.domain.admin.dto.user.AdminUserSignupCountResponse;
import com.bipa.bizsurvey.domain.admin.dto.user.UserSearchRequest;
import com.bipa.bizsurvey.domain.community.application.PostService;
import com.bipa.bizsurvey.domain.community.application.SurveyPostService;
import com.bipa.bizsurvey.domain.user.application.UserService;
import com.bipa.bizsurvey.domain.user.dto.mypage.UserInfoResponse;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminUserService adminUserService;
    private final AdminClaimService adminClaimService;
    private final PostService postService;
    private final UserService userService;
    private final SurveyPostService surveyPostService;

    //회원목록
    @GetMapping("/users")
    public ResponseEntity<?> getUser(@PageableDefault(size = 10) Pageable pageable,
                                     @RequestBody(required = false) UserSearchRequest userSearchRequest){
        return ResponseEntity.ok().body(adminUserService.getUserList(pageable, userSearchRequest));
    }

    //회원목록 상세
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getOneUser(@PathVariable Long id){
        UserInfoResponse infoResponse = userService.userInfo(id);
        return ResponseEntity.ok().body(infoResponse);
    }

    //플랜 회원 조회
    @GetMapping("/user/plan/{plan}")
    public ResponseEntity<?> planUser(@PathVariable Plan plan){

        return ResponseEntity.ok().body((adminUserService.getPlanUserList(plan)).size());
    }

    //커뮤니티 목록
    @GetMapping("/community")
    public ResponseEntity<?> getPostList(@PageableDefault(size = 20) Pageable pageable){
        return ResponseEntity.ok().body(postService.getPostList(pageable));
    }

    //설문 커뮤니티 목록
    @GetMapping("/s-community")


    public ResponseEntity<?> getSurveyPost(@PageableDefault(size = 20) Pageable pageable){
        return ResponseEntity.ok().body(surveyPostService.getSurveyPostList(pageable));
    }

    //미처리 신고 목록
    @GetMapping("/claim/unprocessed")
    public ResponseEntity<?> claimUnprocessed(@PageableDefault(size = 20) Pageable pageable){
        return ResponseEntity.ok().body(adminClaimService.getProcessed(0, pageable));
    }

    //처리 신고 목록
    @GetMapping("/claim/processed")
    public ResponseEntity<?> claimProcessed(@PageableDefault(size = 20) Pageable pageable){
        return ResponseEntity.ok().body(adminClaimService.getProcessed(1, pageable));
    }

    //신고내역 상세 조회
    @GetMapping("/claim/{id}")
    public ResponseEntity<?> claim(@PathVariable Long id){
        ClaimDetailResponse claim = adminClaimService.getClaim(id);

        String claimType = claim.getClaimType();
        Long key = claim.getLogicalKey();

        Map<String, Object> data = new HashMap<>();
        data.put("claim", claim);

        if ("POST".equals(claim.getClaimType())) {
            data.put("child", adminClaimService.getClaimPost(key));
        } else if ("COMMENT".equals(claimType)) {
            data.put("child", adminClaimService.getClaimComment(key));
        } else if ("CHILD_COMMENT".equals(claimType)) {
            data.put("child", adminClaimService.getClaimChildComment(key));
        }

        return ResponseEntity.ok().body(data);
    }

    //신고 처리
    @GetMapping("/claim/processing/{id}")
    public ResponseEntity<?> claimProcessing(@PathVariable Long id){
        adminClaimService.claimProcessing(id);
        return ResponseEntity.ok().body("");
    }

    //신고 반려 처리
    @PatchMapping ("/claim/unprocessing")
    public ResponseEntity<?> claimUnprocessing(@RequestBody ClaimUnProcessingRequest request){
        adminClaimService.claimUnprocessing(request);
        return ResponseEntity.ok().body("");
    }

    //회원가입 통계
    @GetMapping("/signup/count")
    public ResponseEntity<?> signupCount(){
        List<AdminUserSignupCountResponse> list = adminUserService.signupCount();
        return ResponseEntity.ok().body(list);
    }






}