package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.SecurityConstants;
import cc.dmji.api.entity.User;
import cc.dmji.api.enums.Role;
import cc.dmji.api.service.UserService;
import cc.dmji.api.utils.JwtTokenUtils;
import cc.dmji.api.utils.JwtUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by echisan on 2018/5/16
 */
public class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    public Result getSuccessResult() {
        return new Result<>().setResultCode(ResultCode.SUCCESS);
    }

    public <T> Result<T> getSuccessResult(T data) {
        Result<T> result = new Result<>();
        result.setResultCode(ResultCode.SUCCESS);
        result.setData(data);
        return result;
    }

    public Result getSuccessResult(String msg) {
        Result result = new Result().setResultCode(ResultCode.SUCCESS);
        result.setMsg(msg);
        return result;
    }

    public <T> Result<T> getSuccessResult(T data, String msg) {
        Result<T> result = getSuccessResult(data);
        result.setMsg(msg);
        return result;
    }

    public Result getErrorResult(ResultCode resultCode, String msg) {
        Result result = new Result();
        result.setResultCode(resultCode);
        result.setMsg(msg);
        return result;
    }

    public <T> Result<T> getErrorResult(ResultCode resultCode, String msg, T data) {
        Result<T> result = new Result<>();
        result.setResultCode(resultCode);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public Result getErrorResult(ResultCode resultCode) {
        return new Result().setResultCode(resultCode);
    }

    public ResponseEntity<Result> getResponseEntity(HttpStatus httpStatus, Result result) {
        return new ResponseEntity<>(result, httpStatus);
    }

    public String getToken(HttpServletRequest request) {
        String header = request.getHeader(SecurityConstants.TOKEN_HEADER_AUTHORIZATION);
        if (StringUtils.isEmpty(header)) {
            return null;
        }
        return header.replace(SecurityConstants.TOKEN_PREFIX, "");
    }

    public Long getUidFromRequest(HttpServletRequest request) {
        String token = getToken(request);
        if (token != null) {
            JwtTokenUtils jwtTokenUtils = new JwtTokenUtils();
            return jwtTokenUtils.getUid(token);
        }
        return null;
    }

    public String getNickFormRequest(HttpServletRequest request) {
        return new JwtTokenUtils().getUsername(getToken(request));
    }

    /**
     * 获取当前用户， 只能用在"/admin/**"下
     * 因为在header上肯定存在token
     *
     * @param request     request
     * @param userService userServiceImpl
     * @return User
     */
    public User getCurrentUser(HttpServletRequest request, UserService userService) {
        Long uid = getUidFromRequest(request);
        return userService.getUserById(uid);
    }

    public ResponseEntity<Result> getErrorResponseEntity(HttpStatus httpStatus, ResultCode resultCode, String msg) {
        return getResponseEntity(httpStatus, getErrorResult(resultCode, msg));
    }

    public ResponseEntity<Result> getErrorResponseEntity(HttpStatus httpStatus, ResultCode resultCode) {
        return getResponseEntity(httpStatus, getErrorResult(resultCode));
    }

    public ResponseEntity<Result> getSuccessResponseEntity(Result result) {
        return getResponseEntity(HttpStatus.OK, result);
    }

    /**
     * 必须是要登陆后才能使用
     *
     * @param request request
     * @return @see JwtUserInfo
     */
    public JwtUserInfo getJwtUserInfo(HttpServletRequest request) {
        String token = getToken(request);
        if (token == null) return null;
        JwtTokenUtils.Payload payload = new JwtTokenUtils().getPayload(token);
        JwtUserInfo userInfo = new JwtUserInfo();
        userInfo.setCreateTime(payload.getCreateTime());
        userInfo.setIssAt(payload.getIssAt());
        userInfo.setCreateTime(payload.getCreateTime());
        userInfo.setNick(payload.getUsername());
        userInfo.setRole(Role.byRoleName(payload.getRole()));
        userInfo.setEmailVerify(payload.isEmailVerify());
        userInfo.setLock(payload.isLock());
        userInfo.setUid(payload.getUid());
        userInfo.setExpiration(payload.getExpiration());
        return userInfo;
    }

}
