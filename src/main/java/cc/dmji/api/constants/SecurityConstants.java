package cc.dmji.api.constants;

/**
 * Created by echisan on 2018/5/18
 */
public class SecurityConstants {

    /** 请求头 */
    public static final String TOKEN_HEADER_AUTHORIZATION = "Authorization";
    /** token前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_SECRET = "darkersecrettoken";
    public static final String TOKEN_ISSUER = "DMCollection";
    /* 默认过期时长 432000L 秒 */
    public static final Long TOKEN_EXPIRATION = 432000L;
    public static final Long TOKEN_REMEMBER_ME_EXPIRATION = 2592000L;

    /** 用户id */
    public static final String TOKEN_CLAIM_KEY_UID = "uid";
    /** 用户角色 */
    public static final String TOKEN_CLAIM_KEY_ROLE = "rol";
    /** 封禁信息 */
    public static final String TOKEN_CLAIM_KEY_LOCK = "loc";
    /** 邮箱验证信息 */
    public static final String TOKEN_CLAIM_KEY_EMAIL = "ema";
    /** 用户创建时间 */
    public static final String TOKEN_CLAIM_KEY_CREATE_TIME = "crt";

    public static final String TOKEN_RESULT_CODE_HEADER = "Result-Code";

}
