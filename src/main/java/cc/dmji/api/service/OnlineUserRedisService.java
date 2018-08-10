package cc.dmji.api.service;

import java.util.List;
import java.util.Set;

public interface OnlineUserRedisService {

    /**
     * 插入一个在线用户
     * @param userFlag 用户标识，假如是已注册用户则传入uid，非注册用户则传入clientId
     * @param isAuth 是否是注册用户or 游客
     * @return 插入是否成功
     */
    boolean insertOnlineUser(String userFlag, boolean isAuth);

    Long countOnlineUser();

    /**
     * @return 今日最多同时在线人数
     */
    Long countTodayMaxOnlineUser();

    Long countAuthOnlineUser();

    /**
     * @return 今日最多在线注册用户数
     */
    Long countTodayMaxAuthOnlineUser();

    /**
     * @return 今日总共在线注册用户人数
     */
    Long countTodayTotalAuthOnlineUser();

    /**
     * @return 当前在线游客人数
     */
    Long countAnonOnlineUser();

    /**
     * @return 今日最多在线游客人数
     */
    Long countTodayMaxAnonOnlineUser();

    /**
     * @return 今日总共在线游客人数
     */
    Long countTodayTotalAnonOnlineUser();

    Long countVisitors();

    void deleteExpirationUsers();

    Set<String> listAuthOnlineUserIds();

    Set<String> listTodayOnlineUserIds();

}
