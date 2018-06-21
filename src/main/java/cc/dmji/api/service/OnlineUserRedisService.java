package cc.dmji.api.service;

import java.util.List;

public interface OnlineUserRedisService {

    /**
     * 插入一个在线用户
     * @param userFlag 用户标识，假如是已注册用户则传入uid，非注册用户则传入clientId
     * @param isAuth 是否是注册用户or 游客
     * @return 插入是否成功
     */
    boolean insertOnlineUser(String userFlag, boolean isAuth);

    Long countOnlineUser();

    Long countAuthOnlineUser();

    Long countAnonOnlineUser();

    Long countVisitors();

    void deleteExpirationUsers();

    List<String> listAuthOnlineUserIds();
}
