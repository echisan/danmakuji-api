package cc.dmji.api.enums;

public enum PostBangumiStatus {

    CANCLE("已取消"),

    PENDING("待处理"),

    SUCCESS("已采纳"),

    NEED_PERFECT("待完善"),

    FAILED("未被采纳");

    String statusName;

    PostBangumiStatus(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}
