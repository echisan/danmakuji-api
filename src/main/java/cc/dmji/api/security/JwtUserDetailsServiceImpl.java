package cc.dmji.api.security;

import cc.dmji.api.entity.User;
import cc.dmji.api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by echisan on 2018/5/17
 */
@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(JwtUserDetailsServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userService.getUserByNick(s);
        if (user == null){
            user = userService.getUserByEmail(s);
        }
        if (user == null) {
            logger.debug("用户名为 {} 的用户不存在", s);
            throw new UsernameNotFoundException("该用户不存在");
        }
        return new JwtUser(user);
    }
}
