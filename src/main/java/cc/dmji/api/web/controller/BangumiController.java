package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.service.BangumiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.ConnectException;
import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/bangumis")
public class BangumiController extends BaseController{

    @Autowired
    BangumiService bangumiService;

    @GetMapping
    public Result listBangumis(@RequestParam(required = false) String bangumiName) throws ConnectException {
        List<Bangumi> bangumis = null;
        if(null == bangumiName || bangumiName.equals("")){
            bangumis = bangumiService.listBangumis();
        }
        else {
            bangumis = bangumiService.listBangumisByName(bangumiName);
        }
        if(bangumis.size()==0){
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        else {
            return getSuccessResult(bangumis);
        }
    }

    @GetMapping("/{id}")
    public Result getBangumiByBangumiId(@PathVariable Integer id){
        Bangumi bangumi = bangumiService.getBangumiById(id);
        if(null == bangumi){
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        return getSuccessResult(bangumi);
    }

    @PostMapping
    public Result addBangumi(@RequestBody Bangumi bangumi){
        if(null == bangumi.getBangumiName() || bangumi.getBangumiName().equals("")){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"BangumiName不能为空");
        }
        if(null == bangumi.getEpisodeTotal() || bangumi.getEpisodeTotal()<0){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"集数不能为负数");
        }
        if(null != bangumi.getBangumiId()){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"非法参数(插入数据不需要提供id)");
        }
        // TODO 图片链接验证

        Bangumi insertedBangumi = null;
        insertedBangumi = bangumiService.insertBangumi(bangumi);
        if(null == insertedBangumi){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"添加番剧信息失败");
        }
        else {
            return getSuccessResult(insertedBangumi);
        }
    }

    @PutMapping("/{bangumiId}")
    public Result editBangumi(@PathVariable("bangumiId") Integer bangumiId, @RequestBody Bangumi bangumi){

        Bangumi editedBangumi = bangumiService.getBangumiById(bangumiId);
        if(editedBangumi == null){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"BangumiId不存在");
        }
        if(null != bangumi.getBangumiName()){
            if(bangumi.getBangumiName().equals("")){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"BangumiName不能为空");
            }
            else {
                editedBangumi.setBangumiName(bangumi.getBangumiName());
            }
        }
        if(null != bangumi.getEpisodeTotal()){
            if(bangumi.getEpisodeTotal() < 0){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"番剧集数不能为空或负数");
            }
            else {
                editedBangumi.setEpisodeTotal(bangumi.getEpisodeTotal());
            }
        }
        // TODO 图片链接验证
        if(bangumi.getThumb() == null || bangumi.getThumb().equals("")){}
        else {
            editedBangumi.setThumb(bangumi.getThumb());
        }
        editedBangumi.setModifyTime(new Timestamp(System.currentTimeMillis()));
        editedBangumi = bangumiService.updateBangumi(editedBangumi);
        if(null == editedBangumi){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"更新番剧信息失败");
        }
        else {
            return getSuccessResult(editedBangumi);
        }
    }

    @DeleteMapping("/{bangumiId}")
    public Result deleteBangumi(@PathVariable Integer bangumiId){
        Bangumi deletedBangumi = bangumiService.getBangumiById(bangumiId);
        if(null == deletedBangumi){
            return getErrorResult(ResultCode.PARAM_IS_INVALID,"删除番剧失败");
        }
        else {
            bangumiService.deleteBangumiById(bangumiId);
            return getSuccessResult(deletedBangumi);
        }
    }
}
