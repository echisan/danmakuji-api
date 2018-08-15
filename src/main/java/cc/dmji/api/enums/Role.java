package cc.dmji.api.enums;

public enum Role {

    /** 普通用户 */
    USER("ROLE_USER"),
    /** 系统管理员 */
    ADMIN("ROLE_ADMIN"),
    /** 普通管理员 */
    MANAGER("ROLE_MANAGER"),
    /** 特殊的游客 */
    VISITOR("ROLE_VISITOR");

    String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Role byRoleName(String roleName){
        Role[] values = Role.values();
        for (Role role:values) {
            if (role.name.equals(roleName)){
                return role;
            }
        }
        return null;
    }
}
