package com.zmy.sys_common.entity;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/1/2
 * Time: 14:31
 * Description:
 *          /**
 *  * 数据响应对象
 *      {
 *        success ：是否成功
 *        code    ：返回码
 *        message ：返回信息
 *        //返回数据
 *       data：  ：{}
 *      }
 *
 */
@Data
public class Result {
    private boolean success;//是否成功
    private Integer code;// 返回码
    private String message;//返回信息
    private Object data;// 返回数据
    private Date date;

    public static Result error() {
        Result result = new Result();
        result.setSuccess(ResultCode.FAIL.isSuccess());
        result.setCode(ResultCode.FAIL.getCode());
        result.setMessage(ResultCode.FAIL.getMessage());
        result.setDate(new Date());
        return result;
    }
    public static Result error(String message) {
        Result result = new Result();
        result.setSuccess(ResultCode.FAIL.isSuccess());
        result.setCode(ResultCode.FAIL.getCode());
        result.setMessage(message);
        result.setDate(new Date());
        return result;
    }
    public static Result error(ResultCode resultEnum) {
        Result result = new Result();
        result.setSuccess(resultEnum.isSuccess());
        result.setCode(resultEnum.getCode());
        result.setMessage(resultEnum.getMessage());
        result.setDate(new Date());
        return result;
    }


    public static Result error(ResultCode resultEnum, Object data) {
        Result result = new Result();
        result.setSuccess(resultEnum.isSuccess());
        result.setCode(resultEnum.getCode());
        result.setMessage(resultEnum.getMessage());
        result.setData(data);
        result.setDate(new Date());
        return result;
    }

    public static Result error(Integer code, String message) {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        result.setDate(new Date());
        return result;
    }

    public static  Result success(Object o) {
        Result result = new Result();
        result.setSuccess(ResultCode.SUCCESS.isSuccess());
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(o);
        result.setDate(new Date());
        return result;
    }

    public static Result success() {
        return success(null);
    }
}
