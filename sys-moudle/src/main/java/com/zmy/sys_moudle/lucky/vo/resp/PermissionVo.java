package com.zmy.sys_moudle.lucky.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/1/3
 * Time: 8:35
 * Description:
 */
@Data
public class PermissionVo {

    private String id;
    private String name;
    private String code;
    /**
     * 权限类型 1为菜单 2为功能 3为API
     */
    private int type;
    private String parentId;
    private String department;
    /**
     * 权限描述
     */
    private String description;
    private Date updateTime;
    private Date createTime;


}
