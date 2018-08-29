package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;
import cc.dmji.api.constants.RedisKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/index")
public class IndexController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/is")
    public Result getIndexSentence(){
        String s = stringRedisTemplate.opsForValue().get(RedisKey.INDEX_SENTENCE);
        if (s == null){
            s = "welcome to darker~";
        }
        return getSuccessResult(s,"ok");
    }


    @GetMapping()
    public Result getIndex(){
        // 首页欢迎语
        String sentence = stringRedisTemplate.opsForValue().get(RedisKey.INDEX_SENTENCE);
        if (sentence == null){
            sentence = "welcome to darker~";
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("sentence",sentence);

        

        return getSuccessResult(resultMap);
    }
}
