package cc.dmji.api.web.filter;

import cc.dmji.api.constants.HeaderConstants;
import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.constants.SecurityConstants;
import cc.dmji.api.service.OnlineUserRedisService;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.GeneralUtils;
import cc.dmji.api.utils.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by echisan on 2018/6/19
 */
@Component
public class OnlineUserFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(OnlineUserFilter.class);

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private OnlineUserRedisService onlineUserRedisService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
//        logger.debug("OnlineUserFilter");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 如果是后台的路径的话就不拦了
        if (isAdminUri(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String tokenHeader = request.getHeader(SecurityConstants.TOKEN_HEADER_AUTHORIZATION);
//        String clientId = request.getHeader(HeaderConstants.CLIENT_ID);
        if (tokenHeader != null) {
            if (tokenHeader.contains("Bearer")){
                // 如果请求头中存在token,就算是以登录的用户
                String token = tokenHeader.replace(SecurityConstants.TOKEN_PREFIX, "");
                try {
                    // 如果能获取uid
                    Long uid = jwtTokenUtils.getUid(token);
                    onlineUserRedisService.insertOnlineUser(String.valueOf(uid), true);
                    stringRedisTemplate.boundZSetOps(RedisKey.ONLINE_ANON_USER_KEY).remove(GeneralUtils.getIpAddress(request));
                    filterChain.doFilter(request, response);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    // 如果解析不了，就算是未登录用户, 看看有没有clientId
                    // 如果没有clientId就算了,也不拦了
                    onlineUserRedisService.insertOnlineUser(GeneralUtils.getIpAddress(request), false);
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        } else {
            onlineUserRedisService.insertOnlineUser(GeneralUtils.getIpAddress(request), false);
        }

        // 如果请求头中的没有，clientId也没有，那就算没有这个人了，ip地址也算了
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    /**
     * @return 是否访问的是管理后台
     */
    private boolean isAdminUri(String uri) {
        String[] split = uri.split("/");
        if (split.length < 2) {
            return false;
        }
        return split[1].equals("admin");
    }
}
