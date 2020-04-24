package com.zwq.dao;

import com.zwq.pojo.Resume;
import com.zwq.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * ⼀个符合SpringDataJpa要求的Dao层接⼝是需要继承JpaRepository和
 JpaSpecificationExecutor
 *
 * JpaRepository<操作的实体类类型,主键类型>
 * 封装了基本的CRUD操作
 *
 * JpaSpecificationExecutor<操作的实体类类型>
 * 封装了复杂的查询（分⻚、排序等）
 *
 */
@Repository
public interface UserDao extends JpaRepository<User,Integer>,JpaSpecificationExecutor<Resume> {

    @Query(value = "select * from tb_user  where username=?1 and password =?2",nativeQuery = true)
    User findBySql(String username,String password);
}
