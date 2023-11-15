package com.bipa.bizsurvey.global.config.jwt;

public interface JwtVO {
    public static final String SECRET = "bizSurvey"; // HS256 (대칭키)
    public static final int EXPIRATION_TIME = 60 * 60; // 한시간
    public static final int REFRESH_EXPIRATION_TIME = 60 * 60 * 1000; // 한시간
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
    public static final String REFRESH_HEADER = "RefreshAuthorization";
}
