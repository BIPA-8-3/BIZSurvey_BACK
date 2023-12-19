package com.bipa.bizsurvey.domain.community.api;

import com.bipa.bizsurvey.domain.community.application.CommentService;
import com.bipa.bizsurvey.domain.community.application.PostService;
import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.community.dto.request.post.SearchPostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.surveyPost.CreateSurveyPostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.surveyPost.UpdateSurveyPostRequest;
import com.bipa.bizsurvey.domain.community.application.SurveyPostService;
import com.bipa.bizsurvey.domain.community.dto.response.post.PostTitleResponse;
import com.bipa.bizsurvey.domain.community.dto.response.surveyPost.SurveyPostCardResponse;
import com.bipa.bizsurvey.domain.community.dto.response.surveyPost.SurveyPostResponse;
import com.bipa.bizsurvey.domain.community.exception.postException.PostException;
import com.bipa.bizsurvey.domain.community.exception.postException.PostExceptionType;
import com.bipa.bizsurvey.domain.survey.application.SurveyCommunityService;
import com.bipa.bizsurvey.domain.survey.application.SurveyService;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.global.common.CustomPageImpl;
import com.bipa.bizsurvey.global.common.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/s-community")
public class SurveyCommunityPostController {
    //

    private final SurveyPostService surveyPostService;
    private final PostService postService;
    private final CommentService commentService;
    private final SurveyCommunityService surveyCommunityService;
    private final RedisService redisService;


    @PostMapping("/createPost")
    public ResponseEntity<?> createSurveyPost(@AuthenticationPrincipal LoginUser loginUser,
                                              @Valid @RequestBody CreateSurveyPostRequest createSurveyPostRequest
    ) {
        return ResponseEntity.ok().body(surveyPostService.createSurveyPost(loginUser.getId(), createSurveyPostRequest));
    }

    // 전체 조회
    // TODO : 참여 가능 여부 캐싱된 데이터가 아니겠끔
    // TODO : 댓글 갯수 캐싱된 데이터가 아니겠끔
    @GetMapping("")
    public ResponseEntity<?> getSurveyPostList(@PageableDefault(size = 8) Pageable pageable) {

        CustomPageImpl<?> surveyPostPage = surveyPostService.getSurveyPostList(pageable);
        List<?> content = surveyPostPage.getContent();

        for(Object o : content){
            SurveyPostCardResponse surveyPostCardResponse = (SurveyPostCardResponse) o;
            System.out.println("컨트롤러 객체 : "+surveyPostCardResponse.toString());

            surveyPostCardResponse.setCount(postService.getPostCount(surveyPostCardResponse.getPostId()));
            surveyPostCardResponse.setCommentSize(commentService.getCommentList(surveyPostCardResponse.getPostId()).size());
            surveyPostCardResponse.setParticipateCount(surveyCommunityService.getParticipants(surveyPostCardResponse.getSurveyPostId()));
            surveyPostCardResponse.setCanAccess(surveyPostService.checkAccess(surveyPostCardResponse.getSurveyPostId()));
            surveyPostCardResponse.setProfile(surveyPostService.findSurveyPost(surveyPostCardResponse.getSurveyPostId())
                    .getPost()
                    .getUser()
                    .getProfile());
        }

        return ResponseEntity.ok().body(surveyPostPage);
    }

    ///s-community/showSPost/{post_id}
    @GetMapping("/showPost/{postId}")
    public ResponseEntity<?> showSurveyPost(@PathVariable Long postId) {
        return ResponseEntity.ok().body(surveyPostService.getSurveyPost(postId));
    }

    // 검색
    @GetMapping("/search")
    public ResponseEntity<?> searchSurveyPost(@RequestParam String keyword,
                                              @PageableDefault(size = 8) Pageable pageable) {
        log.info("키워드 : " + keyword);
        return ResponseEntity.ok().body(surveyPostService.searchSurveyPost(keyword, pageable));
    }

    @PatchMapping("/updateSurveyPost/{postId}")
    public ResponseEntity<?> updateSurveyPost(@AuthenticationPrincipal LoginUser loginUser,
                                              @PathVariable Long postId,
                                              @Valid @RequestBody UpdateSurveyPostRequest updateSurveyPostRequest
    ) {
        surveyPostService.updateSurveyPost(loginUser.getId(), postId, updateSurveyPostRequest);
        return ResponseEntity.ok().body("게시물이 수정되었습니다.");
    }

    @DeleteMapping("/deleteSurveyPost/{postId}")
    public ResponseEntity<?> deleteSurveyPost(@AuthenticationPrincipal LoginUser loginUser,
                                              @PathVariable Long postId){
        surveyPostService.deleteSurveyPost(loginUser.getId(), postId);
        return ResponseEntity.ok().body("설문 게시물이 삭제되었습니다.");
    }


    @PostMapping("/findSurveyPostTitle")
    public ResponseEntity<?> findPostTitle(@RequestBody SearchPostRequest searchPostRequest) {

        if (searchPostRequest.getKeyword().equals("") || searchPostRequest.getKeyword().equals(" ")) {
            return ResponseEntity.ok().body("");
        }


        List<String> titles = redisService.getData("SearchSurveyTitles", ArrayList.class)
                .orElseThrow(() -> new PostException(PostExceptionType.NO_RESULT));
        if (titles.size() == 0) {
            return ResponseEntity.ok().body("");
        }


        List<PostTitleResponse> answerList = new ArrayList<>();
        for (String title : titles) {
            if (title != null && title.contains(searchPostRequest.getKeyword())) {
                if (answerList.size() >= 10) {
                    break;
                }
                answerList.add(PostTitleResponse.builder()
                        .result(title)
                        .build()
                );
            }
        }

        log.info("findSurveyPostTitle 동작 동작 동작 동작");

        return ResponseEntity.ok().body(answerList);
    }







}