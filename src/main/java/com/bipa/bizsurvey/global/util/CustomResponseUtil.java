package com.bipa.bizsurvey.global.util;

import com.bipa.bizsurvey.global.exception.ExceptionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CustomResponseUtil {
    public static void success(HttpServletResponse response, String msg) {
        try {
            ObjectMapper om = new ObjectMapper();
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(200);
        } catch (Exception e) {
            log.error("서버 파싱 에러");
        }
    }

    public static void fail(HttpServletResponse response, String msg) {
        try {
            ObjectMapper om = new ObjectMapper();
            ExceptionDto responseDto = new ExceptionDto(402, HttpStatus.UNAUTHORIZED, msg);
            String responseBody = om.writeValueAsString(responseDto);
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(402);
            response.getWriter().println(responseBody);
        } catch (Exception e) {
            log.error("서버 파싱 에러");
        }
    }

    public static void noLogin(HttpServletResponse response, String msg) {
        try {
            ObjectMapper om = new ObjectMapper();
            ExceptionDto responseDto = new ExceptionDto(401, HttpStatus.UNAUTHORIZED, msg);
            String responseBody = om.writeValueAsString(responseDto);
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(401);
            response.getWriter().println(responseBody);
        } catch (Exception e) {
            log.error("서버 파싱 에러");
        }
    }

    public static void porbiden(HttpServletResponse response, String msg) {
        try {
            ObjectMapper om = new ObjectMapper();
            ExceptionDto responseDto = new ExceptionDto(403, HttpStatus.FORBIDDEN, msg);
            String responseBody = om.writeValueAsString(responseDto);
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(403);
            response.getWriter().println(responseBody);
        } catch (Exception e) {
            log.error("서버 파싱 에러");
        }
    }


}