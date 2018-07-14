package cc.dmji.api.enums;

/**
 * Created by echisan on 2018/7/13
 */
public enum  PostBangumiOrderBy {

    createTime("create_time"),
    modifyTime("modify_time");

    String colum;

    PostBangumiOrderBy(String colum) {
        this.colum = colum;
    }

    public String getColum() {
        return colum;
    }
}
