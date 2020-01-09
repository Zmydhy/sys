package com.zmy.sys_common.exception;

import com.zmy.sys_common.entity.ResultCode;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/1/2
 * Time: 15:37
 * Description:
 */

@Getter
public class CommonExp extends Exception {
    private ResultCode resultCode;

    public CommonExp(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}
