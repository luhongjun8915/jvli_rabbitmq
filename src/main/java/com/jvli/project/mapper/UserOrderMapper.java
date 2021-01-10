package com.jvli.project.mapper;

import org.apache.ibatis.annotations.Param;

import com.jvli.project.entity.UserOrder;

public interface UserOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserOrder record);

    int insertSelective(UserOrder record);

    UserOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserOrder record);

    int updateByPrimaryKey(UserOrder record);
    
    UserOrder selectByPkAndStatus(@Param("id") Integer id, @Param("status") Integer status);
}