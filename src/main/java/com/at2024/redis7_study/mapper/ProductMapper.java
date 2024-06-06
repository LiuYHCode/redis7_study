package com.at2024.redis7_study.mapper;

import com.at2024.redis7_study.entities.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper {
    Product selectProductById(String productId);
}