package com.zmy.sys_moudle.lucky.entity.lucky;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/3/15
 * Time: 15:37
 * Description:
 */
@Data
@Entity(name = "role")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
}
