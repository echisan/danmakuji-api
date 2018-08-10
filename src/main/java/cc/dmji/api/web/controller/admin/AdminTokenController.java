package cc.dmji.api.web.controller.admin;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.User;
import cc.dmji.api.service.OnlineUserRedisService;
import cc.dmji.api.service.RedisTokenService;
import cc.dmji.api.service.UserService;
import cc.dmji.api.utils.JwtTokenUtils;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.web.controller.BaseController;
import cc.dmji.api.web.model.admin.DeleteToken;
import cc.dmji.api.web.model.admin.LoginTokenInfo;
import cc.dmji.api.web.model.admin.LoginUserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by echisan on 2018/6/8
 */
@RestController
@RequestMapping("/admin/tokens")
public class AdminTokenController extends BaseController {

    @Autowired
    private RedisTokenService redisTokenService;

    @Autowired
    private OnlineUserRedisService onlineUserRedisService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @GetMapping
    public ResponseEntity<Result> listLoginToken(@RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn,
                                                 @RequestParam(value = "ps", required = false, defaultValue = "20") Integer ps) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNumber(pn);
        pageInfo.setPageSize(ps);
        pageInfo.setTotalSize(redisTokenService.countTokens());
        List<String> tokens = redisTokenService.listTokens(pn, ps);
        List<LoginTokenInfo> loginTokenInfos = new ArrayList<>();
        tokens.forEach(t -> {
            JwtTokenUtils.Payload payload = jwtTokenUtils.getPayload(t);
            String nick = payload.getUsername();
            Long uid = payload.getUid();
            Date expAt = payload.getClaims().getExpiration();
            Date issuedAt = payload.getClaims().getIssuedAt();
            LoginTokenInfo loginTokenInfo = new LoginTokenInfo(uid, nick, issuedAt, expAt, t);
            loginTokenInfos.add(loginTokenInfo);
        });
        Map<String, Object> data = new HashMap<>();
        data.put("page", pageInfo);
        data.put("tokensInfo", loginTokenInfos);

        return getResponseEntity(HttpStatus.OK, getSuccessResult(data));
    }



    @DeleteMapping
    public ResponseEntity<Result> deleteToken(@RequestBody DeleteToken deleteToken) {
        if (redisTokenService.invalidToken(deleteToken.getToken()) != -1) {
            return getResponseEntity(HttpStatus.OK, getSuccessResult("删除成功"));
        }
        return getResponseEntity(HttpStatus.OK, getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND, "该token不存在，无法删除"));
    }

    @GetMapping("/size")
    public ResponseEntity<Result> countTokens() {
        Long size = redisTokenService.countTokens();
        Map<String, Object> data = new HashMap<>();
        data.put("size", size);
        return getResponseEntity(HttpStatus.OK, getSuccessResult(data));
    }

    @GetMapping("/{uid}")
    public ResponseEntity<Result> listUserTokens(@PathVariable("uid")Long uid){
        User user = userService.getUserById(uid);
        List<LoginUserToken> loginUserTokens = redisTokenService.listUserTokens(user.getNick());
        return getResponseEntity(HttpStatus.OK, getSuccessResult(loginUserTokens));
    }

}
