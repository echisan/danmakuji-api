package cc.dmji.api.enums;

public enum Role {

    /** 普通用户 */
    USER("ROLE_USER"),
    /** 系统管理员 */
    ADMIN("ROLE_ADMIN"),
    /** 普通管理员 */
    MANAGER("ROLE_MANAGER");

    String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
