package cc.dmji.api.enums;

/**
 * Created by echisan on 2018/7/13
 */
public enum  PostBangumiOrderBy {

    createTime("create_time",0),
    modifyTime("modify_time",1);

    String colum;
    Integer code;

    PostBangumiOrderBy(String colum, Integer code) {
        this.colum = colum;
        this.code = code;
    }

    public String getColum() {
        return colum;
    }

    public Integer getCode() {
        return code;
    }

    public static PostBangumiOrderBy byCode(Integer code){
        PostBangumiOrderBy[] values = PostBangumiOrderBy.values();
        for (PostBangumiOrderBy pbob :
                values) {
            if (pbob.code.equals(code)){
                return pbob;
            }
        }
        return null;
    }
}
