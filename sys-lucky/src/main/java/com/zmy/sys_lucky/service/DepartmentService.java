package com.zmy.sys_lucky.service;

import com.zmy.jar_test.log.Log;
import com.zmy.sys_common.entity.ResultCode;
import com.zmy.sys_common.exception.CommonExp;
import com.zmy.sys_common.utils.IdWorker;
import com.zmy.sys_common.utils.encdec.PasswordUtils;
import com.zmy.sys_lucky.dao.DepartmentDao;
import com.zmy.sys_lucky.dao.DepartmentUserRelationDao;
import com.zmy.sys_lucky.dao.RoleDao;
import com.zmy.sys_lucky.dao.UserDao;
import com.zmy.sys_moudle.lucky.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/1/3
 * Time: 9:53
 * Description:
 */
@Service
public class DepartmentService {
    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    UserDao userDao;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    RoleDao roleDao;

    @Autowired
    DepartmentUserRelationDao departmentUserRelationDao;

    private String[] permissions = {"添加用户api", "删除用户api", "添加权限api", "删除权限api", "添加角色api", "删除角色api", "修改用户api", "修改权限api", "修改角色api", "分配角色api", "分配权限api"};

    /**
     * 1.保存用户
     */
    @Transactional
    public Department save(Department department) throws CommonExp {
        Department dbDepartment = departmentDao.findDepartmentByNameEquals(department.getName());
        if (dbDepartment == null) {
            //设置主键的值
            String id = IdWorker.getInstance().nextId();
            department.setId(id);
            department.setUpdateTime(new Date());
            department.setCreateTime(new Date());
            //设置管理员
            User admin = checkManager(department);
            department.setManager(admin.getUsername());
            //添加用户名和项目间关系
            addRelation(department, admin);
            //调用dao保存部门
            departmentDao.save(department);
            return department;
        } else {
            throw new CommonExp(ResultCode.DEPARTMENTALEADYEXIST);
        }

    }

    /**
     * 添加部门与用户之间的关系
     *
     * @param department
     * @param admin
     */
    private void addRelation(Department department, User admin) {
        DepartmentUserRelation departmentUserRelation = new DepartmentUserRelation();
        departmentUserRelation.setId(IdWorker.getInstance().nextId());
        departmentUserRelation.setDepartmentId(department.getId());
        departmentUserRelation.setUserId(admin.getId());
        departmentUserRelation.setUpdateTime(new Date());
        departmentUserRelation.setCreateTime(new Date());
        departmentUserRelationDao.save(departmentUserRelation);
    }

    /**
     * 默认添加管理员账号
     *
     * @param manager
     */
    private User checkManager(Department manager) throws CommonExp {

        User user = createUser(manager);
        User dbuser = userDao.findUserByUsernameEquals(user.getUsername());
        if (dbuser == null) {
            userDao.save(user);
            Role role = createRole(user);
            roleDao.save(role);
            assignRole(user, role);
            roleService.assignPermissions(role.getId());
            return user;
        } else {
            dbuser.setDepartment(user.getDepartment());
            dbuser.setUpdateTime(new Date());
            userDao.save(dbuser);

            Role role = createRole(user);
            roleDao.save(role);
            assignRole(user, role);
            roleService.assignPermissions(role.getId());
            return dbuser;
        }
    }

    /**
     * 赋予项目管理员权限
     *
     * @param role
     */
//    public void assignPermissions(Role role) {
//        List<String> lists = new ArrayList<>();
//        //TODO
//        try {
//            for (int i = 0; i < permissions.length; i++) {
//                Permission permission = permissionService.findByName(permissions[i], "lucky");
//                if (permission != null){
//                    lists.add(permission.getId());
//                }
//            }
//        } catch (CommonExp commonExp) {
//            commonExp.printStackTrace();
//        }
//        try {
//            roleService.assignPermission(role.getId(),lists);
//        } catch (CommonExp commonExp) {
//            commonExp.printStackTrace();
//        }
//
//    }


    /**
     * 赋予管理员角色
     */
    public void assignRole(User user, Role role) throws CommonExp {
        List<String> lists = new ArrayList<>();
        lists.add(role.getId());
        userService.assignRoles(user.getId(), lists);
    }

    /**
     * 创建项目管理员角色
     */
    public Role createRole(User user) {
        Role role = new Role();
        role.setId(IdWorker.getInstance().nextId());
        role.setName("msadmin");
        role.setDepartment(user.getDepartment());
        role.setDescription(user.getDepartment() + "项目管理员");
        role.setUpdateTime(new Date());
        role.setCreateTime(new Date());
        return role;
    }

    /**
     * 创建项目管理员
     *
     * @param manager
     * @return
     */
    private User createUser(Department manager) {
        User admin = new User();
        admin.setId(IdWorker.getInstance().nextId());
        if (manager.getManager() == null) {
            admin.setUsername(manager.getName() + "_admin");
        } else {
            admin.setUsername(manager.getManager());
        }
        String salt = PasswordUtils.getSalt();
        admin.setSalt(salt);
        String password = PasswordUtils.encode("666666", salt);
        admin.setPassword(password);
        admin.setState("1");
        admin.setDepartment(manager.getName());
        admin.setUpdateTime(new Date());
        admin.setCreateTime(new Date());
        return admin;
    }

    /**
     * 更新用户
     */
    public Department update(Department department) throws CommonExp {
        Department dbdepartment = departmentDao.findDepartmentByIdEquals(department.getId());
        if (dbdepartment != null) {
            Optional.ofNullable(department.getManager()).ifPresent(new Consumer<String>() {
                @Override
                public void accept(String s) {

                    User user = userDao.findUserByUsernameEquals(department.getManager());
                    if (user != null) {
                        dbdepartment.setManager(s);
                    } else {
                        try {
                            throw new CommonExp(ResultCode.DEPARTMENTNOEXIST);
                        } catch (CommonExp commonExp) {
                            Log.warn(commonExp.getMessage());
                        }
                    }
                }
            });
            Optional.ofNullable(department.getDescription()).ifPresent((s) -> dbdepartment.setDescription(s));
            dbdepartment.setUpdateTime(new Date());
            //调用dao保存部门
            departmentDao.save(dbdepartment);
            return dbdepartment;
        } else {
            throw new CommonExp(ResultCode.DEPARTMENTNOEXIST);
        }
    }

    /**
     * 3.根据id查询用户
     */
    public Department findById(String id) throws CommonExp {
        Department dbdepartment = departmentDao.findDepartmentByIdEquals(id);
        if (dbdepartment != null) {
            return departmentDao.findById(id).get();
        } else {
            throw new CommonExp(ResultCode.DEPARTMENTNOEXIST);
        }
    }

    public List<Department> getList() throws CommonExp {
        List<Department> dbdepartments = departmentDao.findAll();
        if (dbdepartments != null) {
            return dbdepartments;
        } else {
            throw new CommonExp(ResultCode.FAIL);
        }
    }

    @Transactional
    public void deleteDepartment(String id) throws CommonExp {
        Department dbdepartment = departmentDao.findDepartmentByIdEquals(id);
        if (dbdepartment != null) {
            departmentDao.delete(dbdepartment);
        } else {
            throw new CommonExp(ResultCode.DEPARTMENTNOEXIST);
        }
    }


}
