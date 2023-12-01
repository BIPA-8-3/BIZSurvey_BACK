package com.bipa.bizsurvey.global.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut(" execution(* com.bipa.bizsurvey.domain.community.application..*.*(..)) "  +
            "|| execution(* com.bipa.bizsurvey.domain.survey.application..*.*(..))" +
            "|| execution(* com.bipa.bizsurvey.domain.user.application..*.*(..))"   +
            "|| execution(* com.bipa.bizsurvey.domain.workspace.application..*.*(..))"

    )
    private void cut(){}

    @Before("cut()")
    public void beforeParameterLog(JoinPoint joinPoint){
        Method method = getMethod(joinPoint);
        log.info("=====method name = {} =====", method.getName());

        Object[] args = joinPoint.getArgs();
        if(args.length == 0)
            log.info("no parameter");
        for (Object arg : args){
            log.info("parameter type = {}", arg.getClass().getSimpleName());
            log.info("parameter value = {}", arg);
        }

    }

    @AfterReturning(value = "cut()", returning = "returnObj")
    public void afterReturnLog(JoinPoint joinPoint, Object returnObj){
        // 메서드 정보 받아오기
        Method method = getMethod(joinPoint);
        log.info("=====method name = {} =====", method.getName());

        if (returnObj != null) {
            log.info("return type = {}", returnObj.getClass().getSimpleName());
            log.info("return value = {}", returnObj);
        } else {
            log.info("return value is null");
        }
    }

    // joinPoint 로 메서드 정보 가져오기
    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}
