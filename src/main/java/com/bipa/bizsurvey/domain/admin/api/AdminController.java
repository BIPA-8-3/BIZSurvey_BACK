package com.bipa.bizsurvey.domain.admin.api;

import com.bipa.bizsurvey.domain.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final PostService postService;

    //게시물 전체 조회
    @GetMapping("/community")
    public ResponseEntity<?> getPostList(@PageableDefault(size = 10) Pageable pageable,
                                         @RequestParam(required = false) String fieldName
    ){

        return ResponseEntity.ok().body(postService.getPostList(pageable, fieldName)); // 200 OK
    }
}
