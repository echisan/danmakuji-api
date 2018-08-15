package cc.dmji.api.utils;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * 匹配 @用户
     */
    private static final String AT_REGEX = "@([a-z0-9A-Z\\u4e00-\\u9fa5_]+)\\s";

    public static boolean validUsername(String username) {
        return username.matches(USERNAME_REGEX);
    }

    public static boolean validPassword(String password) {
        return password.matches(PASSWORD_REGEX);
    }

    public static boolean validEmail(String email) {
        return email.matches(EMAIL_REGEX);
    }

    public static String getUUID32() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * 清除html代码
     *
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

    public static String commentHtmlEncode(String html) {
        if (StringUtils.hasText(html)) {
            html = html
                    .replaceAll("eval\\((.*)\\)", "")
                    .replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"")
                    .replaceAll("script", "");
            return html;
        }
        return "";
    }

    public static int validatePageParam(Integer pageNum, Integer pageSize) {
        if (null == pageNum) {
            return 1;//1表示pageNum为空
        } else {
            if (pageNum < 1) {
                return 2;//2表示pageNum不合法（小于1)
            } else {
                if (pageSize == null) {
                    return 3;//3表示pageNum不为空，pageSize为空
                } else {
                    if (pageSize < 1) {
                        return 4;//4表示pageSize不合法（小于1）
                    } else {
                        return 5;//5表示pageNum和pageSize均为有效参数
                    }
                }
            }
        }
    }

    public static boolean validPageParam(Integer pn, Integer ps) {
        return validatePageParam(pn, ps) == 5;
    }


    /**
     * 查找出该段字符串中是否存在 "@用户名 "格式的
     *
     * @param text 需要查找的字符串
     * @return 用户名列表, 或者是一个size=0的列表
     */
    public static List<String> findAtUsername(String text) {
        List<String> usernameList = new ArrayList<>();
        Pattern pattern = Pattern.compile(AT_REGEX);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String group = matcher.group();
            String username = group.substring(1, group.length() - 1);
            usernameList.add(username);
        }
        return usernameList;
    }

    /**
     * 将需要限制长度的回复限制在75个字符内
     *
     * @param content
     * @return
     */
    public static String formatReplyContent(String content) {
        if (content.length() < 75) {
            return content;
        }
        return content.substring(0, 75);
    }

    /**
     * 判断字符串是不是正整数
     *
     * @param string
     * @return
     */
    public static boolean isPositiveNumber(String string) {

        if (!StringUtils.hasText(string))
            return false;
        String regEx1 = "\\d+";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        return m.matches();
    }

    /**
     * 获取6位数的验证码
     *
     * @return 6位数的验证码，字符串格式
     */
    public static String generateVerifyCode() {
        String chars = "0123456789";
        char[] rands = new char[6];
        for (int i = 0; i < rands.length; i++) {
            int rand = (int) (Math.random() * 10);
            rands[i] = chars.charAt(rand);
        }
        return String.valueOf(rands);
    }
}
