package cc.dmji.api.enums;

public enum Status {

    // 正常
    NORMAL(0),

    // 删除
    DELETE(1);

    private Integer code;

    Status(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static Status byCode(Integer code){
        Status[] values = Status.values();
        for (Status s : values) {
            if (s.code.equals(code)){
                return s;
            }
        }
        return null;
    }
}
