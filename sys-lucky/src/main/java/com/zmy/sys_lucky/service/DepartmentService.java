package com.zmy.sys_lucky.service;

import com.zmy.jar_test.log.Log;
import com.zmy.sys_common.entity.ResultCode;
import com.zmy.sys_common.exception.CommonExp;
import com.zmy.sys_common.utils.IdWorker;
import com.zmy.sys_lucky.dao.DepartmentDao;
import com.zmy.sys_lucky.dao.DepartmentUserRelationDao;
import com.zmy.sys_lucky.dao.RoleDao;
import com.zmy.sys_lucky.dao.UserDao;
import com.zmy.sys_moudle.lucky.entity.Department;
import com.zmy.sys_moudle.lucky.entity.DepartmentUserRelation;
import com.zmy.sys_moudle.lucky.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
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
    DepartmentUserRelationDao departmentUserRelationDao;


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
            addRelation(department,admin);
            //调用dao保存部门
            departmentDao.save(department);
            return department;
        } else {
            throw new CommonExp(ResultCode.DEPARTMENTALEADYEXIST);
        }

    }

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
            return user;
        } else {
          return  dbuser;
        }
    }

    private User createUser(Department manager) {
        User admin = new User();
        admin.setId(IdWorker.getInstance().nextId());
        admin.setState("1");
        if (manager.getManager() == null) {
            admin.setUsername(manager.getName() + "_admin");
        } else {
            admin.setUsername(manager.getManager());
        }
        admin.setPassword("666666");
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
                    }else {
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
