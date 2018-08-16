package cc.dmji.api.exception;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@RestControllerAdvice
public class GlobalExceptinoHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptinoHandler.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e){
        logger.info("非运行时异常，"+e.getMessage()+","+e.getMessage());
        Result<String> result = null;
        result = new Result<>(ResultCode.SYSTEM_INTERNAL_ERROR,e.getMessage());
        return result;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result> handleRuntimeException(RuntimeException e){
        logger.info("运行时异常，"+e.getMessage()+","+e.getClass().getName());
        Result<String> result = new Result<>();
        if(e instanceof JDBCConnectionException){
            result = new Result<>(ResultCode.DATABASE_NOT_CONNECTED,e.getMessage());
        }
        else if (e instanceof AccessDeniedException){
            logger.info("AccessDeniedException");
            result = new Result<>(ResultCode.PERMISSION_DENY,e.getMessage());
            return new ResponseEntity<Result>(result,HttpStatus.FORBIDDEN);
        }
        result.setData(e.getMessage());
        result.setResultCode(ResultCode.SYSTEM_INTERNAL_ERROR);
        return new ResponseEntity<Result>(result,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
