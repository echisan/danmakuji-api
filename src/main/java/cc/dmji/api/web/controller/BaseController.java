package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by echisan on 2018/5/16
 */
public class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    public Result getSuccessResult() {
        return new Result<>().setResultCode(ResultCode.SUCCESS);
    }

    public <T> Result<T> getSuccessResult(T data) {
        Result<T> result = new Result<>();
        result.setResultCode(ResultCode.SUCCESS);
        result.setData(data);
        return result;
    }

    public Result getSuccessResult(String msg){
        Result result = new Result().setResultCode(ResultCode.SUCCESS);
        result.setMsg(msg);
        return result;
    }

    public <T> Result<T> getSuccessResult(T data, String msg){
        Result<T> result = getSuccessResult(data);
        result.setMsg(msg);
        return result;
    }

    public Result getErrorResult(ResultCode resultCode, String msg){
        Result result = new Result();
        result.setResultCode(resultCode);
        result.setMsg(msg);
        return result;
    }

    public <T> Result<T> getErrorResult(ResultCode resultCode, String msg, T data) {
        Result<T> result = new Result<>();
        result.setResultCode(resultCode);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public Result getErrorResult(ResultCode resultCode){
        return new Result().setResultCode(resultCode);
    }

    public ResponseEntity<Result> getResponseEntity(HttpStatus httpStatus, Result result){
        return new ResponseEntity<>(result,httpStatus);
    }

}
