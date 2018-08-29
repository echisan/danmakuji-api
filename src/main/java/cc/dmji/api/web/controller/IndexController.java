package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;
import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.entity.IndexRecommend;
import cc.dmji.api.service.IndexRecommendService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/index")
public class IndexController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IndexRecommendService indexRecommendService;

    @GetMapping("/is")
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
        resultMap.put("is", sentence);

        // 首页推荐
        String indexRecommendCacheJson = stringRedisTemplate.opsForValue().get(RedisKey.INDEX_RECOMMEND_CACHE);
        if (indexRecommendCacheJson == null || indexRecommendCacheJson.equals("")) {
            Page<IndexRecommend> indexRecommends = indexRecommendService.listByShowIndex(1, 5);
            List<IndexRecommend> indexRecommendList = indexRecommends.getContent();
            resultMap.put("ir", indexRecommendList);
            stringRedisTemplate.opsForValue().set(RedisKey.INDEX_RECOMMEND_CACHE,
                    new ObjectMapper().writeValueAsString(indexRecommendList));
        } else {
            resultMap.put("ir", new ObjectMapper().readValue(indexRecommendCacheJson, IndexRecommend.class));
        }

        return getSuccessResult(resultMap);
    }

}
