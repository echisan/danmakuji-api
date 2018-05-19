package cc.dmji.api.web.model;

/**
 * Created by echisan on 2018/5/18
 */
public class AuthUser {

    private String principal;
    private String password;
    private Integer remember_me;

    public AuthUser() {
    }

    public AuthUser(String principal, String password, Integer remember_me) {
        this.principal = principal;
        this.password = password;
        this.remember_me = remember_me;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRemember_me() {
        return remember_me;
    }

    public void setRemember_me(Integer remember_me) {
        this.remember_me = remember_me;
    }

    @Override
    public String toString() {
        return "AuthUser{" +
                "principal='" + principal + '\'' +
                ", password='" + password + '\'' +
                ", remember_me=" + remember_me +
                '}';
    }
}
