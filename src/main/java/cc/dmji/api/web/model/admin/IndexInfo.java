package cc.dmji.api.web.model.admin;

/**
 * Created by echisan on 2018/6/9
 */
public class IndexInfo {

    // 网站今日总访问数
    private Long visit;
    // 在线人总人数
    private Long totalOnline;
    // 在线游客
    private Long anonOnline;
    // 在线注册用户
    private Long authOnline;
    // 总注册人数
    private Long totalUsers;
    // 今日新增人数
    private Long newUsers;

    public Long getVisit() {
        return visit;
    }

    public void setVisit(Long visit) {
        this.visit = visit;
    }

    public Long getTotalOnline() {
        return totalOnline;
    }

    public void setTotalOnline(Long totalOnline) {
        this.totalOnline = totalOnline;
    }

    public Long getAnonOnline() {
        return anonOnline;
    }

    public void setAnonOnline(Long anonOnline) {
        this.anonOnline = anonOnline;
    }

    public Long getAuthOnline() {
        return authOnline;
    }

    public void setAuthOnline(Long authOnline) {
        this.authOnline = authOnline;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getNewUsers() {
        return newUsers;
    }

    public void setNewUsers(Long newUsers) {
        this.newUsers = newUsers;
    }
}
