package com.zmy.sys_moudle.lucky.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/1/3
 * Time: 8:35
 * Description:
 */
@Data
public class DepartmentVo {

    private String id;
    private String name;
    private String  manager;
    private String description;
    private Date updateTime;
    private Date createTime;

}
