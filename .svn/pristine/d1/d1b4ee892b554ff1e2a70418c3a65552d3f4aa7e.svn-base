package com.jvli.project.mapper;



import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jvli.project.entity.OrderRecord;

@Mapper
public interface OrderRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderRecord record);

    int insertSelective(OrderRecord record);

    OrderRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderRecord record);

    int updateByPrimaryKey(OrderRecord record);

    List<OrderRecord> selectAll();
}