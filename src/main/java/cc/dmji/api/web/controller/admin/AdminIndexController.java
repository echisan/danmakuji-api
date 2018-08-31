package cc.dmji.api.web.controller.admin;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.entity.IndexRecommend;
import cc.dmji.api.enums.Status;
import cc.dmji.api.service.IndexRecommendService;
import cc.dmji.api.service.OnlineUserRedisService;
import cc.dmji.api.service.UserService;
import cc.dmji.api.service.v2.ReplyV2Service;
import cc.dmji.api.utils.GeneralUtils;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by echisan on 2018/6/9
 */
@RestController
@RequestMapping("/admin/index")
public class AdminIndexController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private OnlineUserRedisService onlineUserRedisService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ReplyV2Service replyV2Service;
    @Autowired
    private IndexRecommendService indexRecommendService;

    @GetMapping("/mainInfo")
    public ResponseEntity<Result> getIndexInfo() {

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
        map.put("totalOnline", anon + auth);

        // 总访问人数
        String visitCountString = stringRedisTemplate.opsForValue().get(RedisKey.VISIT_COUNT_KEY);
        Long visitCount = StringUtils.isEmpty(visitCountString) ? 0L : Long.valueOf(visitCountString);
        map.put("visit", visitCount);

        // 新评论
        Long newReplies = replyV2Service
                .countReplyByCreateTimeBetween(
                        new Timestamp(GeneralUtils.getToday0Clock().getTime()),
                        new Timestamp(GeneralUtils.getToday2359Clock().getTime()));
        map.put("newReplies", newReplies);

        // 今日总访客
        Long totalVisitors = onlineUserRedisService.countVisitors();
        map.put("totalVisitors", totalVisitors);

        // 今日在线游客峰值
        map.put("maxAnonOnline", onlineUserRedisService.countTodayMaxAnonOnlineUser());
        // 今日在线注册用户峰值
        map.put("maxAuthOnline", onlineUserRedisService.countTodayMaxAuthOnlineUser());

        return getResponseEntity(HttpStatus.OK, getSuccessResult(map));
    }

    @PostMapping("/is")
    public Result setIndexSentence(@RequestBody Map<String, String> requestMap) {
        String word = requestMap.get("sentence");
        stringRedisTemplate.opsForValue().set(RedisKey.INDEX_SENTENCE, word);
        return getSuccessResult("设置成功～");
    }

    @GetMapping("/is")
    public Result getIndexSentence() {
        String s = stringRedisTemplate.opsForValue().get(RedisKey.INDEX_SENTENCE);
        if (s == null) {
            s = "";
        }
        return getSuccessResult(s, "ok");
    }

    @GetMapping("/recommend")
    public Result listRecommend(@RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn,
                                @RequestParam(value = "ps", required = false, defaultValue = "20") Integer ps,
                                @RequestParam(value = "sort", required = false, defaultValue = "1") Integer sort) {
        // 排序，1是按照时间倒序排序
        // 2 是按照权重排序
        Page<IndexRecommend> indexRecommendPage;
        if (sort == 1) {
            indexRecommendPage = indexRecommendService.listByShowIndex(pn, ps);
        } else {
            indexRecommendPage = indexRecommendService.listByCreateTimeDesc(pn, ps);
        }
        List<IndexRecommend> indexRecommendList = indexRecommendPage.getContent();
        PageInfo pageInfo = new PageInfo(pn, ps, indexRecommendPage.getTotalElements());
        Map<String, Object> resultMap = new HashMap<>(4);
        resultMap.put("irs", indexRecommendList);
        resultMap.put("page", pageInfo);

        return getSuccessResult(resultMap);
    }

    @GetMapping("/recommend/{irid}")
    public Result getRecommend(@PathVariable("irid") Long irId) {
        IndexRecommend indexRecommend = indexRecommendService.getById(irId);
        if (indexRecommend == null) {
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        return getSuccessResult(indexRecommend);
    }

    @PostMapping("/recommend")
    public Result postRecommend(@RequestBody Map<String, String> requestMap,
                                HttpServletRequest request) {
        String title = requestMap.get("title");
        String imageUrl = requestMap.get("image_url");
        String linkUrl = requestMap.get("link_url");
        if (!StringUtils.hasText(title) || !StringUtils.hasText(imageUrl) || !StringUtils.hasText(linkUrl)) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "title,imageUrl,linkUrl cannot be null!!");
        }
        String showIndexStr = requestMap.get("show_index");
        Integer showIndex;
        if (showIndexStr == null) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "是否在首页显示不能为空");
        }
        if (!showIndexStr.equals("1") && !showIndexStr.equals("0")) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "show_index类型不合法，只能是0或1");
        }
        showIndex = Integer.valueOf(showIndexStr);
        IndexRecommend ir = new IndexRecommend();
        ir.setRecommendStatus(Status.NORMAL);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ir.setCreateTime(timestamp);
        ir.setModifyTime(timestamp);
        ir.setImageUrl(imageUrl);
        ir.setLinkUrl(linkUrl);
        ir.setTitle(title);
        if (showIndex.equals(1)) {
            ir.setShowIndex(true);
        } else {
            ir.setShowIndex(false);
        }
        ir.setPublisherId(getJwtUserInfo(request).getUid());
        IndexRecommend insert = indexRecommendService.insert(ir);

        // 如果是显示在首页的话就清除缓存
        if (insert.isShowIndex()) {
            stringRedisTemplate.delete(RedisKey.INDEX_RECOMMEND_CACHE);
        }

        return getSuccessResult(insert);
    }

    @PutMapping("/recommend/{irid}")
    public Result updateIndexRecommend(@PathVariable("irid") Long irId,
                                       @RequestBody Map<String, String> requestMap,
                                       HttpServletRequest request) {
        IndexRecommend indexRecommend = indexRecommendService.getById(irId);
        if (indexRecommend == null) {
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND, "被修改的对象不存在，请检查你的irid是否正确");
        }
        String title = requestMap.get("title");
        String imageUrl = requestMap.get("image_url");
        String linkUrl = requestMap.get("link_url");
        if (!StringUtils.hasText(title) || !StringUtils.hasText(imageUrl) || !StringUtils.hasText(linkUrl)) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "title,imageUrl,linkUrl cannot be null!!");
        }
        String showIndexStr = requestMap.get("show_index");
        Integer showIndex;
        if (showIndexStr == null) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "是否在首页显示不能为空");
        }
        if (!showIndexStr.equals("1") && !showIndexStr.equals("0")) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "show_index类型不合法，只能是0或1");
        }
        showIndex = Integer.valueOf(showIndexStr);

        if (showIndex.equals(1)) {
            indexRecommend.setShowIndex(true);
        } else {
            indexRecommend.setShowIndex(false);
        }
        indexRecommend.setLinkUrl(linkUrl);
        indexRecommend.setImageUrl(imageUrl);
        indexRecommend.setPublisherId(getUidFromRequest(request));
        indexRecommend.setTitle(title);
        indexRecommend.setModifyTime(new Timestamp(System.currentTimeMillis()));
        IndexRecommend update = indexRecommendService.update(indexRecommend);

        if (update.isShowIndex()) {
            stringRedisTemplate.delete(RedisKey.INDEX_RECOMMEND_CACHE);
        }
        return getSuccessResult(update);
    }

    @DeleteMapping("/recommend/{irid}")
    public Result deleteIndexRecommendById(@PathVariable("irid") Long irId,
                                           HttpServletRequest request) {
        IndexRecommend indexRecommend = indexRecommendService.getById(irId);
        if (indexRecommend == null) {
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND, "需要删除的首页推荐不存在，请检查你的irId参数");
        }
        indexRecommend.setPublisherId(getUidFromRequest(request));
        indexRecommend.setModifyTime(new Timestamp(System.currentTimeMillis()));
        IndexRecommend delete = indexRecommendService.delete(indexRecommend);

        if (delete.isShowIndex()) {
            stringRedisTemplate.delete(RedisKey.INDEX_RECOMMEND_CACHE);
        }
        return getSuccessResult(delete);
    }
}
