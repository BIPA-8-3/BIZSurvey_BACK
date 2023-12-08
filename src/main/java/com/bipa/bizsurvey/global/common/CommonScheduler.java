package com.bipa.bizsurvey.global.common;

import com.bipa.bizsurvey.domain.community.application.PostService;
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

    @Scheduled(cron = "0 0 18 ? * THU")
    public void autoUpdate() {
        List<String> postTitles = postService.findPostTitle();
        redisService.saveData("searchTitles", postTitles);
        log.info("스케줄러 동작");
    }



}
