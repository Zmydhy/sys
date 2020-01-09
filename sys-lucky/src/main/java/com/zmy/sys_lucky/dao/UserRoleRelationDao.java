package com.zmy.sys_lucky.dao;

import com.zmy.sys_moudle.lucky.entity.UserRoleRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/1/3
 * Time: 9:43
 * Description:
 */
@Repository
public interface UserRoleRelationDao extends JpaRepository<UserRoleRelation, String> {
    void  deleteAllByUserIdEquals(String userId);
    List<UserRoleRelation> findAllByUserIdEquals(String userId);
}
