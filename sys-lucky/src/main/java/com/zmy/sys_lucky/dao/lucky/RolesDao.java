package com.zmy.sys_lucky.dao.lucky;


import com.zmy.sys_moudle.lucky.entity.lucky.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/3/15
 * Time: 15:39
 * Description:
 */
@Repository
public interface RolesDao extends JpaRepository<Roles, String> {
    Roles findRoleByIdEquals(Integer id);
}
