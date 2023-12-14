package com.bipa.bizsurvey.domain.admin.application;

import com.bipa.bizsurvey.domain.admin.dto.childComment.ClaimChileCommentResponse;
import com.bipa.bizsurvey.domain.admin.dto.claim.ClaimDetailResponse;
import com.bipa.bizsurvey.domain.admin.dto.claim.ClaimListResponse;
import com.bipa.bizsurvey.domain.admin.dto.claim.ClaimUnProcessingRequest;
import com.bipa.bizsurvey.domain.admin.dto.claim.ClaimUserResponse;
import com.bipa.bizsurvey.domain.admin.dto.comment.ClaimCommentResponse;
import com.bipa.bizsurvey.domain.admin.dto.post.ClaimPostResponse;
import com.bipa.bizsurvey.domain.community.application.ChildCommentService;
import com.bipa.bizsurvey.domain.community.application.CommentService;
import com.bipa.bizsurvey.domain.community.application.PostService;
import com.bipa.bizsurvey.domain.community.domain.*;
import com.bipa.bizsurvey.domain.community.exception.postException.PostException;
import com.bipa.bizsurvey.domain.community.exception.postException.PostExceptionType;
import com.bipa.bizsurvey.domain.community.repository.ChildCommentRepository;
import com.bipa.bizsurvey.domain.community.repository.CommentRepository;
import com.bipa.bizsurvey.domain.community.repository.PostRepository;
import com.bipa.bizsurvey.domain.user.domain.Claim;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.enums.ClaimType;
import com.bipa.bizsurvey.domain.user.repository.ClaimRepository;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminClaimService {
    private final ClaimRepository claimRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ChildCommentRepository childCommentRepository;
    private final UserRepository userRepository;

    private final PostService postService;
    private final CommentService commentService;
    private final ChildCommentService childCommentService;


    //처리/미처리 신고 목록
    public Page<ClaimListResponse> getProcessed(int processing, Pageable pageable){
        return claimRepository.findAllByWithUser(processing, pageable);
    }

    //신고 상세(신고 테이블)
    public ClaimDetailResponse getClaim(Long id){
        Claim data = claimRepository.findById(id).orElseThrow();
        return ClaimDetailResponse.builder()
                .id(data.getId())
                .claimType(String.valueOf(data.getClaimType()))
                .logicalKey(data.getLogicalKey())
                .claimReason(data.getClaimReason().getValue())
                .userid(data.getUser().getId())
                .nickname(data.getUser().getNickname())
                .email(data.getUser().getEmail())
                .createTime(String.valueOf(data.getRegDate()))
                .build();
    }

    //신고 상세(게시물)
    public ClaimPostResponse getClaimPost(Long id){
        Post post = postRepository.findById(id).orElseThrow(
                () -> new PostException(PostExceptionType.NON_EXIST_POST));

        return ClaimPostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .count(post.getCount())
                .nickname(post.getUser().getNickname())
                .createTime(post.getRegDate().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm")))
                .userId(post.getUser().getId())
                .email(post.getUser().getEmail())
                .build();
    }

    //신고 상세(댓글)
    public ClaimCommentResponse getClaimComment(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new PostException(PostExceptionType.NON_EXIST_POST));

        return ClaimCommentResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .nickName(comment.getUser().getNickname())
                .createTime(comment.getRegDate().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm")))
                .userId(comment.getUser().getId())
                .build();
    }

    //신고 상세(대댓글)
    public ClaimChileCommentResponse getClaimChildComment(Long id){
        ChildComment childComment = childCommentRepository.findById(id).orElseThrow(
                () -> new PostException(PostExceptionType.NON_EXIST_POST));
        return ClaimChileCommentResponse.builder()
                .childCommentId(childComment.getId())
                .content(childComment.getContent())
                .nickName(childComment.getUser().getNickname())
                .createTime(childComment.getRegDate().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm")))
                .userId(childComment.getUser().getId())
                .build();
    }

    //신고 처리 및 정지기간 등록
    public void claimProcessing(Long id) {
        Claim claim = claimRepository.findById(id).orElseThrow();
        // 신고 처리
        claim.claimProcessing();
        Claim result = claimRepository.save(claim);
        //같은 사유로 등록된 신고 갯수
        int claimCount = claimRepository.countByPenalizedAndClaimReasonAndProcessing(result.getPenalized(), result.getClaimReason(), 1);
        int plus = calculatePlus(String.valueOf(result.getClaimReason()), claimCount);
        User user = userRepository.findById(result.getPenalized()).orElseThrow();
        if(plus != 99) {
            if (user.getForbiddenDate() == null || user.getForbiddenDate().isEmpty()) {
                //현재 날짜에서 플러스
                user.forbiddenDateUpdate(nowPlusDateTime(plus));
            } else {
                //정지 기간에서 플러스
                user.forbiddenDateUpdate(plusDateTime(plus, user.getForbiddenDate()));
            }
        }else {
            user.forbiddenDateUpdate("forbidden");
        }
    }
    public void claimUnprocessing(ClaimUnProcessingRequest request){
        System.out.println("testestsetste!!!!");
        //신고 반려 처리
        Claim claim = claimRepository.findById(request.getClaimId()).orElseThrow();
        claim.claimUnProcessing();

        // 게시물을 신고했을 경우
        if(claim.getClaimType() == ClaimType.POST){
            Post post = postService.findPost(request.getPostId());
            post.setReported(false); // 신고된 게시물
            // 댓글이 신고된 경우
        }else if(claim.getClaimType() == ClaimType.COMMENT){
            Comment comment = commentService.findComment(request.getPostId());
            comment.updateReportedFalse(); // 신고된 댓글
            // 대댓글이 신고된 경우
        } else if (claim.getClaimType() == ClaimType.CHILD_COMMENT) {
            ChildComment childComment = childCommentService.findChildComment(request.getPostId());
            childComment.updateReportedFalse(); // 신고된 대댓글
        }
    }

    private int calculatePlus(String type, int size) {
        switch (type) {
            case "PROMOTION":
            case "ABUSIVE":
            case "COPYRIGHT":
                return calculatePlusForType(size, 1, 3, 7, 14, 99);
            case "DISCLOSURE":
            case "FALSE_REPORTER":
                return calculatePlusForType(size, 7, 14, 30, 60, 99);
            case "ILLEGAL_INFO":
                return 99;
            default:
                return 0;
        }
    }

    private int calculatePlusForType(int size, int... values) {
        return (size >= 1 && size <= values.length) ? values[size - 1] : values[values.length - 1];
    }

    //현재날짜에서 플러스
    private String nowPlusDateTime(int day){
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime newDateTime = currentDateTime.plusDays(day);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNewDateTime = newDateTime.format(formatter);

        return String.valueOf(formattedNewDateTime);
    }

    //기존 정지 날짜에서 플러스
    private String plusDateTime(int day, String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);

        LocalDateTime newDateTime = localDateTime.plusDays(day);

        return newDateTime.format(formatter);
    }
}























