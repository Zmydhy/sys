package com.zmy.sys_moudle.lucky.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zmy.sys_moudle.lucky.entity.Permission;
import com.zmy.sys_moudle.lucky.entity.Role;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/1/3
 * Time: 8:26
 * Description:
 */
@Data
public class RoleVo {
    private String id;
    private String name;
    private String department;
    private String description;
    private Set<Permission> permissions;
    private Date updateTime;
    private Date createTime;


}
