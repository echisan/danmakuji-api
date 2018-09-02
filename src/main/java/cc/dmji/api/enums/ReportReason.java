package cc.dmji.api.enums;

/**
 * 举报的原因(类型)
 */
public enum ReportReason {

    ABUSE(1,"辱骂"),
    PORN(2,"色情"),
    AD(3,"垃圾广告"),
    LEAD_WAR(4,"引战"),
    SPOILER(5,"剧透"),
    PERSONAL_ATTACK(6,"人身攻击"),
    PRIVACY_INVASION(7,"隐私侵犯"),
    BRUSH_SCREEN(8,"刷屏"),
    BREAK_RULES(9,"违法违规"),
    VULGAR(10,"低俗"),
    GAMBLING_FRAUD(11,"赌博诈骗"),
    OTHER(12,"其他");

    Integer code;
    String description;

    ReportReason(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ReportReason byCode(Integer code){
        ReportReason[] values = ReportReason.values();
        for (ReportReason rt :
                values) {
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
