package com.now.admin.common.util;


import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.InvalidConfigException;
import org.lionsoul.ip2region.service.Ip2Region;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.XdbException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 登录用户 工具类
 * 功能：获取真实IP + 解析地址 + 解析设备/浏览器/系统
 */
public class LoginDeviceInfoUtil {

    private static Searcher v4Searcher;
    private static Searcher v6Searcher;

    private static final Ip2Region ip2Region;

    static {

        try {
            // 指定为 v4 配置

            final Config v4Config = Config.custom()
                    .setCachePolicy(Config.VIndexCache)     // 指定缓存策略:  NoCache / VIndexCache / BufferCache
                    .setSearchers(15)                       // 设置初始化的查询器数量
                    // .setCacheSliceBytes(int)             // 设置缓存的分片字节数，默认为 50MiB
                    // .setXdbInputStream(InputStream)      // 设置 v4 xdb 文件的 inputstream 对象
                    // .setXdbFile(File)                    // 设置 v4 xdb File 对象
                    // .setFairLock(boolean)                // 设置 ReentrantLock 是否使用公平锁
                    .setXdbPath("ip2region_v4.xdb")    // 设置 v4 xdb 文件的路径
                    .asV4();
            // 2, 创建 v6 的配置：指定缓存策略和 v6 的 xdb 文件路径

            final Config v6Config = Config.custom()
                    .setCachePolicy(Config.VIndexCache)     // 指定缓存策略: NoCache / VIndexCache / BufferCache
                    .setSearchers(15)                       // 设置初始化的查询器数量
                    // .setCacheSliceBytes(int)             // 设置缓存的分片字节数，默认为 50MiB
                    // .setXdbInputStream(InputStream)      // 设置 v6 xdb 文件的 inputstream 对象
                    // .setXdbFile(File)                    // 设置 v6 xdb File 对象
                    // .setFairLock(boolean)                // 设置 ReentrantLock 是否使用公平锁
                    .setXdbPath("ip2region_v6.xdb")    // 设置 v6 xdb 文件的路径
                    .asV6();    // 指定为 v6 配置
            ip2Region = Ip2Region.create(v4Config,v6Config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XdbException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigException e) {
            throw new RuntimeException(e);
        }
    }

    public static LoginInfo getLoginInfo(HttpServletRequest request) {
        LoginInfo info = new LoginInfo();
        String ip = getRealIp(request);
        info.setIp(ip);
        info.setLocation(getLocation(ip));

        // 解析设备/浏览器/OS
        UserAgent ua = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        info.setOs(ua.getOperatingSystem().getName());
        info.setBrowser(ua.getBrowser().getName());
        info.setDevice(ua.getOperatingSystem().getDeviceType().getName());
        return info;
    }

    /**
     * 获取穿透代理的真实IP（Nginx/SLB/CDN）
     */
    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            if (ip.contains(",")) ip = ip.split(",")[0].trim();
            return ip;
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) return ip;
        return request.getRemoteAddr();
    }

    /**
     * 自动识别 IPv4 / IPv6 并解析地址
     */
    public static String getLocation(String ip) {
        try {
            if (!StringUtils.hasText(ip)) return "未知IP";
            if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1") || ip.startsWith("192.168.")) {
                return "局域网/本地";
            }

            InetAddress inet = InetAddress.getByName(ip);
            String region = inet instanceof java.net.Inet4Address
                    ? v4Searcher.search(ip)
                    : v6Searcher.search(ip);

            // 格式化：中国|广东省|深圳市|电信 → 中国 广东省 深圳市 电信
            return region == null ? "未知" : region.replace("|", " ").trim();
        } catch (UnknownHostException e) {
            return "IP格式错误";
        } catch (Exception e) {
            return "解析失败";
        }
    }

    @Data
    public static class LoginInfo {
        private String ip;
        private String location;
        private String os;
        private String browser;
        private String device;
    }
}
