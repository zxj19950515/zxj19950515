package com.qf.v2.common;

import com.github.pagehelper.PageInfo;
import com.qf.v2.entity.TProduct;

import java.util.List;

public interface IBaseService<T> {

    int deleteByPrimaryKey(Long id);

    int insert(T record);

    int insertSelective(T record);

    T selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);

    List<T> list();

    PageInfo<T> getPageInfo(int pageNum, int pageSize);
}
