package cc.dmji.api.enums;

public enum MessageType {

    // 回复
    COMMENT(1),
    // 子回复
    REPLY(2),
    // 赞
    LIKE(3),
    // 系统通知
    SYSTEM(4),
    // 艾特
    AT(5);

    Integer code;

    MessageType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static MessageType byCode(Integer code){
        MessageType[] values = MessageType.values();
        for (MessageType mt :
                values) {
            if (mt.code.equals(code)){
                return mt;
            }
        }
        return null;
    }
}
