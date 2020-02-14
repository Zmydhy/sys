package com.zmy.sys_lucky.controller;

import com.zmy.sys_common.ApiPermission;
import com.zmy.sys_common.entity.Result;
import com.zmy.sys_common.exception.CommonExp;
import com.zmy.sys_lucky.service.DepartmentService;
import com.zmy.sys_lucky.service.RoleService;
import com.zmy.sys_moudle.lucky.entity.Role;
import com.zmy.sys_moudle.lucky.vo.resp.RoleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/1/3
 * Time: 9:42
 * Description:
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    RoleService roleService;

    @Autowired
    DepartmentService departmentService;

    @PostMapping
    @ApiPermission(name = "api:addrole")
    public Result addRole(@RequestBody Role role) throws CommonExp {
        return Result.success(getRoleVo(roleService.save(role)));
    }

    @GetMapping
    public Result queryRole(@RequestParam String roleId) throws CommonExp {
        return Result.success(getRoleVo(roleService.findById(roleId)));
    }

    @GetMapping("/list")
    public Result queryRoleList(@RequestParam String depart) throws CommonExp {
        return Result.success((roleService.getList(depart)));
    }

    @PutMapping
    @ApiPermission(name = "api:updaterole")
    public Result updateRole(@RequestBody Role role) throws CommonExp {
        Role dbrole = roleService.update(role);
        return Result.success(getRoleVo(dbrole));
    }

    @DeleteMapping
    @ApiPermission(name = "api:deleterole")
    public Result deleteRole(@RequestParam String roleId) throws CommonExp {
        roleService.deleteRole(roleId);
        return Result.success();
    }

    /**
     * 赋予某角色管理员权限
     * @return
     * @throws CommonExp
     */
    @GetMapping("/assign_manger_permission")
    public Result assginMangerPermission(@RequestParam String roleId) throws CommonExp {
        roleService.assignPermissions(roleId);
        return Result.success();
    }

    /**
     * 分配权限
     */
    @PostMapping("/assign_permission")
    @ApiPermission(name = "api:assignpermission")
    public Result assignPermissions(@RequestBody Map<String,Object> map) throws CommonExp {
        //1.获取被分配的用户id
        String roleId = (String) map.get("id");
        //2.获取到角色的id列表
        List<String> permissionIds = (List<String>) map.get("permissionIds");
        //3.调用service完成角色分配
        RoleVo roleVo =  roleService.assignPermission(roleId,permissionIds);
        return Result.success(roleVo);
    }


    private RoleVo getRoleVo(Role role) {
        RoleVo roleVo = new RoleVo();
        BeanUtils.copyProperties(role, roleVo);
        return roleVo;
    }
}
