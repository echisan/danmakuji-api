package cc.dmji.api.web.model.admin;

/**
 * Created by echisan on 2018/6/9
 */
public class IndexInfo {

    // 在线人数
    private Long online;
    // 总注册人数
    private Long users;
    // 今日新增人数
    private Long new_users;

    public Long getOnline() {
        return online;
    }

    public void setOnline(Long online) {
        this.online = online;
    }

    public Long getUsers() {
        return users;
    }

    public void setUsers(Long users) {
        this.users = users;
    }

    public Long getNew_users() {
        return new_users;
    }

    public void setNew_users(Long new_users) {
        this.new_users = new_users;
    }
}
