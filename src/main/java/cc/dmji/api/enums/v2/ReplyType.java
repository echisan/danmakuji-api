package cc.dmji.api.enums.v2;

public enum ReplyType {

    /**
     * 番剧某一集下的评论
     */
    BANGUMI_EPISODE(1),

    /**
     * 公告下的评论
     */
    NOTICE(2);

    Integer code;

    ReplyType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static ReplyType byCode(Integer code){
        ReplyType[] replyTypes = ReplyType.values();
        for (ReplyType rt : replyTypes) {
            if (rt.code.equals(code)){
                return rt;
            }
        }
        return null;
    }
}
