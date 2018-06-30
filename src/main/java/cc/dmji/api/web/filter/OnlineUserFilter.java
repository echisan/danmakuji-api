package cc.dmji.api.web.filter;

import cc.dmji.api.constants.HeaderConstants;
import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.constants.SecurityConstants;
import cc.dmji.api.service.OnlineUserRedisService;
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
        logger.debug("OnlineUserFilter");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 如果是后台的路径的话就不拦了
        if (isAdminUri(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 记录访问次数
        stringRedisTemplate.opsForValue().increment(RedisKey.VISIT_COUNT_KEY, 1);

        String tokenHeader = request.getHeader(SecurityConstants.TOKEN_HEADER_AUTHORIZATION);
        if (tokenHeader != null) {
            // 如果请求头中存在token,就算是以登录的用户
            String token = tokenHeader.replace(SecurityConstants.TOKEN_PREFIX, "");
            try {
                // 如果能获取uid
                String uid = jwtTokenUtils.getUid(token);
                onlineUserRedisService.insertOnlineUser(uid, true);
                filterChain.doFilter(request, response);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                // 如果解析不了，就算是未登录用户, 看看有没有clientId
                String clientId = request.getHeader(HeaderConstants.CLIENT_ID);
                // 如果没有clientId就算了,也不拦了
                if (clientId == null) {
                    filterChain.doFilter(request, response);
                    return;
                }
                onlineUserRedisService.insertOnlineUser(clientId, false);
                filterChain.doFilter(request, response);
                return;
            }
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
        if (split.length<2){
            return false;
        }
        return split[1].equals("admin");
    }
}
