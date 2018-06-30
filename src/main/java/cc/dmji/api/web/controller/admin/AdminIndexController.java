package cc.dmji.api.web.controller.admin;

import cc.dmji.api.common.Result;
import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.service.OnlineUserRedisService;
import cc.dmji.api.service.ReplyService;
import cc.dmji.api.service.UserService;
import cc.dmji.api.utils.GeneralUtils;
import cc.dmji.api.web.controller.BaseController;
import cc.dmji.api.web.model.admin.IndexInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by echisan on 2018/6/9
 */
@RestController
@RequestMapping("/admin/mainInfo")
public class AdminIndexController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private OnlineUserRedisService onlineUserRedisService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ReplyService replyService;

    @GetMapping
    public ResponseEntity<Result> getIndexInfo(){

        Map<String, Object> map = new HashMap<>();

        // 当前总用户数
        map.put("totalUsers", userService.countUsers());
        // 今天注册的用户数
        Long newUsersCount = userService.countUsersByCreateTime(GeneralUtils.getToday0Clock(), GeneralUtils.getToday2359Clock());
        map.put("newUsers", newUsersCount);

        // 当前在线游客
        Long anon = onlineUserRedisService.countAnonOnlineUser();
        map.put("anonOnline", anon);

        // 当前在线注册用户
        Long auth = onlineUserRedisService.countAuthOnlineUser();
        map.put("authOnline", auth);

        // 当前在线用户
        map.put("totalOnline",anon+auth);

        // 总访问人数
        String visitCountString = stringRedisTemplate.opsForValue().get(RedisKey.VISIT_COUNT_KEY);
        Long visitCount = StringUtils.isEmpty(visitCountString) ? 0L :Long.valueOf(visitCountString);
        map.put("visit", visitCount);

        // 新评论
        Long newReplies = replyService.countReplysBetween(GeneralUtils.getToday0Clock(), GeneralUtils.getToday2359Clock());
        map.put("newReplies", newReplies);

        // 今日总访客
        Long totalVisitors = onlineUserRedisService.countVisitors();
        map.put("totalVisitors", totalVisitors);

        // 今日在线游客峰值
        map.put("maxAnonOnline",onlineUserRedisService.countTodayMaxAnonOnlineUser());
        // 今日在线注册用户峰值
        map.put("maxAuthOnline",onlineUserRedisService.countTodayMaxAuthOnlineUser());

        return getResponseEntity(HttpStatus.OK, getSuccessResult(map));
    }
}
