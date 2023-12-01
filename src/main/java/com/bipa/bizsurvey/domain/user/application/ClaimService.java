package com.bipa.bizsurvey.domain.user.application;

import com.bipa.bizsurvey.domain.community.domain.ChildComment;
import com.bipa.bizsurvey.domain.community.domain.Comment;
import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.community.application.ChildCommentService;
import com.bipa.bizsurvey.domain.community.application.CommentService;
import com.bipa.bizsurvey.domain.community.application.PostService;
import com.bipa.bizsurvey.domain.user.domain.Claim;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.dto.claim.ClaimRequest;
import com.bipa.bizsurvey.domain.user.enums.ClaimReason;
import com.bipa.bizsurvey.domain.user.enums.ClaimType;
import com.bipa.bizsurvey.domain.user.repository.ClaimRepository;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;

    private final PostService postService;
    private final CommentService commentService;
    private final ChildCommentService childCommentService;


    // 댓글과 대댓글이 신고된 경우에는 "신고된 댓글입니다로 치환"
    // Post의 경우 아예 띄워주지 않기로

    // TODO : 원본 데이터 유지
    public void createClaim(ClaimRequest claimRequest, Long userId){

        User user = userRepository.findById(userId).get();

        // 게시물을 신고했을 경우
        if(claimRequest.getClaimType() == ClaimType.POST){
            Post post = postService.findPost(claimRequest.getId());
            post.setReported(true); // 신고된 게시물
            saveClaim(user, claimRequest.getId(), claimRequest.getClaimType(), claimRequest.getClaimReason());


        // 댓글이 신고된 경우
        }else if(claimRequest.getClaimType() == ClaimType.COMMENT){
            Comment comment = commentService.findComment(claimRequest.getId());
            comment.updateReported(); // 신고된 댓글
            saveClaim(user, claimRequest.getId(), claimRequest.getClaimType(), claimRequest.getClaimReason());


        // 대댓글이 신고된 경우
        } else if (claimRequest.getClaimType() == ClaimType.CHILD_COMMENT) {
            ChildComment childComment = childCommentService.findChildComment(claimRequest.getId());
            childComment.updateReported(); // 신고된 대댓글
            saveClaim(user, claimRequest.getId(), claimRequest.getClaimType(), claimRequest.getClaimReason());
        }
    }





    private void saveClaim(User user, Long id, ClaimType claimType, ClaimReason claimReason){
        Claim claim = Claim.builder()
                .user(user)
                .claimType(claimType)
                .claimReason(claimReason)
                .logicalKey(id)
                .build();
        claimRepository.save(claim);
    }






}
