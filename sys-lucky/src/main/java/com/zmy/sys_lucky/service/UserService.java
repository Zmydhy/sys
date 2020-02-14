package com.zmy.sys_lucky.service;

import com.zmy.sys_common.config.Constant;
import com.zmy.sys_common.entity.ResultCode;
import com.zmy.sys_common.exception.CommonExp;
import com.zmy.sys_common.utils.IdWorker;
import com.zmy.sys_common.utils.encdec.PasswordUtils;
import com.zmy.sys_lucky.dao.DepartmentUserRelationDao;
import com.zmy.sys_lucky.dao.UserRoleRelationDao;
import com.zmy.sys_moudle.lucky.entity.DepartmentUserRelation;
import com.zmy.sys_moudle.lucky.entity.Role;
import com.zmy.sys_moudle.lucky.entity.User;
import com.zmy.sys_lucky.dao.RoleDao;
import com.zmy.sys_lucky.dao.UserDao;
import com.zmy.sys_moudle.lucky.entity.UserRoleRelation;
import com.zmy.sys_moudle.lucky.vo.resp.RoleVo;
import com.zmy.sys_moudle.lucky.vo.resp.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/1/3
 * Time: 9:53
 * Description:
 */
@Service
@Transactional
public class UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    RoleService roleService;
    @Autowired
    UserRoleRelationDao userRoleRelationDao;
    @Autowired
    DepartmentUserRelationDao departmentUserRelationDao;

    /**
     * 1.保存用户
     */
    public User save(User user) throws CommonExp {
        User dbUser = userDao.findUserByUsernameEquals(user.getUsername());
        if (dbUser == null) {
            String salt = PasswordUtils.getSalt();
            String password = PasswordUtils.encode(user.getPassword(), salt);
            //设置主键的值
            String id = IdWorker.getInstance().nextId();
            user.setId(id);
            user.setPassword(password);
            user.setSalt(salt);
            user.setState("1");
            user.setUpdateTime(new Date());
            user.setCreateTime(new Date());
            //调用dao保存部门
            userDao.save(user);
            return user;
        } else {
            throw new CommonExp(ResultCode.USERALEADYEXIST);
        }

    }

    /**
     * 更新用户
     */
    public User update(User user) throws CommonExp {
        User dbuser = userDao.findUserByIdEquals(user.getId());
        if (dbuser != null) {
            Optional.ofNullable(user.getRemark()).ifPresent((s) -> dbuser.setRemark(s));
            Optional.ofNullable(user.getDepartment()).ifPresent((s) -> dbuser.setDepartment(s));
            Optional.ofNullable(user.getMobile()).ifPresent((s) -> dbuser.setMobile(s));
            Optional.ofNullable(user.getNickname()).ifPresent((s) -> dbuser.setNickname(s));
            Optional.ofNullable(user.getPassword()).ifPresent((s) -> dbuser.setPassword(s));
            Optional.ofNullable(user.getState()).ifPresent((s) -> dbuser.setState(s));
            dbuser.setUpdateTime(new Date());
            //调用dao保存部门
            userDao.save(dbuser);
            return dbuser;
        } else {
            throw new CommonExp(ResultCode.USERNOEXIST);
        }
    }

    public User findById(String id) throws CommonExp {
        User dbuser = userDao.findUserByIdEquals(id);
        if (dbuser != null) {
            return dbuser;
        } else {
            throw new CommonExp(ResultCode.USERNOEXIST);
        }
    }

    /**
     * 3.根据id查询用户
     */
    public UserVo findUserVoById(String id) throws CommonExp {
        User dbuser = userDao.findUserByIdEquals(id);
        if (dbuser != null) {
            List<UserRoleRelation> list = userRoleRelationDao.findAllByUserIdEquals(dbuser.getId());
            Set<RoleVo> roles = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {
                RoleVo role = roleService.findRoleVoById(list.get(i).getRoleId());
                roles.add(role);
            }
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(dbuser, userVo);
            userVo.setRoles(roles);
            return userVo;
        } else {
            throw new CommonExp(ResultCode.USERNOEXIST);
        }
    }

    public void deleteUser(String id) throws CommonExp {
        User dbuser = userDao.findUserByIdEquals(id);
        if (dbuser != null) {
            userDao.delete(dbuser);
            //删除用户的时候同时删除用户和角色关联表
            userRoleRelationDao.deleteAllByUserIdEquals(id);
            departmentUserRelationDao.deleteAllByUserIdEquals(dbuser.getId());
        } else {
            throw new CommonExp(ResultCode.USERNOEXIST);
        }
    }

    @Transactional
    public UserVo assignRoles(String userId, List<String> roleIds) throws CommonExp {
        //1.根据id查询用户
        User user = userDao.findById(userId).get();
        //首先清除用户角色关联
//        userRoleRelationDao.deleteAllByUserIdEquals(user.getId());
        //2.设置用户的角色集合,重新关联
        Set<RoleVo> roles = new HashSet<>();
        for (String roleId : roleIds) {
            RoleVo role = roleService.findRoleVoById(roleId);
            //创建关联
            createUR_Realation(user.getId(), roleId);
            roles.add(role);
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        userVo.setRoles(roles);
        return userVo;
    }

    private void createUR_Realation(String userId, String roleId) {
        UserRoleRelation userRoleRelation1 = userRoleRelationDao.findUserRoleRelationByUserIdEqualsAndRoleIdEquals(userId, roleId);
        if (userRoleRelation1 == null) {
            UserRoleRelation userRoleRelation = new UserRoleRelation();
            userRoleRelation.setId(IdWorker.getInstance().nextId());
            userRoleRelation.setRoleId(roleId);
            userRoleRelation.setUserId(userId);
            userRoleRelation.setCreateTime(new Date());
            userRoleRelation.setUpdateTime(new Date());
            //加入表
            userRoleRelationDao.save(userRoleRelation);
        }

    }

    public List<UserVo> findUserListByDepartment(String department) throws CommonExp {
        List<User> list = userDao.findAllByDepartmentEquals(department);
        List<UserVo> voList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            UserVo userVo = findUserVoById(list.get(i).getId());
            voList.add(userVo);
        }
        return voList;
    }

    public User findByUserName(String username) {
        return userDao.findUserByUsernameEquals(username);
    }

    public List<Role> getRoles(String userId) throws CommonExp {
        User dbuser = userDao.findUserByIdEquals(userId);
        if (dbuser != null) {
            List<UserRoleRelation> list = userRoleRelationDao.findAllByUserIdEquals(dbuser.getId());
            List<Role> roles = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Role role = roleService.findById(list.get(i).getRoleId());
                roles.add(role);
            }
            return roles;
        }
        return null;
    }
}
