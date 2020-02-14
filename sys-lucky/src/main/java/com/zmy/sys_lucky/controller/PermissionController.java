package com.zmy.sys_lucky.controller;

import com.zmy.sys_common.ApiPermission;
import com.zmy.sys_common.entity.Result;
import com.zmy.sys_common.exception.CommonExp;
import com.zmy.sys_lucky.service.PermissionService;
import com.zmy.sys_lucky.service.PermissionService;
import com.zmy.sys_moudle.lucky.entity.Permission;
import com.zmy.sys_moudle.lucky.vo.resp.PermissionVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Permission: zmy
 * Date: 2020/1/3
 * Time: 9:42
 * Description:
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    PermissionService permissionService;

    @PostMapping
    @ApiPermission(name = "api:addpermission")
    public Result addPermission(@RequestBody Map<String,Object> map) throws Exception {
        return Result.success(getPermissionVo(permissionService.save(map)));
    }

    @GetMapping
    public Result queryPermission(@RequestParam String permissionId) throws CommonExp {
        return Result.success(getPermissionVo(permissionService.findById(permissionId)));
    }

    @GetMapping("/list")
    public Result queryPermissionList(@RequestParam String depart) throws CommonExp {
        return Result.success((permissionService.getList(depart)));
    }

    @PutMapping
    @ApiPermission(name = "api:updatepermission")
    public Result updatePermission(@RequestBody Permission permission) throws CommonExp {
        Permission dbpermission = permissionService.update(permission);
        return Result.success(getPermissionVo(dbpermission));
    }

    @DeleteMapping
    @ApiPermission(name = "api:deletepermission")
    public Result deletePermission(@RequestParam String permissionId) throws CommonExp {
        permissionService.deletePermission(permissionId);
        return Result.success();
    }

    private PermissionVo getPermissionVo(Permission permission) {
        PermissionVo permissionVo = new PermissionVo();
        BeanUtils.copyProperties(permission, permissionVo);
        return permissionVo;
    }
}
