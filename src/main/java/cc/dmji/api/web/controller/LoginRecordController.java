package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.LoginRecord;
import cc.dmji.api.service.LoginRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loginrecords")
public class LoginRecordController extends BaseController {

    @Autowired
    LoginRecordService loginRecordService;

    @GetMapping
    public Result listLoginRecords(@RequestParam(required = false) Long userId,
                                   @RequestParam(required = false) String ip){
        List<LoginRecord> result = null;
        if(null == userId && null ==ip){
            //参数为空，则默认查找所有登陆记录
            result = loginRecordService.listLoginRecords();
        }
        else {
            if(null == userId){
                result = loginRecordService.listLoginRecordsByIp(ip);
            }
            else if(null == ip){
                result = loginRecordService.listLoginRecordsByUserId(userId);
            }
            else {
                result = loginRecordService.listLoginRecordsByUserIdAndIp(userId,ip);
            }
        }
        if(result.size() == 0){
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        else {
            return getSuccessResult(result);
        }
    }

    @GetMapping("/{recordId}")
    public Result getLoginRecordById(@PathVariable Long recordId){
        LoginRecord loginRecord = loginRecordService.getLoginRecordByRecordId(recordId);
        if(null == loginRecord){
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        else {
            return getSuccessResult(loginRecord);
        }
    }

    @PostMapping()
    public Result addLoginRecord(@RequestBody LoginRecord loginRecord){
        LoginRecord insertedloginRecord = loginRecordService.insertLoginRecord(loginRecord);
        if(null == insertedloginRecord){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"新增登陆记录失败");
        }
        else {
            return getSuccessResult(insertedloginRecord);
        }
    }

    @DeleteMapping("/{recordId}")
    public Result deleteRecordId(@PathVariable Long recordId){
        LoginRecord loginRecord = loginRecordService.getLoginRecordByRecordId(recordId);
        if(null == loginRecord){
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND,"删除登陆记录失败");
        }
        else {
            return getSuccessResult(loginRecord);
        }
    }
}
