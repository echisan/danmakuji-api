package cc.dmji.api.service;

import cc.dmji.api.web.model.admin.LoginUserToken;

import java.util.List;
import java.util.Set;

/**
 * Created by echisan on 2018/5/21
 */
public interface RedisTokenService {

    void saveToken(String token);

    boolean hasToken(String token);

    /**
     * 删除redis中的token
     * @param token token
     * @return 如果删除成功，会返回该token的index，假如不存在则会返回-1
     */
    Long invalidToken(String token);

    /**
     * 列出token列表
     * @param pn 页码
     * @param ps 每页大小
     * @return tokens
     */
    List<String> listTokens(Integer pn, Integer ps);

    List<LoginUserToken> listUserTokens(String username);

    Long countTokens();

    void addUserLock(String uid);

    Long deleteUserLock(String uid);

    boolean isUserLock(String uid);
}
