package cc.dmji.api.web.controller;

import cc.dmji.api.annotation.UserLog;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.entity.PostBangumi;
import cc.dmji.api.enums.PostBangumiOrderBy;
import cc.dmji.api.enums.PostBangumiStatus;
import cc.dmji.api.enums.Status;
import cc.dmji.api.service.BangumiService;
import cc.dmji.api.service.PostBangumiService;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.GeneralUtils;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.web.model.UserPostBangumi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
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

    @GetMapping()
    @UserLog("获取个人提交番剧列表")
    public ResponseEntity<Result> listUserPostBangumis(HttpServletRequest request,
                                                       @RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn,
                                                       @RequestParam(value = "ps", required = false, defaultValue = "20") Integer ps,
                                                       @RequestParam(value = "sc", required = false, defaultValue = "0") Integer statusCode,
                                                       // postBangumiStatus code
                                                       @RequestParam(value = "pbsc", required = false) Integer pbsc,
                                                       // postBangumiOrderBy Code
                                                       @RequestParam(value = "pc", required = false, defaultValue = "0") Integer pc,
                                                       // Direction Code,
                                                       // 0 : ASC
                                                       // 1 : DESC
                                                       @RequestParam(value = "dc", required = false, defaultValue = "1") Integer dc,
                                                       @RequestParam(value = "bn", required = false) String bangumiName) {

        if (DmjiUtils.validatePageParam(pn, ps) != 5) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "页码，页数参数不正确");
        }

        Status status = null;

        if (statusCode != null) {
            status = Status.byCode(statusCode);
            if (status == null) {
                return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "sc参数不正确:" + statusCode);
            }
        }

        PostBangumiStatus postBangumiStatus = null;
        if (pbsc != null) {
            try {
                postBangumiStatus = PostBangumiStatus.byCode(pbsc);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "pbs参数不正确:" + pbsc);
            }
        }

        PostBangumiOrderBy postBangumiOrderBy = PostBangumiOrderBy.byCode(pc);
        if (postBangumiOrderBy == null) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "pc参数不正确:" + pc);
        }

        if (!dc.equals(0) && !dc.equals(1)) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "dc参数不正确:" + dc);
        }

        Sort.Direction direction = null;
        if (dc.equals(0)) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        Long userId = getUidFromRequest(request);
        Page<PostBangumi> postBangumiPage = null;

        if (postBangumiStatus == null) {
            if (!StringUtils.hasText(bangumiName)) {
                postBangumiPage = postBangumiService.listByUserId(userId, pn, ps, status, Sort.by(direction, postBangumiOrderBy.name()));
            } else {
                postBangumiPage = postBangumiService.listByBangumiName(userId, pn, ps, bangumiName, status);
            }
        } else {
            if (!StringUtils.hasText(bangumiName)) {
                postBangumiPage = postBangumiService.listByUserId(userId, pn, ps, postBangumiStatus, status, Sort.by(direction, postBangumiOrderBy.name()));
            } else {
                postBangumiPage = postBangumiService.listPostBangumiByBangumiName(userId,
                        bangumiName, pn, ps, postBangumiStatus, status, Sort.by(direction, postBangumiOrderBy.name()));
            }
        }

        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNumber(pn);
        pageInfo.setPageSize(ps);
        pageInfo.setTotalSize(postBangumiPage.getTotalElements());

        List<PostBangumi> postBangumiList = postBangumiPage.getContent();
        List<UserPostBangumi> userPostBangumiList = new ArrayList<>();
        postBangumiList.forEach(postBangumi -> userPostBangumiList.add(new UserPostBangumi(postBangumi)));

        Map<String, Object> map = new HashMap<>(2);
        map.put("page", pageInfo);
        map.put("postBangumi", userPostBangumiList);

        return getResponseEntity(HttpStatus.OK, getSuccessResult(map));
    }

    @PostMapping
    @UserLog("提交番剧信息")
    public ResponseEntity<Result> postBangumi(@RequestBody Map<String, String> requestMap,
                                              HttpServletRequest request) {
        String bangumiName = requestMap.get("bangumiName");
        String episodeTotalString = requestMap.get("episodeTotal");
        String hasZeroIndexString = requestMap.get("hasZeroIndex");
        String thumb = requestMap.get("thumb");

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
        Long userId = getUidFromRequest(request);
        if (episodeTotal < 0) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "总集数不能小于0");
        }

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

        if (postBangumiList.size() != 0) {
            return getErrorResponseEntity(HttpStatus.OK, ResultCode.DATA_ALREADY_EXIST, "该番剧已经有小伙伴提交了,不用再提交啦");
        }

        String msg = "感谢dalao的提交,后续结果请留意系统通知";


        pb.setBangumiName(bangumiName);
        pb.setHasZeroIndex(hasZeroIndex);
        pb.setEpisodeTotal(episodeTotal);
        pb.setUserId(userId);
        pb.setStatus(Status.NORMAL);
        pb.setManagerUserId(null);
        pb.setIsShow(SHOW);
        pb.setPostBangumiStatus(PostBangumiStatus.PENDING);
        pb.setMessage("");
        pb.setThumb(GeneralUtils.cleanXSS(thumb));

        PostBangumi insertPostBangumi = postBangumiService.insertPostBangumi(pb);
        logger.debug("插入postBangumi成功,{}", insertPostBangumi);
        return getResponseEntity(HttpStatus.OK, getSuccessResult(new UserPostBangumi(insertPostBangumi), msg));
    }

    @PutMapping
    @UserLog("更新番剧信息")
    public ResponseEntity<Result> updatePostBangumi(HttpServletRequest request,
                                                    @RequestBody Map<String, String> requestMap) {

        String idString = requestMap.get("id");
        String bangumiNameString = requestMap.get("bn");
        String episodeTotalString = requestMap.get("et");
        String hasZeroIndexString = requestMap.get("hzi");
        String thumb = requestMap.get("thumb");


        Long userId = getUidFromRequest(request);
        if (userId == null) {
            return getErrorResponseEntity(HttpStatus.UNAUTHORIZED, ResultCode.PERMISSION_DENY, "请先登陆");
        }

        if (!StringUtils.hasText(idString)) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "id不能为空");
        }
        if (!StringUtils.hasText(bangumiNameString)) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "番剧名不能为空");
        }
        if (episodeTotalString == null) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "总集数不能为空");
        }
        if (hasZeroIndexString == null) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "到底有没有第0集呢");
        }
        if (!DmjiUtils.isPositiveNumber(episodeTotalString)) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "总集数不能写奇奇怪怪的东西,只接受正整数");
        }
        if (!hasZeroIndexString.equals("1") && !hasZeroIndexString.equals("0")) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "hasZeroIndex 只接受参数'0'或'1'");
        }

        String bangumiName = GeneralUtils.cleanXSS(bangumiNameString);
        if (!StringUtils.hasText(bangumiName)) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "大佬求放过QAQ");
        }

        Integer hasZeroIndex = Integer.valueOf(hasZeroIndexString);
        Integer episodeTotal = Integer.valueOf(episodeTotalString);
        Long id = Long.valueOf(idString);

        PostBangumi postBangumi = postBangumiService.getById(id);
        if (postBangumi == null) {
            return getErrorResponseEntity(HttpStatus.NOT_FOUND, ResultCode.RESULT_DATA_NOT_FOUND);
        }
        // 如果操作的不是自己的资源
        if (!postBangumi.getUserId().equals(userId)) {
            return getErrorResponseEntity(HttpStatus.FORBIDDEN, ResultCode.PERMISSION_DENY);
        }

        postBangumi.setBangumiName(bangumiName);
        postBangumi.setHasZeroIndex(hasZeroIndex.byteValue());
        postBangumi.setEpisodeTotal(episodeTotal);
        postBangumi.setThumb(GeneralUtils.cleanXSS(thumb));
        postBangumi.setModifyTime(new Timestamp(System.currentTimeMillis()));

        PostBangumi updatePostBangumi = postBangumiService.updatePostBangumi(postBangumi);
        return getResponseEntity(HttpStatus.OK, getSuccessResult(new UserPostBangumi(updatePostBangumi)));

    }

    @PutMapping("/thumb")
    @UserLog("更新番剧封面")
    public ResponseEntity<Result> updatePostBangumiThumb(HttpServletRequest request,
                                                         @RequestBody Map<String, String> requestMap) {

        String idString = requestMap.get("id");
        String thumb = requestMap.get("thumb");

        if (!StringUtils.hasText(idString)) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "id不能为空");
        }
        if (!StringUtils.hasText(thumb)) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "thumb不能为空");
        }
        Long id = Long.valueOf(idString);
        Long userId = getUidFromRequest(request);
        if (userId == null) {
            return getErrorResponseEntity(HttpStatus.FORBIDDEN, ResultCode.PERMISSION_DENY);
        }

        PostBangumi postBangumi = postBangumiService.getById(id);
        if (postBangumi == null) {
            return getErrorResponseEntity(HttpStatus.NOT_FOUND, ResultCode.RESULT_DATA_NOT_FOUND);
        }
        if (!postBangumi.getUserId().equals(userId)) {
            return getErrorResponseEntity(HttpStatus.FORBIDDEN, ResultCode.PERMISSION_DENY);
        }

        postBangumi.setThumb(thumb);
        postBangumi.setPostBangumiStatus(PostBangumiStatus.AUDITING);
        postBangumi.setModifyTime(new Timestamp(System.currentTimeMillis()));
        PostBangumi updatePostBangumi = postBangumiService.updatePostBangumi(postBangumi);
        return getSuccessResponseEntity(getSuccessResult(new UserPostBangumi(updatePostBangumi)));
    }

    @DeleteMapping("/{pbId}")
    @UserLog("删除番剧信息")
    public ResponseEntity<Result> deletePostBangumi(HttpServletRequest request,
                                                    @PathVariable("pbId") Long pbId) {
        if (pbId == null || pbId < 0) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "pbid不能为空");
        }

        PostBangumi postBangumi = postBangumiService.getById(pbId);
        if (postBangumi == null) {
            return getErrorResponseEntity(HttpStatus.NOT_FOUND, ResultCode.RESULT_DATA_NOT_FOUND);
        }
        Long userId = getUidFromRequest(request);
        if (!postBangumi.getUserId().equals(userId)) {
            return getErrorResponseEntity(HttpStatus.FORBIDDEN, ResultCode.PERMISSION_DENY);
        }
        postBangumi.setStatus(Status.DELETE);
        postBangumi.setModifyTime(new Timestamp(System.currentTimeMillis()));
        PostBangumi updatePostBangumi = postBangumiService.updatePostBangumi(postBangumi);
        return getSuccessResponseEntity(getSuccessResult(new UserPostBangumi(updatePostBangumi)));
    }
}