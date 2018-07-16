package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.entity.PostBangumi;
import cc.dmji.api.enums.PostBangumiStatus;
import cc.dmji.api.enums.Status;
import cc.dmji.api.service.BangumiService;
import cc.dmji.api.service.PostBangumiService;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.GeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static cc.dmji.api.constants.PostBangumiConstants.SHOW;

/**
 * Created by echisan on 2018/7/12
 */
@RestController
@RequestMapping("/postBangumis")
public class PostBangumiController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(PostBangumiController.class);

    @Autowired
    private PostBangumiService postBangumiService;

    @Autowired
    private BangumiService bangumiService;

    @PostMapping
    public ResponseEntity<Result> postBangumi(@RequestBody Map<String, String> requestMap,
                                              HttpServletRequest request) {
        String bangumiName = requestMap.get("bangumiName");
        String episodeTotalString = requestMap.get("episodeTotal");
        String hasZeroIndexString = requestMap.get("hasZeroIndex");

        if (!StringUtils.hasText(bangumiName)) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "番剧名不能为空");
        }
        if (!StringUtils.hasText(episodeTotalString)) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "总集数不能为空");
        }
        if (!StringUtils.hasText(hasZeroIndexString)) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "到底有没有第0集呢");
        }
        if (!DmjiUtils.isPositiveNumber(episodeTotalString)) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "总集数不能写奇奇怪怪的东西,只接受正整数");
        }
        if (!hasZeroIndexString.equals("1") && !hasZeroIndexString.equals("0")) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "hasZeroIndex 只接受参数'0'或'1'");
        }

        Integer episodeTotal = Integer.valueOf(episodeTotalString);
        Byte hasZeroIndex = Byte.valueOf(hasZeroIndexString);
        String userId = getUidFromToken(request);
//        if (episodeTotal < 0) {
//            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "总集数不能小于0");
//        }

        PostBangumi pb = new PostBangumi();

        bangumiName = GeneralUtils.cleanXSS(bangumiName);
        if (!StringUtils.hasText(bangumiName)) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "大佬求放过QAQ");
        }

        Bangumi bangumi = bangumiService.getBangumiByName(bangumiName);
        if (bangumi != null) {
            return getErrorResponseEntity(HttpStatus.OK, ResultCode.DATA_ALREADY_EXIST, "该番剧信息已经存在,不用再提交啦");
        }

        List<PostBangumi> postBangumiList = postBangumiService.listByBangumiName(bangumiName);

        Result result = getSuccessResult();
        result.setMsg("感谢dalao的提交,后续结果请留意系统通知");
        if (postBangumiList.size() != 0) {
            String msg = "该番剧已经有小伙伴提交了,具体采用哪一个请留意后续通知";
            result.setMsg(msg);
        }

        pb.setBangumiName(bangumiName);
        pb.setHasZeroIndex(hasZeroIndex);
        pb.setEpisodeTotal(episodeTotal);
        pb.setUserId(userId);
        pb.setStatus(Status.NORMAL);
        pb.setManagerUserId("");
        pb.setIsShow(SHOW);
        pb.setPostBangumiStatus(PostBangumiStatus.PENDING);
        pb.setMessage("");
        pb.setThumb("");

        PostBangumi insertPostBangumi = postBangumiService.insertPostBangumi(pb);
        logger.debug("插入postBangumi成功,{}", insertPostBangumi);

        return getResponseEntity(HttpStatus.OK, result);
    }
}
