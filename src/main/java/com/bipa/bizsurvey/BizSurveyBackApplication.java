package com.bipa.bizsurvey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BizSurveyBackApplication {
	public static void main(String[] args) {
		SpringApplication.run(BizSurveyBackApplication.class, args);
	}
}
