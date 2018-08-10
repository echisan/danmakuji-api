package cc.dmji.api.common;

/**
 * 该类为消息状态码的表示
 * 状态码和消息一一对应,
 * 如0对应的消息为"OK"，6001对应的消息为"无访问权限"
 */
public enum  ResultCode {

    /*成功状态码*/
    SUCCESS(0,"OK"),

    /*参数错误1001-1999*/
    PARAM_IS_INVALID(1001,"参数无效"),
    PARAM_IS_BLANK(1002,"参数为空"),
    PARAM_TYPE_ERROR(1003,"参数类型错误"),
    PARAM_NOT_CPMPLETE(1004,"参数缺失"),

    /*用户错误2001-2999*/
    USER_NOT_LOGINED(2001,"用户未登陆"),
    USER_LOGIN_ERROR(2002,"账号不存在或密码错误"),
    USER_ACCOUNT_FORBIDDEN(2003,"账号已被禁用"),
    USER_NOT_EXIST(2004,"用户不存在"),
    USER_ALREADY_EXIST(2005,"用户已存在"),
    USER_EXPIRATION(2006,"帐号有效期已过，请重新登陆"),

    /*业务错误3001-3999*/
    SPECIFIED_QUESTION_USER_NOT_EXIST(3001,"某业务出现问题"),

    /*系统错误4001-4999*/
    SYSTEM_INTERNAL_ERROR(4001,"服务器内部错误"),

    /*数据错误5001-5999*/
    RESULT_DATA_NOT_FOUND(5001,"数据未找到"),
    DATA_IS_WRONG(5002,"数据有误"),
    DATA_ALREADY_EXIST(5003,"数据已存在"),
    DATABASE_NOT_CONNECTED(5004,"无法连接到数据库"),
    DATA_EXPIRATION(5005,"数据已过期"),
    DATA_ALREADY_EXIST_BUT_ALLOW_REQUEST(5006,"数据已存在"),

    /*权限错误6001-6999*/
    PERMISSION_DENY(6001,"无访问权限");


    /*提示消息*/
    String msg;

    /*状态码*/
    Integer code;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static String getMsg(Integer code){
        for (ResultCode rc : ResultCode.values()){
            if (rc.getCode().equals(code)){
                return rc.getMsg();
            }
        }
        return null;
    }

    public static ResultCode byCode(Integer code){
        for (ResultCode rc : ResultCode.values()){
            if (rc.getCode().equals(code)){
                return rc;
            }
        }
        return null;
    }
}
