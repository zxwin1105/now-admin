package com.now.admin.common.annotation;

public @interface RateLimit {
    String keyPrefix() default "-";
    // 时间窗口（秒）
    int seconds() default 60;
    // 最大请求数
    int maxCount() default 100;
    // 限制维度：userId / ip
    LimitType type() default LimitType.USER_ID;

    enum LimitType {
        USER_ID, IP, GLOBAL
    }

}

