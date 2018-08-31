package cc.dmji.api.web.socket;

import cc.dmji.api.config.MyEndpointConfigure;
import cc.dmji.api.constants.RedisKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint(value = "/watch/{epId}", configurator = MyEndpointConfigure.class)
@Component
public class WatchPageWs {
    private static final Logger logger = LoggerFactory.getLogger(WatchPageWs.class);
    public static final AtomicInteger watchPageTotalCount = new AtomicInteger(0);
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<Session>> sessionMap = new ConcurrentHashMap<>();

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @OnError
    public void onError(Throwable throwable) {
        logger.warn(this.getClass().getName() + "，webSocket链接出现了错误,{}" + throwable.getMessage());
        throwable.printStackTrace();
    }

    @OnOpen
    public void connected(@PathParam("epId") String epId, Session session) {
        // 先判断是否存在该key
        if (sessionMap.containsKey(epId)) {
            CopyOnWriteArraySet<Session> sessions = sessionMap.get(epId);
            sessions.add(session);
        } else {
            CopyOnWriteArraySet<Session> sessionSet = new CopyOnWriteArraySet<>();
            sessionSet.add(session);
            sessionMap.put(epId, sessionSet);
        }
        int size = sessionMap.get(epId).size();
        updateRedisCount(epId,size);
        watchPageTotalCount.incrementAndGet();
        sendAllByEpId(epId, String.valueOf(size));
    }

    @OnMessage
    public void onMessage(String message) throws IOException {
        logger.debug("onMessage message:{}", message);
    }

    @OnClose
    public void onClose(@PathParam("epId") String epId, Session session) {
        // 关闭webSocket时
        sessionMap.get(epId).remove(session);
        int size;
        if ((size = sessionMap.get(epId).size()) == 0) {
            sessionMap.remove(epId);
        }
        updateRedisCount(epId,size);
        watchPageTotalCount.decrementAndGet();
        sendAllByEpId(epId, String.valueOf(size));
    }

    private void sendAllByEpId(String epId, String msg) {
        CopyOnWriteArraySet<Session> sessions = sessionMap.get(epId);
        if (sessions != null && sessions.size() != 0) {
            sessions.forEach(session -> {
                try {
                    session.getBasicRemote().sendText(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void updateRedisCount(String epId, Integer count) {
        // 如果在线人数不为0 则更新，如果为0则删除该key
        if (count != 0) {
            stringRedisTemplate.opsForZSet().add(RedisKey.WATCH_EPISODE_ONLINE_EACH, epId, count);
        } else {
            stringRedisTemplate.opsForZSet().remove(RedisKey.WATCH_EPISODE_ONLINE_EACH, epId);
        }
    }
}
