package com.zmy.sys_lucky.controller;

import com.zmy.sys_common.controller.BaseController;
import com.zmy.sys_common.entity.Result;
import com.zmy.sys_common.entity.ResultCode;
import com.zmy.sys_common.exception.CommonExp;
import com.zmy.sys_common.utils.JwtUtils;
import com.zmy.sys_common.utils.PermissionConstants;
import com.zmy.sys_common.utils.encdec.PasswordUtils;
import com.zmy.sys_lucky.service.RoleService;
import com.zmy.sys_lucky.service.UserService;
import com.zmy.sys_moudle.lucky.entity.Permission;
import com.zmy.sys_moudle.lucky.entity.Role;
import com.zmy.sys_moudle.lucky.entity.User;
import com.zmy.sys_moudle.lucky.vo.resp.ProfileVo;
import com.zmy.sys_moudle.lucky.vo.resp.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.zmy.sys_common.ApiPermission;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/1/3
 * Time: 9:42
 * Description:
 */
//1.解决跨域
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @PostMapping
    @ApiPermission(name = "api:adduser")
    public Result addUser(@RequestBody @Valid User user , BindingResult bindingResult) throws CommonExp {
        if (bindingResult.hasErrors()) {
            return Result.error(ResultCode.FAIL,Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        return Result.success(getUserVo(userService.save(user)));
    }



    @GetMapping
    @ApiPermission(name = "api:queryuser")
    public Result queryUser(@RequestParam String userId) throws CommonExp {
        return Result.success(userService.findUserVoById(userId));
    }
    @GetMapping("/list")
    public Result queryUserList(@RequestParam String depart) throws CommonExp {
        return Result.success(userService.findUserListByDepartment(depart));
    }

    @PutMapping
    @ApiPermission(name = "api:updateuser")
    public Result updateUser(@RequestBody User user) throws CommonExp {
        User dbuser = userService.update(user);
        return Result.success(getUserVo(dbuser));
    }

    @DeleteMapping
    @ApiPermission(name = "api:deleteuser")
    public Result deleteUser(@RequestParam String userId) throws CommonExp {
        userService.deleteUser(userId);
        return Result.success();
    }


    /**
     * 分配角色
     */
    @PostMapping("/assign_role")
    @ApiPermission(name = "api:assignrole")
    public Result assignRoles(@RequestBody Map<String, Object> map) throws CommonExp {
        //1.获取被分配的用户id
        String userId = (String) map.get("id");
        //2.获取到角色的id列表
        List<String> roleIds = (List<String>) map.get("roleIds");
        //3.调用service完成角色分配
        UserVo userVo = userService.assignRoles(userId, roleIds);
        return Result.success(userVo);
    }

    private UserVo getUserVo(User user) {
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }

    /**
     * 用户登录
     * 1.通过service根据mobile查询用户
     * 2.比较password
     * 3.生成jwt信息
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> loginMap) throws CommonExp {
        String username = loginMap.get("username");
        String password = loginMap.get("password");
        User user = userService.findByUserName(username);
        //登录失败
        if (user == null){
            return Result.error(ResultCode.USERNOEXIST);
        }else if(!PasswordUtils.matches(user.getSalt(),password,user.getPassword())){
            return Result.error(ResultCode.USERNAMEORPASSWORDERROR);
        } else if(user.getState().equals("0")){
            return Result.error(ResultCode.USERLOCKED);
        }else {
            //登录成功
            //api权限字符串
            StringBuilder sb = new StringBuilder();
            //获取到所有的可访问API权限
            List<Role> roleList = userService.getRoles(user.getId());
            sb.append("PERMISSIONS;");
            if (roleList != null && roleList.size()>0){
                for (Role role : roleList) {
                    List<Permission> permissionList = roleService.getPermissions(role.getId());
                    if (permissionList != null && permissionList.size()>0){
                        for (Permission perm : permissionList) {
                            if (perm.getType() == PermissionConstants.PERMISSION_API) {
                                sb.append(perm.getCode()).append(",");
                            }
                        }
                    }
                }
            }

            Map<String, Object> map = new HashMap<>();
            map.put("apis", sb.toString());//可访问的api权限字符串
            map.put("department", user.getDepartment());
            String token = JwtUtils.createJwt(user.getId(), user.getUsername(), map);
            return Result.success(token);
        }
    }


    /**
     * 用户登录成功之后，获取用户信息
     * 1.获取用户id
     * 2.根据用户id查询用户
     * 3.构建返回值对象
     * 4.响应
     */
    @PostMapping("/profile")
    public Result profile(HttpServletResponse response) throws Exception {

        String userId = claims.getId();
        //获取用户信息
        UserVo user = userService.findUserVoById(userId);
        //根据不同的用户级别获取用户权限
        ProfileVo result = new ProfileVo(user);
        return Result.success(result);
    }
}
