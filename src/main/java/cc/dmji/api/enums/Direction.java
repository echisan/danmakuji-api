package cc.dmji.api.enums;

public enum Direction {
    ASC(0),
    DESC(1);

    Integer code;

    Direction(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static Direction byCode(Integer code){
        Direction[] values = Direction.values();
        for (Direction d:values) {
            if (d.code.equals(code)){
                return d;
            }
        }
        return null;
    }
}
