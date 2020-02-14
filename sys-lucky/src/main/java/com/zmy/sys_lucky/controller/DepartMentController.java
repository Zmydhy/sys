package com.zmy.sys_lucky.controller;

import com.zmy.sys_common.ApiPermission;
import com.zmy.sys_common.entity.Result;
import com.zmy.sys_common.exception.CommonExp;
import com.zmy.sys_lucky.service.DepartmentService;
import com.zmy.sys_lucky.service.UserService;
import com.zmy.sys_moudle.lucky.entity.Department;
import com.zmy.sys_moudle.lucky.entity.User;
import com.zmy.sys_moudle.lucky.vo.resp.DepartmentVo;
import com.zmy.sys_moudle.lucky.vo.resp.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/1/3
 * Time: 9:42
 * Description:
 */
@RestController
@RequestMapping("/department")
public class DepartMentController {
    @Autowired
    DepartmentService departmentService;

    @PostMapping
    @ApiPermission(name = "api:adddepartment")
    public Result addDepartment(@RequestBody Department department) throws CommonExp {
        return Result.success(getDepartmentVo(departmentService.save(department)));
    }

    @GetMapping
    public Result queryDepartment(@RequestParam String departmentId) throws CommonExp {
        return Result.success(getDepartmentVo(departmentService.findById(departmentId)));
    }
    @GetMapping("/list")
    public Result queryDepartmentList() throws CommonExp {
        return Result.success((departmentService.getList()));
    }

    @PutMapping
    @ApiPermission(name = "api:updatedepartment")
    public Result updateDepartment(@RequestBody Department department) throws CommonExp {
        Department dbdepartment = departmentService.update(department);
        return Result.success(getDepartmentVo(dbdepartment));
    }

    @DeleteMapping
    @ApiPermission(name = "api:deletedepartment")
    public Result deleteDepartment(@RequestParam String departmentId) throws CommonExp {
        departmentService.deleteDepartment(departmentId);
        return Result.success();
    }

    private DepartmentVo getDepartmentVo(Department department) {
        DepartmentVo departmentVo = new DepartmentVo();
        BeanUtils.copyProperties(department, departmentVo);
        return departmentVo;
    }
}
