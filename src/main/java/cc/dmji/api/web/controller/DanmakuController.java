package cc.dmji.api.web.controller;

import cc.dmji.api.constants.DanmakuResponseType;
import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.constants.SecurityConstants;
import cc.dmji.api.entity.Danmaku;
import cc.dmji.api.service.DanmakuService;
import cc.dmji.api.utils.GeneralUtils;
import cc.dmji.api.utils.JwtTokenUtils;
import cc.dmji.api.web.model.DanmakuResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/dplayer/v2")
public class DanmakuController {

    private static final Logger logger = LoggerFactory.getLogger(DanmakuController.class);

    private static final Long POST_FREQUENT_IP_TIME_OUT = 5L;
    private static final String BLACK_LIST_FILE_NAME = "blacklist";

    @Autowired
    private DanmakuService danmakuService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @CrossOrigin
    @GetMapping
    public DanmakuResponse getDanmakuList(@RequestParam("id") String id,
                                          @RequestParam(value = "max", required = false, defaultValue = "1000") Integer max) {
        DanmakuResponse danmakuResponse = new DanmakuResponse();
        try {
            List<Danmaku> danmakuEntityList = danmakuService.listDanmakuById(id, max);
            if (danmakuEntityList != null && danmakuEntityList.size() != 0) {
                danmakuResponse.setDanmaku(parseDanmakuListToArray(danmakuEntityList));
            } else {
                danmakuResponse.setDanmaku(new ArrayList<>());
            }
            danmakuResponse.setCode(DanmakuResponseType.SUCCESS);
            danmakuResponse.setMsg("");
            return danmakuResponse;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("数据库出错");
            danmakuResponse.setCode(DanmakuResponseType.DATABASE_ERROR);
            danmakuResponse.setMsg("数据库出现了点偏差");
            return danmakuResponse;
        }
    }

    @CrossOrigin
    @PostMapping
    public DanmakuResponse postDanmaku(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Danmaku danmaku = new ObjectMapper().readValue(request.getInputStream(), Danmaku.class);
        DanmakuResponse danmakuResponse = new DanmakuResponse();
        // 先验证token
        String header = request.getHeader(SecurityConstants.TOKEN_HEADER_AUTHORIZATION);
        if (StringUtils.isEmpty(header)) {
            header = danmaku.getToken();
        }
        if (StringUtils.isEmpty(header)) {
            danmakuResponse.setMsg("请先登录后再发弹幕");
            danmakuResponse.setCode(DanmakuResponseType.PERMISSION_DENY);
            return danmakuResponse;
        }

        String token = header.replace(SecurityConstants.TOKEN_PREFIX, "");
        if (!jwtTokenUtils.validateToken(token)) {
            danmakuResponse.setMsg("无效的登录凭证或该凭证已过期，请重新登录");
            danmakuResponse.setCode(DanmakuResponseType.PERMISSION_DENY);
            return danmakuResponse;
        }

        // 获取请求域
        String refererHeader = request.getHeader("referer");
        String referer = refererHeader == null ? "" : refererHeader;
        logger.info("referer [{}]", referer);

        String author = danmaku.getAuthor();
        String color = danmaku.getColor();
        double time = danmaku.getTime();
        String player = danmaku.getPlayer();
        String text = danmaku.getText();
        String type = danmaku.getType();
        String ip = GeneralUtils.getIpAddress(request);

        logger.info("请求参数 :{} ip :{}", danmaku, ip);


        // 去除黑名单


        String fequentIpKey = RedisKey.POST_FREQUENT_IP_KEY + ip;
        if (stringRedisTemplate.hasKey(fequentIpKey)) {
            logger.info("ip为 [{}] 访问频繁");
            danmakuResponse.setCode(DanmakuResponseType.FREQUENT_OPERATION);
            danmakuResponse.setMsg("你发弹幕太快啦!稍后再试试吧!");
            return danmakuResponse;
        } else {
            stringRedisTemplate.opsForValue().set(fequentIpKey, ip, POST_FREQUENT_IP_TIME_OUT, TimeUnit.SECONDS);
        }

        if (isEmpty(author) || isEmpty(color) || isEmpty(player)
                || isEmpty(text) || isEmpty(type)) {
            danmakuResponse.setCode(DanmakuResponseType.ILLEGAL_DATA);
            danmakuResponse.setMsg("数据异常");
            return danmakuResponse;
        }


        Danmaku danmakuEntity = new Danmaku();
        danmakuEntity.setAuthor(GeneralUtils.htmlEncode(author));
        danmakuEntity.setColor(GeneralUtils.htmlEncode(color));
        danmakuEntity.setPlayer(GeneralUtils.htmlEncode(player));
        danmakuEntity.setText(GeneralUtils.htmlEncode(text));
        danmakuEntity.setTime(time);
        danmakuEntity.setType(GeneralUtils.htmlEncode(type));
        danmakuEntity.setIpAddress(ip);
        danmakuEntity.setReferer(referer);
//        danmakuEntity.setToken(token);

        try {
            Danmaku newDanmaku = danmakuService.saveDanmaku(danmakuEntity);
            danmakuResponse.setCode(DanmakuResponseType.SUCCESS);
            danmakuResponse.setMsg("ok");
            danmakuResponse.setDanmaku(parseDanmakuListToArray(Collections.singletonList(newDanmaku)));
            return danmakuResponse;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("数据库出现了点偏差");
            danmakuResponse.setCode(DanmakuResponseType.DATABASE_ERROR);
            danmakuResponse.setMsg("数据库出现了点偏差");
            return danmakuResponse;
        }
    }

    private boolean isEmpty(String string) {
        return StringUtils.isEmpty(string);
    }

    /**
     * 将弹幕type转成int
     *
     * @param type 弹幕type
     * @return 弹幕代号
     */
    private int parseTypeToInt(String type) {
        if (type.equals("right")) {
            return 0;
        }
        if (type.equals("top")) {
            return 1;
        }
        if (type.equals("bottom")) {
            return 2;
        }
        return 0;
    }

    /**
     * 将弹幕数据包装成dplayer能识别的格式
     *
     * @param danmakuEntities 弹幕列表
     * @return 弹幕列表
     */
    private List<Object[]> parseDanmakuListToArray(List<Danmaku> danmakuEntities) {
        List<Object[]> data = new ArrayList<>();
        if (danmakuEntities != null && danmakuEntities.size() != 0) {
            for (Danmaku de : danmakuEntities) {
                Object[] danmaku = new Object[]{de.getTime(), parseTypeToInt(de.getType()), de.getColor(), de.getAuthor(), de.getText()};
                data.add(danmaku);
            }
            return data;
        }
        return data;
    }
}
