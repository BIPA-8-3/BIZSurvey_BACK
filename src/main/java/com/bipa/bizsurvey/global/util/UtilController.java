package com.bipa.bizsurvey.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/utils")
public class UtilController {
    private final Environment env;

    @GetMapping("/profile")
    public ResponseEntity<String> getProfile() {
        String activeProfile =  Arrays.stream(env.getActiveProfiles()).findFirst().orElse("");
        return ResponseEntity.ok(activeProfile);
    }
}
