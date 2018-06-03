package cc.dmji.api.constants;

/**
 * Created by echisan on 2018/5/21
 */
public class RedisKey {

    /** 验证邮箱时存放uuid */
    public static final String VERIFY_EMAIL_KEY = "verify_email_uid_";

    /** 存放token */
    public static final String LOGIN_TOKEN_KEY = "login_token";

    /** IP提交频繁 */
    public static final String POST_FREQUENT_IP_KEY = "POST_FREQUENT_IP_";

    /** 弹幕缓存 */
    public static final String DANMAKU_KEY = "danmaku_id_";
}
