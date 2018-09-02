package cc.dmji.api.enums;

/**
 * 举报的目标类型
 */
public enum ReportTargetType {

    REPLY(1,"回复"),
    DANMAKU(2,"弹幕");

    Integer code;
    String description;

    ReportTargetType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ReportTargetType byCode(Integer code){
        ReportTargetType[] values = ReportTargetType.values();
        for (ReportTargetType rt : values) {
            if (rt.code.equals(code)){
                return rt;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
