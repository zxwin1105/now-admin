package com.now.admin.common.util;

import java.util.UUID;

public class UUIDUtil {
    /**
     * 生成标准 UUID (带横杠)
     * 例：1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成简化 UUID (去掉横杠, 32位)
     * 例：1b9d6bcdbbfd4b2d9b5dab8dfbbd4bed
     */
    public static String simpleUUID() {
        return randomUUID().replace("-", "");
    }

    /**
     * 生成短UUID（前16位），适合做redis key、临时标识
     */
    public static String shortUUID() {
        return simpleUUID().substring(0, 16);
    }

}
