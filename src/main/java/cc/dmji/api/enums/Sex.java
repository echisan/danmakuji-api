package cc.dmji.api.enums;

/**
 * Created by echisan on 2018/5/22
 */
public enum  Sex {

    MALE("男"),
    FEMALE("女"),
    OTHER("不明");

    String value;

    Sex(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
