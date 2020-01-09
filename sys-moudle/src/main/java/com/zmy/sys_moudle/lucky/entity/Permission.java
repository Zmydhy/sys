package com.zmy.sys_moudle.lucky.entity;

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
@Entity(name = "permission")
public class Permission {
    @Id
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

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


}
