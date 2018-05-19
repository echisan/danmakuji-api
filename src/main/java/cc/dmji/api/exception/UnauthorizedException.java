package cc.dmji.api.exception;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;

/**
 * Created by echisan on 2018/5/18
 */
public class UnauthorizedException extends RuntimeException {

    private Result<Void> result;

    public UnauthorizedException() {
    }

    public UnauthorizedException(String msg) {
        this(msg, ResultCode.PERMISSION_DENY);
    }

    public UnauthorizedException(ResultCode resultCode) {
        this(resultCode.getMsg(), resultCode);
    }

    public UnauthorizedException(String message, ResultCode resultCode) {
        super(message);
        result = new Result<>();
        result.setResultCode(resultCode);
        result.setMsg(message);
    }

    public Result<Void> getResult() {
        return result;
    }
}
