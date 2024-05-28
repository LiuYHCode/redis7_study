package com.at2024.redis7_study.mapper;

import com.at2024.redis7_study.entities.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper {
    int insertCustomerForSelective(Customer customer);

    Customer selectCustomerById(Integer id);
}