package com.zmy.sys_lucky.service;

import com.zmy.sys_common.entity.ResultCode;
import com.zmy.sys_common.exception.CommonExp;
import com.zmy.sys_common.utils.IdWorker;
import com.zmy.sys_lucky.dao.*;
import com.zmy.sys_moudle.lucky.entity.Permission;
import com.zmy.sys_moudle.lucky.entity.Role;
import com.zmy.sys_moudle.lucky.entity.RolePermissionRelation;
import com.zmy.sys_moudle.lucky.vo.resp.PermissionVo;
import com.zmy.sys_moudle.lucky.vo.resp.RoleVo;
import com.zmy.sys_moudle.lucky.vo.resp.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class RoleService {


    @Autowired
    RoleDao roleDao;
    @Autowired
    PermissionService permissionService;
    @Autowired
    PermissionDao permissionDao;
    @Autowired
    PermissionPointDao permissionPointDao;
    @Autowired
    PermissionApiDao permissionApiDao;
    @Autowired
    PermissionMenuDao permissionMenuDao;
    @Autowired
    RolePermissionRelationDao rolePermissionRelationDao;
    @Autowired
    UserRoleRelationDao userRoleRelationDao;

    private String[] permissions = {"添加用户api", "删除用户api", "添加权限api", "删除权限api", "添加角色api", "删除角色api", "修改用户api", "修改权限api", "修改角色api", "分配角色api", "分配权限api"};


    /**
     * 1.保存用户
     */
    public Role save(Role role) throws CommonExp {
        Role dbRole = roleDao.findRoleByNameEqualsAndDepartmentEquals(role.getName(), role.getDepartment());
        if (dbRole == null) {
            //设置主键的值
            String id = IdWorker.getInstance().nextId();
            role.setId(id);
            role.setUpdateTime(new Date());
            role.setCreateTime(new Date());
            //调用dao保存部门
            roleDao.save(role);
            return role;
        } else {
            throw new CommonExp(ResultCode.ROLEALEADYEXIST);
        }

    }

    /**
     * 赋予角色管理员权限
     */
    public void assignPermissions(String id) throws CommonExp {
        Role role = roleDao.findRoleByIdEquals(id);
        if (role != null) {
            List<String> lists = new ArrayList<>();
            //TODO
            try {
                for (int i = 0; i < permissions.length; i++) {
                    Permission permission = permissionService.findByName(permissions[i], "lucky");
                    if (permission != null) {
                        lists.add(permission.getId());
                    }
                }
            } catch (CommonExp commonExp) {
                commonExp.printStackTrace();
            }
            assignPermission(role.getId(), lists);

        }
    }

    /**
     * 更新用户
     */
    public Role update(Role role) throws CommonExp {
        Role dbrole = roleDao.findRoleByIdEquals(role.getId());
        if (dbrole != null) {
            Optional.ofNullable(role.getDescription()).ifPresent((s) -> dbrole.setDescription(s));
            Optional.ofNullable(role.getDepartment()).ifPresent((s) -> dbrole.setDepartment(s));
            dbrole.setUpdateTime(new Date());
            //调用dao保存部门
            roleDao.save(dbrole);
            return dbrole;
        } else {
            throw new CommonExp(ResultCode.ROLENOEXIST);
        }
    }

    /**
     * 3.根据id查询用户
     */
    public Role findById(String id) throws CommonExp {
        Role dbrole = roleDao.findRoleByIdEquals(id);
        if (dbrole != null) {
            return roleDao.findById(id).get();
        } else {
            throw new CommonExp(ResultCode.ROLENOEXIST);
        }
    }

    public List<RoleVo> getList(String department) throws CommonExp {
        List<Role> mlist = roleDao.findRolesByDepartmentEquals(department);
        if (mlist != null && mlist.size() > 0) {
            List<RoleVo> lists = new ArrayList<>();
            for (int i = 0; i < mlist.size(); i++) {
                RoleVo roleVo = findRoleVoById(mlist.get(i).getId());
                lists.add(roleVo);
            }
            return lists;
        } else {
            throw new CommonExp(ResultCode.FAIL);
        }
    }

    /**
     * 3.根据id查询用户
     */
    public RoleVo findRoleVoById(String id) throws CommonExp {
        Role dbrole = roleDao.findRoleByIdEquals(id);
        if (dbrole != null) {
            List<Permission> list = getPermissions(dbrole.getId());
            Set<Permission> roleSet = new HashSet<>(list);

            RoleVo roleVo = new RoleVo();
            BeanUtils.copyProperties(dbrole, roleVo);
            roleVo.setPermissions(new HashSet<Permission>(list));
            return roleVo;
        } else {
            throw new CommonExp(ResultCode.ROLENOEXIST);
        }
    }


    public void deleteRole(String id) throws CommonExp {
        Role dbrole = roleDao.findRoleByIdEquals(id);
        if (dbrole != null) {
            roleDao.delete(dbrole);
            //删除角色的的时候同时删除角色和权限的关联表
            rolePermissionRelationDao.deleteAllByRoleIdEquals(dbrole.getId());
            userRoleRelationDao.deleteAllByRoleIdEquals(dbrole.getId());

        } else {
            throw new CommonExp(ResultCode.ROLENOEXIST);
        }
    }

    @Transactional
    public RoleVo assignPermission(String rolesId, List<String> permissionIds) throws CommonExp {
        Role role = roleDao.findRoleByIdEquals(rolesId);
        if (role == null) {
            throw new CommonExp(ResultCode.ROLENOEXIST);
        } else {
            //首先清除用户角色关联
            rolePermissionRelationDao.deleteAllByRoleIdEquals(role.getId());
            //2.设置用户的角色集合,重新关联
            Set<Permission> permissions = new HashSet<>();
            for (String permissionId : permissionIds) {
                Permission permission = permissionDao.findPermissionByIdEquals(permissionId);
                //创建关联
                createRP_Realation(role.getId(), permissionId);
                permissions.add(permission);
            }
            RoleVo roleVo = new RoleVo();
            BeanUtils.copyProperties(role, roleVo);
            roleVo.setPermissions(permissions);
            return roleVo;
        }
    }

    private void createRP_Realation(String id, String permissionId) {
        RolePermissionRelation rolePermissionRelation = new RolePermissionRelation();
        rolePermissionRelation.setId(IdWorker.getInstance().nextId());
        rolePermissionRelation.setCreateTime(new Date());
        rolePermissionRelation.setUpdateTime(new Date());
        rolePermissionRelation.setPermissionId(permissionId);
        rolePermissionRelation.setRoleId(id);
        rolePermissionRelationDao.save(rolePermissionRelation);
    }


    public List<Permission> getPermissions(String id) {
        Role dbrole = roleDao.findRoleByIdEquals(id);
        if (dbrole != null) {
            List<RolePermissionRelation> lists = rolePermissionRelationDao.findAllByRoleIdEquals(dbrole.getId());
            List<Permission> permissions = new ArrayList<>();
            for (RolePermissionRelation permissionRelation : lists) {
                Permission permission = permissionDao.findPermissionByIdEquals(permissionRelation.getPermissionId());
                permissions.add(permission);
            }
            return permissions;
        }
        return null;
    }


}
