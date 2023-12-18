package com.bipa.bizsurvey.domain.community.api;

import com.bipa.bizsurvey.domain.community.dto.request.comment.CreateCommentRequest;
import com.bipa.bizsurvey.domain.community.dto.request.comment.UpdateCommentRequest;
import com.bipa.bizsurvey.domain.community.application.CommentService;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityCommentController {

    private final CommentService commentService;


    // /community/{postId}/createComment
    @PostMapping("/{postId}/createComment")
    public ResponseEntity<?> createComment(@PathVariable Long postId,
                                           @Valid @RequestBody CreateCommentRequest createCommentRequest,
                                           @AuthenticationPrincipal LoginUser loginUser
                                           ){

        commentService.createComment(1L, postId, createCommentRequest);
        return ResponseEntity.ok().body("댓글이 생성되었습니다.");
    }

    // 댓글 전체 조회 -> postService

    // 댓글 수정
    // /community/{postId}/updateComment/{commentId}
    @PatchMapping("/{postId}/updateComment/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long postId,
                                           @PathVariable Long commentId,
                                           @AuthenticationPrincipal LoginUser loginUser,
                                           @Valid @RequestBody UpdateCommentRequest updateCommentRequest
                                           ){
        commentService.updateComment(loginUser.getId(), postId, commentId, updateCommentRequest);
        return ResponseEntity.ok().body("댓글이 변경되었습니다.");
    }

    // 댓글 삭제
    // /community/{postId}/deleteComment/{commentId}
    @DeleteMapping("/{postId}/deleteComment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long postId,
                                           @PathVariable Long commentId,
                                           @AuthenticationPrincipal LoginUser loginUser
                                           ){
        commentService.deleteComment(loginUser.getId(), postId, commentId);
        return ResponseEntity.ok().body("댓글이 삭제되었습니다.");
    }



}
