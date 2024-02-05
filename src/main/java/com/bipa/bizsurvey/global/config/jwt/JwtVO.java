package com.bipa.bizsurvey.global.config.jwt;

public interface JwtVO {
    public static final String SECRET = "bizSurvey";
    public static final int EXPIRATION_TIME = 60 * 60 * 1000; // 한시간
    public static final Long REFRESH_EXPIRATION_TIME = 3L * 24 * 60 * 60 * 1000; // 3일
//    public static final int EXPIRATION_TIME = 30 * 1000; // 1 minute
//    public static final int REFRESH_EXPIRATION_TIME = 60 * 1000; // 2 minute
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
    public static final String REFRESH_HEADER = "RefreshAuthorization";
}
