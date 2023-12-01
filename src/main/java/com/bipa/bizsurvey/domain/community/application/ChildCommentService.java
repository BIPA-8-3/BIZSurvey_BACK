package com.bipa.bizsurvey.domain.community.application;

import com.bipa.bizsurvey.domain.community.domain.ChildComment;
import com.bipa.bizsurvey.domain.community.domain.Comment;
import com.bipa.bizsurvey.domain.community.domain.QChildComment;
import com.bipa.bizsurvey.domain.community.dto.request.childComment.CreateChildCommentRequest;
import com.bipa.bizsurvey.domain.community.dto.request.childComment.UpdateChildCommentRequest;
import com.bipa.bizsurvey.domain.community.dto.response.childComment.ChildCommentResponse;
import com.bipa.bizsurvey.domain.community.exception.childCommentException.ChildCommentException;
import com.bipa.bizsurvey.domain.community.exception.childCommentException.ChildCommentExceptionType;
import com.bipa.bizsurvey.domain.community.exception.commentException.CommentException;
import com.bipa.bizsurvey.domain.community.exception.commentException.CommentExceptionType;
import com.bipa.bizsurvey.domain.community.repository.ChildCommentRepository;
import com.bipa.bizsurvey.domain.community.repository.CommentRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@Transactional
@RequiredArgsConstructor
public class ChildCommentService {


    private final ChildCommentRepository childCommentRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final JPAQueryFactory jpaQueryFactory;


    public void createChildComment(Long userId, Long commentId, CreateChildCommentRequest createChildCommentRequest){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserExceptionType.NON_EXIST_USER));
        Comment comment = findComment(commentId);

        ChildComment childComment = ChildComment.builder()
                .user(user)
                .comment(comment)
                .content(createChildCommentRequest.getContent())
                .build();

        childCommentRepository.save(childComment);
    }

    public List<ChildCommentResponse> getChildCommentList(Long commentId){
        Comment comment = findComment(commentId);
        checkAvailable(comment);

        QChildComment cc = new QChildComment("cc");

        List<ChildComment> childCommentList = jpaQueryFactory
                .select(cc)
                .from(cc)
                .where(cc.comment.eq(comment))
                .where(cc.delFlag.eq(false))
                .orderBy(cc.regDate.desc())
                .fetch();

        List<ChildCommentResponse> commentResponseList = new ArrayList<>();

        for(ChildComment childComment : childCommentList){
            ChildCommentResponse commentResponse = ChildCommentResponse.builder()
                    .childCommentId(childComment.getId())
                    .content(checkContent(childComment))
                    .nickName(childComment.getUser().getNickname())
                    .createTime(childComment.getRegDate().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm")))
                    .build();
            commentResponseList.add(commentResponse);
        }

        return commentResponseList;
    }


    // update
    public void updateChildComment(Long userId, Long commentId, Long childCommentId,
                                   UpdateChildCommentRequest updateChildCommentRequest){
        Comment comment = findComment(commentId);
        checkAvailable(comment);
        ChildComment childComment = findChildComment(childCommentId);
        checkPermission(userId, childComment);
        childComment.updateChildComment(updateChildCommentRequest);
        childCommentRepository.save(childComment);
    }


    // delete
    public void deleteChildComment(Long userId, Long commentId, Long childCommentId){
        Comment comment = findComment(commentId);
        checkAvailable(comment);
        ChildComment childComment = findChildComment(childCommentId);
        checkPermission(userId, childComment);
        childComment.updateDelFlag();
        childCommentRepository.save(childComment);
    }

    public ChildComment findChildComment(Long childCommentId){
        return childCommentRepository.findById(childCommentId).orElseThrow(
                () -> new ChildCommentException(ChildCommentExceptionType.NON_EXIST_CHILD_COMMENT)
        );
    }

    public void checkPermission(Long userId, ChildComment childComment){
        if(!Objects.equals(userId, childComment.getUser().getId())){
            throw new UserException(UserExceptionType.NO_PERMISSION);
        }
    }



    public Comment findComment(Long commentId){
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CommentException(CommentExceptionType.NON_EXIST_COMMENT)
        );
    }

    public void checkAvailable(Comment comment){
        if(comment.getDelFlag()){
            throw new CommentException(CommentExceptionType.ALREADY_DELETED);
        }
    }


    private String checkContent(ChildComment childComment){
        if(childComment.getReported()){
            return "신고된 댓글입니다.";
        }
        return childComment.getContent();
    }
}
