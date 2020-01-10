package com.zmy.sys_moudle.lucky.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/1/3
 * Time: 8:26
 * Description:
 */
@Data
@Entity(name = "user")
public class User  {
    @Id
    private String id;



    private String mobile;

    @NotBlank(message = "账号不能为空")
    private String username;
    /**
     * 昵称
     */
    private String nickname;
    @NotBlank(message = "密码不能为空")
    private String password;

    private String salt;
    /**
     * 启用状态 0为禁用 1为启用
     */
    private String state;
    /**
     * 所属项目
     */
    private String department;
    private String remark;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;



}
