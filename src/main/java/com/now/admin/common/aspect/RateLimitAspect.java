package com.now.admin.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RateLimitAspect {


//    @Around("@annotation(com.now.admin.common.annotation.RateLimit)")
//    public Object around(ProceedingJoinPoint pjp, RateLimit rateLimit) throws Throwable {
//        return null;
//    }


}
