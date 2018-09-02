package cc.dmji.api.enums;

public enum ReportHandleStatus {

    PENDING(1, "待处理"),
    HANDLED_REJECT(2, "已处理:否决"),
    HANDLED_ACCEPT(3,"已处理:采纳");

    Integer code;
    String description;

    ReportHandleStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ReportHandleStatus byCode(Integer code) {
        ReportHandleStatus[] values = ReportHandleStatus.values();
        for (ReportHandleStatus rhs : values) {
            if (rhs.code.equals(code)) {
                return rhs;
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
