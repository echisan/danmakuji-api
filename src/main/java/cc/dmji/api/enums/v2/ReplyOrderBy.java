package cc.dmji.api.enums.v2;

public enum ReplyOrderBy {

    create_time(1),

    like_count(2),

    floor(3);

    Integer code;

    ReplyOrderBy(Integer code) {
        this.code = code;
    }

    public static ReplyOrderBy byCode(Integer code){
        ReplyOrderBy[] values = ReplyOrderBy.values();
        for (ReplyOrderBy rob :
                values) {
            if (rob.code.equals(code)){
                return rob;
            }
        }
        return null;
    }
}
