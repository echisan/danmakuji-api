package cc.dmji.api.enums.v2;

import cc.dmji.api.enums.Role;

/**
 * 系统通知对象类型
 */
public enum SysMsgTargetType {

    // 全站用户
    ALL(0),
    // 管理员
    MANAGER(1),
    // 系统管理员
    ADMIN(2);

    private Integer code;

    SysMsgTargetType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static SysMsgTargetType byCode(Integer code){
        SysMsgTargetType[] values = SysMsgTargetType.values();
        for (SysMsgTargetType smt : values) {
            if (smt.code.equals(code)){
                return smt;
            }
        }
        return null;
    }

    public static SysMsgTargetType byUserRole(Role role){
        switch (role){
            case MANAGER:return SysMsgTargetType.MANAGER;
            case ADMIN:return SysMsgTargetType.ADMIN;
            case USER:return SysMsgTargetType.ALL;
            default:return SysMsgTargetType.ALL;
        }
    }
}
