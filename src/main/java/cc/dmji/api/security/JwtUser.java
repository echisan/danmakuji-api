package cc.dmji.api.security;

import cc.dmji.api.entity.User;
import cc.dmji.api.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by echisan on 2018/5/17
 */
public class JwtUser implements UserDetails {

    private String id;
    private String nick;
    private String pwd;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    private Byte isLock;
    private Byte isEmailVerify;

    public JwtUser(String id, String nick, String pwd, String email, Collection<? extends GrantedAuthority> authorities, Byte isLock, Byte isEmailVerify) {
        this.id = id;
        this.nick = nick;
        this.pwd = pwd;
        this.email = email;
        this.authorities = authorities;
        this.isLock = isLock;
        this.isEmailVerify = isEmailVerify;
    }

    public JwtUser(User user) {
        id = user.getUserId();
        nick = user.getNick();
        pwd = user.getPwd();
        email = user.getEmail();
        authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
        isLock = user.getIsLock();
        isEmailVerify = user.getEmailVerified();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return pwd;
    }

    @Override
    public String getUsername() {
        return nick;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isLock.equals(UserStatus.UN_LOCK.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getEmail() {
        return email;
    }

    public Byte getIsLock() {
        return isLock;
    }

    public Byte getIsEmailVerify() {
        return isEmailVerify;
    }
}
