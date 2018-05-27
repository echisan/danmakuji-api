package cc.dmji.api.utils;

import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Created by echisan on 2018/5/16
 */
public class DmjiUtils {

    /**
     * 验证用户名的正则
     * 用户名(昵称) 长度1-20,可允许的字符(数字,英文,下划线,中文)
     */
    private static final String USERNAME_REGEX = "[a-z0-9A-Z\\u4e00-\\u9fa5_]{1,20}";

    /**
     * 验证密码的正则
     * 密码 长度6-20
     */
    private static final String PASSWORD_REGEX = "[a-z0-9A-Z._]{6,20}";

    /**
     * 验证邮箱格式
     */
    private static final String EMAIL_REGEX = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    public static boolean validUsername(String username){
        return username.matches(USERNAME_REGEX);
    }

    public static boolean validPassword(String password){
        return password.matches(PASSWORD_REGEX);
    }

    public static boolean validEmail(String email){
        return email.matches(EMAIL_REGEX);
    }

    public static String getUUID32() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * 清除html代码
     * @param html
     * @return
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
}
