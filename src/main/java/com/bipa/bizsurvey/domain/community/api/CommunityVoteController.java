package com.bipa.bizsurvey.domain.community.api;

import com.bipa.bizsurvey.domain.community.dto.request.vote.CreateVoteRequest;
import com.bipa.bizsurvey.domain.community.application.VoteService;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityVoteController {

    private final VoteService voteService;

    @PostMapping("/{postId}/createVote")
    public ResponseEntity<?> createVote(@PathVariable Long postId,
                                        @Valid @RequestBody CreateVoteRequest createVoteRequest,
                                        @AuthenticationPrincipal LoginUser loginUser){
        voteService.createVote(loginUser.getId() ,createVoteRequest, postId);
        return ResponseEntity.ok().body("투표가 생성되었습니다.");
    }

    @GetMapping("/{postId}/showVoteAnswer/{voteId}")
    public ResponseEntity<?> showVoteAnswerList(@PathVariable Long postId,
                                                @PathVariable Long voteId
                                                ){
        return ResponseEntity.ok().body(voteService.showVoteAnswerList(postId, voteId));
    }

    @GetMapping("/{postId}/{voteId}/choseAnswer/{voteAnswerId}")
    public ResponseEntity<?> choseAnswer(@AuthenticationPrincipal LoginUser loginUser,
                                         @PathVariable Long postId,
                                         @PathVariable Long voteId,
                                         @PathVariable Long voteAnswerId){

        voteService.choseAnswer(loginUser.getId(), postId, voteId, voteAnswerId);
        return ResponseEntity.ok().body("투표가 등록되었습니다.");
    }

    @GetMapping("/{voteId}/showPercentage")
    public ResponseEntity<?> choseAnswer(@PathVariable Long voteId){
        return ResponseEntity.ok().body(voteService.calculatePercentage(voteId));
    }


}
