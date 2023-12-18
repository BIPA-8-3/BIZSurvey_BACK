package com.bipa.bizsurvey.global.common;

import com.bipa.bizsurvey.domain.community.application.PostService;
import com.bipa.bizsurvey.domain.community.application.SurveyPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class CommonScheduler {


    private final RedisService redisService;
    private final PostService postService;
    private final SurveyPostService surveyPostService;

    @Scheduled(cron = "0 5 23 ? * WED")
    public void autoUpdatePostTitles() {
        List<String> postTitles = postService.findPostTitle();
        redisService.saveData("searchTitles", postTitles);
        log.info("스케줄러 동작");
    }

    @Scheduled(cron = "0 5 23 ? * WED")
    public void bestCommunityPostId(){
        redisService.saveData("bestCommunityPostId", postService.choseBestCommunityPostId());
    }

    @Scheduled(cron = "0 5 23 ? * WED")
    public void autoUpdateSurveyPostTitles(){
        List<String> postTitles = surveyPostService.findSurveyPostTitle();
        redisService.saveData("SearchSurveyTitles", postTitles);
    }

}
