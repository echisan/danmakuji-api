package cc.dmji.api.service;

import java.util.Set;

/**
 * Created by echisan on 2018/5/21
 */
public interface RedisTokenService {

    void saveToken(String token);

    boolean hasToken(String token);

    Long invalidToken(String token);

    Set<String> listTokens();

}
