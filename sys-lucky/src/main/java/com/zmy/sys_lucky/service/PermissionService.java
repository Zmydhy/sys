package com.zmy.sys_lucky.service;

import com.zmy.sys_common.entity.ResultCode;
import com.zmy.sys_common.exception.CommonExp;
import com.zmy.sys_common.utils.BeanMapUtils;
import com.zmy.sys_common.utils.IdWorker;
import com.zmy.sys_common.utils.PermissionConstants;
import com.zmy.sys_lucky.dao.PermissionApiDao;
import com.zmy.sys_lucky.dao.PermissionDao;
import com.zmy.sys_lucky.dao.PermissionMenuDao;
import com.zmy.sys_lucky.dao.PermissionPointDao;
import com.zmy.sys_moudle.lucky.entity.Permission;
import com.zmy.sys_moudle.lucky.entity.PermissionApi;
import com.zmy.sys_moudle.lucky.entity.PermissionMenu;
import com.zmy.sys_moudle.lucky.entity.PermissionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 * Permission: zmy
 * Date: 2020/1/3
 * Time: 9:53
 * Description:
 */
@Service
public class PermissionService {

    @Autowired
    PermissionDao permissionDao;

    @Autowired
    PermissionApiDao permissionApiDao;

    @Autowired
    PermissionMenuDao permissionMenuDao;

    @Autowired
    PermissionPointDao permissionPointDao;

    /**
     * 1.保存用户
     */
    public Permission save(Map<String, Object> map) throws Exception {
        String name = (String) map.get("name");
        String department = (String) map.get("department");
        Permission dbPermission = permissionDao.findPermissionByNameEqualsAndDepartmentEquals(name, department);
        if (dbPermission == null) {
            //设置主键的值
            String id = IdWorker.getInstance().nextId();
            Permission perm = BeanMapUtils.mapToBean(map, Permission.class);
            perm.setId(id);
            perm.setUpdateTime(new Date());
            perm.setCreateTime(new Date());
            //2.根据类型构造不同的资源对象（菜单，按钮，api）
            int type = perm.getType();
            switch (type) {
                case PermissionConstants.PERMISSION_MENU:
                    PermissionMenu menu = BeanMapUtils.mapToBean(map, PermissionMenu.class);
                    menu.setId(id);
                    menu.setCreateTime(new Date());
                    permissionMenuDao.save(menu);
                    break;
                case PermissionConstants.PERMISSION_POINT:
                    PermissionPoint point = BeanMapUtils.mapToBean(map, PermissionPoint.class);
                    point.setId(id);
                    point.setCreateTime(new Date());
                    permissionPointDao.save(point);
                    break;
                case PermissionConstants.PERMISSION_API:
                    PermissionApi api = BeanMapUtils.mapToBean(map, PermissionApi.class);
                    api.setId(id);
                    api.setCreateTime(new Date());
                    permissionApiDao.save(api);
                    break;
                default:
                    throw new CommonExp(ResultCode.FAIL);
            }
            //调用dao保存部门
            permissionDao.save(perm);
            return perm;
        } else {
            throw new CommonExp(ResultCode.PERMISSIONALEADYEXIST);
        }

    }

    /**
     * 更新用户
     */
    public Permission update(Permission permission) throws CommonExp {
        Permission dbpermission = permissionDao.findPermissionByIdEquals(permission.getId());
        if (dbpermission != null) {
            Optional.ofNullable(permission.getDescription()).ifPresent((s) -> dbpermission.setDescription(s));
            Optional.ofNullable(permission.getParentId()).ifPresent((s) -> dbpermission.setParentId(s));
            Optional.ofNullable(permission.getDepartment()).ifPresent((s) -> dbpermission.setDepartment(s));
            dbpermission.setUpdateTime(new Date());
            //调用dao保存部门
            permissionDao.save(dbpermission);
            return dbpermission;
        } else {
            throw new CommonExp(ResultCode.PERMISSIONNOEXIST);
        }
    }

    /**
     * 3.根据id查询用户
     */
    public Permission findById(String id) throws CommonExp {
        Permission dbpermission = permissionDao.findPermissionByIdEquals(id);
        if (dbpermission != null) {
            return permissionDao.findById(id).get();
        } else {
            throw new CommonExp(ResultCode.PERMISSIONNOEXIST);
        }
    }

    public void deletePermission(String id) throws CommonExp {
        Permission dbpermission = permissionDao.findPermissionByIdEquals(id);
        if (dbpermission != null) {
            permissionDao.delete(dbpermission);
        } else {
            throw new CommonExp(ResultCode.PERMISSIONNOEXIST);
        }
    }


}
