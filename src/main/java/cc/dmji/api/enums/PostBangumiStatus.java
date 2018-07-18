package cc.dmji.api.enums;

public enum PostBangumiStatus {

    CANCLE("已取消",0),

    PENDING("待处理",1),

    SUCCESS("已采纳",2),

    NEED_PERFECT("待完善",3),

    FAILED("未被采纳",4);

    String statusName;
    Integer code;

    PostBangumiStatus(String statusName, Integer code) {
        this.statusName = statusName;
        this.code = code;
    }

    public String getStatusName() {
        return statusName;
    }

    public Integer getCode() {
        return code;
    }

    public static PostBangumiStatus byCode(Integer code){
        PostBangumiStatus[] values = PostBangumiStatus.values();
        for (PostBangumiStatus pbs :
                values) {
            if (pbs.code.equals(code)){
                return pbs;
            }
        }
        return null;
    }
}
