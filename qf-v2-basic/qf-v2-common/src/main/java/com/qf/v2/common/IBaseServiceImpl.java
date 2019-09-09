package com.qf.v2.common;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qf.v2.entity.TProduct;

import java.util.List;

public abstract class IBaseServiceImpl<T> implements IBaseService<T>{

    public abstract IBaseDao<T> getIbaseDao();

    public int deleteByPrimaryKey(Long id){

        return getIbaseDao().deleteByPrimaryKey(id);
    }

    public int insert(T record){
        return getIbaseDao().insert(record);
    }

    public int insertSelective(T record){
        return getIbaseDao().insertSelective(record);
    }

    public T selectByPrimaryKey(Long id){
        return getIbaseDao().selectByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(T record){
        return getIbaseDao().updateByPrimaryKeySelective(record);
    }


    public int updateByPrimaryKey(T record){
        return getIbaseDao().updateByPrimaryKey(record);
    }


    public List<T> list() {
        return getIbaseDao().list();
    }

    public PageInfo<T> getPageInfo(int pageNum, int pageSize) {

        return getIbaseDao().getPageInfo(pageNum,pageSize);
    }
}
