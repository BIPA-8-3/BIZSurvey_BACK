package com.bipa.bizsurvey.domain.community.api;

import com.bipa.bizsurvey.domain.community.dto.request.childComment.CreateChildCommentRequest;
import com.bipa.bizsurvey.domain.community.dto.request.childComment.UpdateChildCommentRequest;
import com.bipa.bizsurvey.domain.community.application.ChildCommentService;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityChildCommentController {

    private final ChildCommentService childCommentService;

    // /community/{commentId}/createChildComment
    @PostMapping("/{commentId}/createChildComment")
    public ResponseEntity<?> createChildComment(@PathVariable Long commentId,
                                                @Valid @RequestBody CreateChildCommentRequest createChildCommentRequest,
                                                @AuthenticationPrincipal LoginUser loginUser
                                                ){
        childCommentService.createChildComment(loginUser.getId(), commentId, createChildCommentRequest);
        return ResponseEntity.ok().body("대댓글이 생성되었습니다.");
    }

    // /community/{commentId}/showChildComment
    @GetMapping("/{commentId}/showChildComment")
    public ResponseEntity<?> getChildCommentList(@PathVariable Long commentId){
        return ResponseEntity.ok().body(childCommentService.getChildCommentList(commentId));
    }

    @PatchMapping("/{commentId}/updateChildComment/{childCommentId}")
    public ResponseEntity<?> updateChildComment(@PathVariable Long commentId,
                                                @PathVariable Long childCommentId,
                                                @AuthenticationPrincipal LoginUser loginUser,
                                                @Valid @RequestBody UpdateChildCommentRequest updateChildCommentRequest
                                                ){
        childCommentService.updateChildComment(loginUser.getId(), commentId, childCommentId, updateChildCommentRequest);

        return ResponseEntity.ok().body("대댓글이 수정되었습니다.");
    }

    // /community/{commentId}/deleteChildComment/{childCommentId}
    @DeleteMapping("/{commentId}/deleteChildComment/{childComment}")
    public ResponseEntity<?> deleteChildComment(@PathVariable Long commentId,
                                                @PathVariable Long childComment,
                                                @AuthenticationPrincipal LoginUser loginUser
                                                ){
        childCommentService.deleteChildComment(loginUser.getId(), commentId, childComment);
        return ResponseEntity.ok().body("대댓글이 삭제되었습니다.");
    }
}
