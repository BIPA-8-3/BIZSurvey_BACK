package com.bipa.bizsurvey.domain.user.api;

import com.bipa.bizsurvey.domain.user.application.ClaimService;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.user.dto.claim.ClaimReasonResponse;
import com.bipa.bizsurvey.domain.user.dto.claim.ClaimRequest;
import com.bipa.bizsurvey.domain.user.enums.ClaimReason;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/claim")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;


    @GetMapping("/list")
    public ResponseEntity<?> getClaimList(){
        List<ClaimReasonResponse> stringClaimList = new ArrayList<>();
        for(ClaimReason claimReason : ClaimReason.values()){
            ClaimReasonResponse claimReasonResponse = ClaimReasonResponse.builder()
                    .claimReason(claimReason.getValue())
                    .build();
            stringClaimList.add(claimReasonResponse);
       }
        return ResponseEntity.ok().body(stringClaimList);
    }

     //어떤 유저가 어떤 게시물/댓글/대댓글 을 신고했는지 이유는 무엇인지
    @PostMapping("/accept")
    public ResponseEntity<?> acceptClaim(@RequestBody ClaimRequest claimRequest,
                                         @AuthenticationPrincipal LoginUser loginUser
                                         ){
        claimService.createClaim(claimRequest, loginUser.getId());
        return ResponseEntity.ok().body("신고가 접수되었습니다.");
    }






}
