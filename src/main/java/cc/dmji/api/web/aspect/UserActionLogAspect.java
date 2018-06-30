package cc.dmji.api.web.aspect;

import cc.dmji.api.annotation.UserLog;
import cc.dmji.api.constants.HeaderConstants;
import cc.dmji.api.constants.SecurityConstants;
import cc.dmji.api.entity.UserLogRecord;
import cc.dmji.api.service.UserLogRecordService;
import cc.dmji.api.utils.GeneralUtils;
import cc.dmji.api.utils.JwtTokenUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by echisan on 2018/6/19
 */
@Aspect
@Component
public class UserActionLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(UserActionLogAspect.class);

    @Autowired
    @Qualifier("userLogRecordRedisServiceImpl")
    private UserLogRecordService userLogRecordRedisService;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Pointcut("@annotation(cc.dmji.api.annotation.UserLog)")
    public void userActionLogPointCut() {
    }

    @After("userActionLogPointCut()")
    public void doAfter(JoinPoint joinPoint) throws ClassNotFoundException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        UserLogRecord ulr = new UserLogRecord();

        String tokenHeader = request.getHeader(SecurityConstants.TOKEN_HEADER_AUTHORIZATION);
        if (tokenHeader != null) {
            String token = tokenHeader.replace(SecurityConstants.TOKEN_PREFIX, "");
            JwtTokenUtils.Payload payload = jwtTokenUtils.getPayload(token);
            String username = payload.getUsername();
            String uid = payload.getUid();
            String role = payload.getRole();
            ulr.setUserId(uid);
            ulr.setNick(username);
            ulr.setUserRole(role);
        } else {
            ulr.setUserId("");
            ulr.setNick("");
            ulr.setUserRole("");
        }

        String clientId = request.getHeader(HeaderConstants.CLIENT_ID);
        if (clientId != null) {
            ulr.setClientId(clientId);
        } else {
            ulr.setClientId("");
        }

        String referer = request.getHeader("referer");
        if (referer != null) {
            ulr.setReferer(referer);
        } else {
            ulr.setReferer("");
        }

        ulr.setIpAddress(GeneralUtils.getIpAddress(request));
        ulr.setUrl(request.getRequestURL().toString());
        ulr.setUri(request.getRequestURI());
        ulr.setHttpMethod(request.getMethod());
        // 方法全名
        ulr.setMethod(getMethodName(joinPoint));

        String description = getMethodDescription(joinPoint);
        ulr.setDescription(description);
        String paramsString = Arrays.toString(joinPoint.getArgs());
        if (paramsString.length()>250){
            paramsString = paramsString.substring(0, 250);
        }
        ulr.setParams(paramsString);
        ulr.setCreateTime(new Date());
        logger.debug("用户日志信息: {}",ulr.toString());

        // 插入到redis
        userLogRecordRedisService.insertUserLogRecord(ulr);

    }

    private String getMethodDescription(JoinPoint joinPoint) throws ClassNotFoundException {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String description = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description = method.getAnnotation(UserLog.class).value();
                    break;
                }
            }
        }
        logger.debug("target_name : {}, method_name : {}, arguments : {}", targetName, methodName, arguments);
        return description;
    }

    private String getMethodName(JoinPoint joinPoint){
        return joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
    }
}
