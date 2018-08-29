package cc.dmji.api.web.controller.admin;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.entity.User;
import cc.dmji.api.service.OnlineUserRedisService;
import cc.dmji.api.service.UserService;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.web.controller.BaseController;
import cc.dmji.api.web.model.IpSbResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Created by echisan on 2018/7/24
 */
@RestController
@RequestMapping("/admin/online")
public class AdminOnlineController extends BaseController {
    private static final String IP_API = "https://api.ip.sb/geoip/";
    private static final String VISITOR_INFO_CACHE_KEY = "online_visitor_info_cache_key";

    @Autowired
    private OnlineUserRedisService onlineUserRedisService;

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<Result> listOnlineUser(@RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn,
                                                 @RequestParam(value = "ps", required = false, defaultValue = "20") Integer ps,
                                                 @RequestParam(value = "t", required = false, defaultValue = "0") Integer today) {

        if (today != 0 && today != 1) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "t参数只支持参数0或1");
        }
        Set<String> onlineUserIds = null;
        if (today == 0) {
            onlineUserIds = onlineUserRedisService.listAuthOnlineUserIds();
        } else {
            onlineUserIds = onlineUserRedisService.listTodayOnlineUserIds();
        }
        List<Long> onlineUserIdList = new ArrayList<>();
        onlineUserIds.forEach(s -> onlineUserIdList.add(Long.valueOf(s)));
        Map<String, Object> data = new HashMap<>();
        if (onlineUserIds.size() > 0) {
            PageInfo pageInfo = new PageInfo(pn, ps, onlineUserIds.size());
            int from = (pn - 1) * ps;
            int to = from + ps - 1;
            List<Long> subIds = null;
            if (onlineUserIds.size() > to) {
                subIds = onlineUserIdList.subList(from, to);
            } else {
                subIds = onlineUserIdList.subList(from, onlineUserIds.size());
            }
            List<User> users = userService.listUserByIdsIn(subIds);

            data.put("page", pageInfo);
            data.put("users", users);
        } else {
            PageInfo pageInfo = new PageInfo(pn, ps, 0);
            data.put("page", pageInfo);
            data.put("users", Collections.EMPTY_LIST);
        }

        return getResponseEntity(HttpStatus.OK, getSuccessResult(data));
    }

    @GetMapping("/visitors")
    public Result listTodayVisitors(@RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn,
                                    @RequestParam(value = "ps", required = false, defaultValue = "50") Integer ps) {

        BoundZSetOperations<String, String> ops = stringRedisTemplate.boundZSetOps(RedisKey.ONLINE_ANON_USER_KEY);
        int from = (pn - 1) * ps;
        int to = from + ps - 1;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = ops.rangeWithScores(from,to);
        List<Map<String, Object>> visitorDataList = new ArrayList<>();
        if (typedTuples != null && typedTuples.size() != 0) {
            typedTuples.forEach(stt -> {
                Map<String, Object> data = new HashMap<>(4);
                data.put("ip", stt.getValue());
                data.put("last_vtime", String.valueOf(new Double(stt.getScore()).longValue()));
//                ResponseEntity<IpSbResp> forEntity = restTemplate.getForEntity(IP_API + stt.getValue(), IpSbResp.class);
//                if (forEntity.getStatusCode().equals(HttpStatus.OK)){
//                    IpSbResp ipSbResp = forEntity.getBody();
//                    String location = ipSbResp.getCountry()+","+ipSbResp.getRegion()+","+ipSbResp.getCity();
//                    data.put("location",location);
//                    data.put("location_detail",ipSbResp);
//                }else {
//                    data.put("location","");
//                }
                visitorDataList.add(data);
            });

        }
        PageInfo pageInfo = new PageInfo(pn, ps, ops.size());
        Map<String, Object> resultMap = new HashMap<>(4);
        resultMap.put("visitors", visitorDataList);
        resultMap.put("page", pageInfo);
        return getSuccessResult(resultMap);

    }

}
