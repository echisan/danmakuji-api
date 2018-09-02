package cc.dmji.api.web.controller;

import cc.dmji.api.annotation.RequestLimit;
import cc.dmji.api.annotation.UserLog;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.SecurityConstants;
import cc.dmji.api.entity.Danmaku;
import cc.dmji.api.service.DanmakuService;
import cc.dmji.api.utils.GeneralUtils;
import cc.dmji.api.utils.JwtTokenUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dplayer3/v3")
public class DanmakuController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(DanmakuController.class);
    private final int[] danmakuTypeHolder = new int[]{0, 1, 2};
    @Autowired
    private DanmakuService danmakuService;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    //    @CrossOrigin
    @GetMapping
    @UserLog("获取某个视频下的弹幕")
    public Result getDanmakuList(@RequestParam("id") String id,
                                 @RequestParam(value = "max", required = false, defaultValue = "1000") Integer max) {

        Page<Danmaku> danmakus = danmakuService.listDanmakuById(id, max);
        List<Object[]> danmakuItemList = new ArrayList<>();
        danmakus.getContent().forEach(danmaku -> {
            Object[] item = new Object[]{
                    danmaku.getTime(),
                    danmaku.getType(),
                    danmaku.getColor(),
                    danmaku.getUsername(),
                    danmaku.getText()
            };
            danmakuItemList.add(item);
        });
        return getSuccessResult(danmakuItemList);
    }

    //    @CrossOrigin
    @PostMapping
    @UserLog("发送弹幕")
    @RequestLimit(value = "你发弹幕太快啦!稍后再试试吧!")
    public Result postDanmaku(HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        Map<String, Object> requestMap = new ObjectMapper().readValue(inputStream, new TypeReference<Map<String, Object>>() {
        });

        // 校验用户
        Object tokenObj = requestMap.get("token");
        if (!(tokenObj instanceof String)) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "token类型错误");
        }
        String token = (String) tokenObj;
        Long uid;
        String nick;
        if (token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            token = token.replace(SecurityConstants.TOKEN_PREFIX, "");
            try {
                JwtTokenUtils.Payload payload = jwtTokenUtils.getPayload(token);
                uid = payload.getUid();
                nick = payload.getUsername();
            } catch (Exception e) {
                e.printStackTrace();
                return getErrorResult(ResultCode.USER_EXPIRATION);
            }
        } else {
            return getErrorResult(ResultCode.USER_NOT_LOGINED, "发送弹幕前请先登录啦～");
        }
        // 校验用户token完毕

        Danmaku danmaku = new Danmaku();
        try {
            String author = (String) requestMap.get("author");
            Integer color = (Integer) requestMap.get("color");
            String id = (String) requestMap.get("id");
            String text = (String) requestMap.get("text");
            Double time = (Double) requestMap.get("time");
            Integer type = (Integer) requestMap.get("type");
            if (!hasText(author)) {
                return getErrorResult(ResultCode.PARAM_IS_INVALID, "用户名不能为空");
            } else {
                // 如果发送的author与token中的nick不一致，则不能视为同一个人
                // 通过这里则视为通过用户权限验证了
                if (!author.equals(nick)) {
                    return getErrorResult(ResultCode.USER_HAVE_RISK, "该账号存在风险，请重新登录");
                }
            }
            if (color == null) {
                return getErrorResult(ResultCode.PARAM_IS_INVALID, "颜色不能为空");
            }
            if (!hasText(id)) {
                return getErrorResult(ResultCode.PARAM_IS_INVALID, "弹幕池id不能为空");
            }
            if (hasText(text)) {
                text = GeneralUtils.htmlEncode(text);
                if (!hasText(text)) {
                    return getErrorResult(ResultCode.PARAM_IS_INVALID, "弹幕不能为空");
                }
            }
            if (time == null) {
                return getErrorResult(ResultCode.PARAM_IS_INVALID, "time不能为空");
            }
            if (type == null) {
                return getErrorResult(ResultCode.PARAM_IS_INVALID, "弹幕类型不能为空");
            } else {
                if (!validDanmakuType(type)) {
                    return getErrorResult(ResultCode.PARAM_IS_INVALID, "弹幕类型错误");
                }
            }
            // init danmaku entity
            danmaku.setColor(color);
            danmaku.setText(text);
            danmaku.setDanmakuId(id);
            danmaku.setUsername(author);
            danmaku.setTime(time);
            danmaku.setType(type);
            danmaku.setUserId(uid);
        } catch (Exception e) {
            getErrorResult(ResultCode.DATA_IS_WRONG, "发送的数据类型有误");
        }

        String ipAddress = GeneralUtils.getIpAddress(request);
        danmaku.setIpAddress(ipAddress);
        danmaku.setUserId(uid);
        danmaku.setCreateTime(new Timestamp(System.currentTimeMillis()));
        danmaku.setReferer(GeneralUtils.getReferer(request));

        danmakuService.saveDanmaku(danmaku);
        return getSuccessResult(requestMap);
    }

    private boolean hasText(String string) {
        return StringUtils.hasText(string);
    }

    private boolean validDanmakuType(Integer type) {
        for (int i : danmakuTypeHolder) {
            if (i == type) {
                return true;
            }
        }
        return false;
    }
}
