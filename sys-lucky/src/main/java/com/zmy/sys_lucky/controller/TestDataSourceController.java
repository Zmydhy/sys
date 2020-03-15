package com.zmy.sys_lucky.controller;

import com.alibaba.druid.pool.DruidDataSource;
import com.zmy.sys_common.entity.Result;
import com.zmy.sys_lucky.config.DynamicDataSource;
import com.zmy.sys_lucky.dao.RoleDao;
import com.zmy.sys_lucky.dao.lucky.RolesDao;
import com.zmy.sys_moudle.lucky.entity.Role;
import com.zmy.sys_moudle.lucky.entity.lucky.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/3/15
 * Time: 15:16
 * Description:
 */
@RestController
public class TestDataSourceController {

    @Autowired
    RolesDao rolesDao;

    @Autowired
    RoleDao roleDao;

    @GetMapping("/change")
    public  Result changeDataSource(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl("jdbc:mysql://192.168.0.123:3306/lucky?characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&useAffectedRows=true");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("root");
        DynamicDataSource.dataSourcesMap.put("dbkey", druidDataSource);
        DynamicDataSource.setDataSource("dbkey");
        Roles role = rolesDao.findRoleByIdEquals(1);
        if (role != null){
            DynamicDataSource.clear();
            return  Result.success(role);
        }else {
            DynamicDataSource.clear();
            return Result.error();
        }

//        此时数据源已切换到druidDataSource ，调用自己的业务方法即可。
//        使用完后调用DynamicDataSource.clear();重置为默认数据源。
    }

    @GetMapping("/role/{id}")
    public Result getRole(@PathVariable String id){
        Role role = roleDao.findRoleByIdEquals(id);
        if (role != null){
            return  Result.success(role);
        }else {
            return Result.error();
        }
    }
}
