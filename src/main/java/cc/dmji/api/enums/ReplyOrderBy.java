package cc.dmji.api.enums;

public enum ReplyOrderBy {

    CREATE_TIME("create_time"),
    LIKE("r_like");

    String columnName;

    ReplyOrderBy(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
