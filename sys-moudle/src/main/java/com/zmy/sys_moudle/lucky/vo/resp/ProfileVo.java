package com.zmy.sys_moudle.lucky.vo.resp;

import com.zmy.sys_moudle.lucky.entity.Permission;
import com.zmy.sys_moudle.lucky.entity.Role;
import com.zmy.sys_moudle.lucky.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * 登陆后获取额权限说明
 */

@Setter
@Getter
public class ProfileVo {
    private String id;
    private String username;
    private String department;
    private Set<String> roles = new HashSet<>();
    private Map<String,Object> permissions = new HashMap<>();

    /**
     *
     * @param user
     */
    public ProfileVo(User user, List<Permission> list) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.department = user.getDepartment();

        Set<String> menus = new HashSet<>();
        Set<String> points = new HashSet<>();
        Set<String> apis = new HashSet<>();

        for (Permission perm : list) {
            String code = perm.getCode();
            if(perm.getType() == 1) {
                menus.add(code);
            }else if(perm.getType() == 2) {
                points.add(code);
            }else {
                apis.add(code);
            }
        }
        this.permissions.put("menus",menus);
        this.permissions.put("points",points);
        this.permissions.put("apis",apis);
    }


    public ProfileVo(UserVo user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.department = user.getDepartment();

        Set<RoleVo> roless = user.getRoles();
        Set<String> menus = new HashSet<>();
        Set<String> points = new HashSet<>();
        Set<String> apis = new HashSet<>();
        for (RoleVo role : roless) {
            this.roles.add(role.getName());
            Set<Permission> perms = role.getPermissions();
            for (Permission perm : perms) {
                String code = perm.getCode();
                if(perm.getType() == 1) {
                    menus.add(code);
                }else if(perm.getType() == 2) {
                    points.add(code);
                }else {
                    apis.add(code);
                }
            }
        }

        this.permissions.put("menus",menus);
        this.permissions.put("points",points);
        this.permissions.put("apis",apis);
    }
}
