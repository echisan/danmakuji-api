package cc.dmji.api.web.controller;

import cc.dmji.api.annotation.RequestLimit;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.entity.IndexRecommend;
import cc.dmji.api.service.DanmakuService;
import cc.dmji.api.service.EpisodeService;
import cc.dmji.api.service.IndexRecommendService;
import cc.dmji.api.web.model.EpisodeDetail;
import cc.dmji.api.web.socket.WatchPageWs;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/index")
public class IndexController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IndexRecommendService indexRecommendService;
    @Autowired
    private EpisodeService episodeService;
    @Autowired
    private DanmakuService danmakuService;

    @GetMapping("/is")
    @RequestLimit
    public Result getIndexSentence() {
        String s = stringRedisTemplate.opsForValue().get(RedisKey.INDEX_SENTENCE);
        if (s == null) {
            s = "welcome to darker~";
        }
        return getSuccessResult(s, "ok");
    }


    @GetMapping()
    public Result getIndex() throws IOException {
        // 首页欢迎语
        String sentence = stringRedisTemplate.opsForValue().get(RedisKey.INDEX_SENTENCE);
        if (sentence == null) {
            sentence = "welcome to darker~";
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("index_sentence", sentence);

        // 首页推荐
        String indexRecommendCacheJson = stringRedisTemplate.opsForValue().get(RedisKey.INDEX_RECOMMEND_CACHE);
        if (indexRecommendCacheJson == null || indexRecommendCacheJson.equals("")) {
            Page<IndexRecommend> indexRecommends = indexRecommendService.listByShowIndex(1, 5);
            List<IndexRecommend> indexRecommendList = indexRecommends.getContent();
            indexRecommendList.forEach(ir -> ir.setPublisherId(0L));
            resultMap.put("index_recommend", indexRecommendList);
            stringRedisTemplate.opsForValue().set(RedisKey.INDEX_RECOMMEND_CACHE,
                    new ObjectMapper().writeValueAsString(indexRecommendList));
        } else {
            resultMap.put("index_recommend", new ObjectMapper().readValue(indexRecommendCacheJson, IndexRecommend[].class));
        }

        // 在线观看总人数
        int watchPageTotalCount = WatchPageWs.watchPageTotalCount.get();
        resultMap.put("online_watch_count", watchPageTotalCount);
        return getSuccessResult(resultMap);
    }


    // TODO 日后会考虑加上缓存,目前人太少没得问题
    @GetMapping("/online")
    public Result getOnlineView() {
        BoundZSetOperations<String, String> ops = stringRedisTemplate.boundZSetOps(RedisKey.WATCH_EPISODE_ONLINE_EACH);
        Set<ZSetOperations.TypedTuple<String>> typedTuples = ops.reverseRangeByScoreWithScores(0, 9999999);

        if (typedTuples != null && typedTuples.size() != 0) {
            Map<String, Integer> onlineMap = new LinkedHashMap<>();
            List<Long> epIds = new ArrayList<>();
            final int[] count = {0};
            typedTuples.forEach(stringTypedTuple -> {
                int score = stringTypedTuple.getScore().intValue();
                onlineMap.put(stringTypedTuple.getValue(), score);
                epIds.add(Long.valueOf(stringTypedTuple.getValue()));
                count[0] = count[0] + score;
            });

            List<EpisodeDetail> episodeDetails = episodeService.listEpisodeDetailByEpIdIn(epIds);
            List<Map<String, Object>> onlineDetailResult = new ArrayList<>();
            episodeDetails.forEach(episodeDetail -> {
                Map<String, Object> map = new HashMap<>();
                map.put("bangumiName", episodeDetail.getBangumiName());
                map.put("epId", episodeDetail.getEpId());
                map.put("bangumiId", episodeDetail.getBangumiId());
                map.put("epIndex", episodeDetail.getEpIndex());
                map.put("episodeViewCount", episodeDetail.getEpisodeViewCount());
                String title = episodeDetail.getBangumiName();
                if (episodeDetail.getEpisodeTotal() != 1) {
                    title = title + " " + episodeDetail.getEpIndex();
                }
                map.put("title", title);
                map.put("onlineCount", onlineMap.get(String.valueOf(episodeDetail.getEpId())));
                map.put("danmakuCount", danmakuService.countDanmakuByPlayer(episodeDetail.getDanmakuId()));
                map.put("thumb", episodeDetail.getThumb());
                map.put("linkUrl", "/#/video/" + episodeDetail.getEpId());
                onlineDetailResult.add(map);
            });
            return getSuccessResult(onlineDetailResult);
        }
        return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND, "暂时没有人在观看视频", Collections.EMPTY_LIST);
    }
}
