package com.zmy.sys_common.handler;


import com.zmy.sys_common.entity.Result;
import com.zmy.sys_common.entity.ResultCode;
import com.zmy.sys_common.exception.CommonExp;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常捕获
 */
@ControllerAdvice
public class ExceptionHandle {
    /**
     * 捕获异常，并且记录在logger中，否则异常都被捕获了就看不到了
     */

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result handle(Exception e) {
        if(e.getClass() == CommonExp.class) {
            return  Result.error(((CommonExp) e).getResultCode());
        }else if(e.getClass()== ExpiredJwtException.class){
            return  Result.error(ResultCode.EXPIREDUNAUTHORISE);
        }else if(e.getClass()== SignatureException.class){
            return  Result.error(ResultCode.NOPWOERDUNAUTHORISE);
        }else {
            return Result.error(ResultCode.FAIL,e.toString());
        }

    }


}
