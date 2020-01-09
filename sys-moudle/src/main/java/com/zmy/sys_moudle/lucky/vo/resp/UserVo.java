package com.zmy.sys_moudle.lucky.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zmy.sys_moudle.lucky.entity.Role;
import com.zmy.sys_moudle.lucky.entity.User;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/1/3
 * Time: 11:09
 * Description:
 */
@Data
public class UserVo  {
    private String id;
    private String mobile;
    private String username;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 启用状态 0为禁用 1为启用
     */
    private String state;
    /**
     * 所属项目
     */
    private String department;
    private String remark;
    private Set<RoleVo> roles;
    private Date updateTime;
    private Date createTime;
}
