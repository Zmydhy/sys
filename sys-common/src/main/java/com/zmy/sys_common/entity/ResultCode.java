package com.zmy.sys_common.entity;

/**
 * 公共的返回码
 * 返回码code：
 * 成功：10000
 * 失败：10001
 * 未登录：10002
 * 未授权：10003
 * 抛出异常：99999
 */
public enum ResultCode {
    //---系统返回码  1xxxx-----
    SUCCESS(true, 10000, "操作成功！"),
    FAIL(false, 10001, "操作失败"),
    UNAUTHORISE(false, 10003, "权限不足"),
    EXPIREDUNAUTHORISE(false, 10004, "token已过期，重新签发"),
    NOPWOERDUNAUTHORISE(false, 10005, "无效token，重新签发"),
    REDISNOAUTHORISE(false, 10006, "校检token无效，重新签发"),
    SERVER_ERROR(false, 99999, "抱歉，系统繁忙，请稍后重试！"),

    //---用户操作返回码  2xxxx----
    USERNAMEORPASSWORDERROR(false, 20001, "用户名或密码错误"),

    USERNOEXIST(false, 20002, "该用户不存在 "),
    USERALEADYEXIST(false, 20003, "该用户已存在 "),
    DEPARTMENTNOEXIST(false, 20004, "该项目不存在 "),
    DEPARTMENTALEADYEXIST(false, 20005, "该项目已存在 "),
    ADMINNOEXIST(false, 20006, "管理员账号不存在 "),
    ADMINLEADYEXIST(false, 20007, "默认管理员账号已存在 ，请指定用户名"),
    ROLENOEXIST(false, 20008, "该角色不存在 "),
    ROLEALEADYEXIST(false, 20009, "该角色已存在 "),
    PERMISSIONNOEXIST(false, 20010, "该权限不存在 "),
    PERMISSIONALEADYEXIST(false, 20011, "该权限已存在 ");

    //---企业操作返回码  3xxxx----
    //---权限操作返回码----
    //---其他操作返回码----

    //操作是否成功
    private boolean success;
    //操作代码
    private int code;
    //提示信息
    private String message;

    ResultCode(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
