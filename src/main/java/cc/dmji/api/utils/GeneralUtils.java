package cc.dmji.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class GeneralUtils {

    private static final Logger logger = LoggerFactory.getLogger(GeneralUtils.class);

    /**
     * 获取请求的ip地址
     *
     * @param request 请求
     * @return ip地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        logger.info("请求的ip地址: {}", ip);
        return ip;
    }

    /**
     * 获取请求域名
     *
     * @param request 请求
     * @return 域名
     */
    public static String getReferer(HttpServletRequest request) {
        return request.getHeader("Referer");
    }

    /**
     * 获取32位无分隔号小写UUID
     *
     * @return UUID
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * 过滤html标签 清除xxs
     * @param html 弹幕内容
     * @return 过滤后的内容
     */
    public static String htmlEncode(String html) {
        if (StringUtils.hasText(html)) {
            html = html.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
                    .replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;")
                    .replaceAll("'", "&#39;")
                    .replaceAll("eval\\((.*)\\)", "")
                    .replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"")
                    .replaceAll("script", "");
            return html;
        }
        return "";
    }

    /**
     * 获取今天的0点
     * @return 时间戳
     */
    public static Date getToday0Clock(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取今天晚上11点59分
     * @return 时间戳
     */
    public static Date getToday2359Clock(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        return calendar.getTime();
    }

}
