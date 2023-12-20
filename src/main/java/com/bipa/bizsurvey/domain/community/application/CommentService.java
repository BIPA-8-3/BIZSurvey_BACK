package com.bipa.bizsurvey.domain.community.application;

import com.bipa.bizsurvey.domain.community.domain.Comment;
import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.community.domain.QComment;
import com.bipa.bizsurvey.domain.community.dto.request.comment.CreateCommentRequest;
import com.bipa.bizsurvey.domain.community.dto.request.comment.UpdateCommentRequest;
import com.bipa.bizsurvey.domain.community.dto.response.comment.CommentResponse;
import com.bipa.bizsurvey.domain.community.exception.commentException.CommentException;
import com.bipa.bizsurvey.domain.community.exception.commentException.CommentExceptionType;
import com.bipa.bizsurvey.domain.community.exception.postException.PostException;
import com.bipa.bizsurvey.domain.community.exception.postException.PostExceptionType;
import com.bipa.bizsurvey.domain.community.repository.CommentRepository;
import com.bipa.bizsurvey.domain.community.repository.PostRepository;
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
public class CommentService {
    //
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final ChildCommentService childCommentService;



    // 댓글 생성
    public void createComment(Long userId, Long postId, CreateCommentRequest createCommentRequest){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserExceptionType.NON_EXIST_USER));
        Post post = findPost(postId);
        checkAvailable(post);

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content(createCommentRequest.getContent())
                .build();
        commentRepository.save(comment);
    }

    //게시물의 댓글 전체보기
    public List<CommentResponse> getCommentList(Long postId){

        Post post = findPost(postId);

        QComment c = new QComment("c");
        List<Comment> commentList = jpaQueryFactory
                .select(c)
                .from(c)
                .where(c.post.eq(post))
                .where(c.delFlag.eq(false))
                .orderBy(c.regDate.desc())
                .fetch();

        List<CommentResponse> commentResponseList = new ArrayList<>();
        for(Comment comment : commentList){
            CommentResponse commentResponse = CommentResponse.builder()
                    .commentId(comment.getId())
                    .content(checkContent(comment))
                    .nickName(comment.getUser().getNickname())
                    .createTime(comment.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    .childCommentResponses(childCommentService.getChildCommentList(comment.getId()))
                    .thumbImageUrl(comment.getUser().getProfile())
                    .build();

            commentResponseList.add(commentResponse);
        }

        return commentResponseList;
    }

    // 게시물의 댓글 수정
    public void updateComment(Long userId, Long postId, Long commentId,
                              UpdateCommentRequest updateCommentRequest){
        Post post = findPost(postId);
        checkAvailable(post);
        Comment comment = findComment(commentId);
        checkPermission(userId, comment);
        comment.updateContent(updateCommentRequest);
        commentRepository.save(comment);
    }

    // 게시물 댓글 삭제
    public void deleteComment(Long userId, Long postId, Long commentId){
        Post post = findPost(postId);
        checkAvailable(post);
        Comment comment = findComment(commentId);
        checkPermission(userId, comment);
        comment.updateDelFlag();
        commentRepository.save(comment);
    }




    
    public Comment findComment(Long commentId){
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CommentException(CommentExceptionType.NON_EXIST_COMMENT)
        );
    }

    public void checkPermission(Long userId, Comment comment){
        if(!Objects.equals(userId, comment.getUser().getId())){
            throw new UserException(UserExceptionType.NO_PERMISSION);
        }
    }

    public Post findPost(Long postId){
        return postRepository.findById(postId).orElseThrow(
                () -> new PostException(PostExceptionType.NON_EXIST_POST)
        );
    }

    public void checkAvailable(Post post){
        if(post.getDelFlag()){
            throw new PostException(PostExceptionType.ALREADY_DELETED);
        }
    }

    private String checkContent(Comment comment){
        if(comment.getReported()){
            return "신고된 댓글입니다.";
        }
        return comment.getContent();
    }





}
