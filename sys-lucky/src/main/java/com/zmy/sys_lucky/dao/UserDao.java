package com.zmy.sys_lucky.dao;

import com.zmy.sys_moudle.lucky.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/1/3
 * Time: 9:43
 * Description:
 */
@Repository
public interface UserDao extends JpaRepository<User, String> {
    User findUserByIdEquals(String id);
    User findUserByUsernameEquals(String username);
}
