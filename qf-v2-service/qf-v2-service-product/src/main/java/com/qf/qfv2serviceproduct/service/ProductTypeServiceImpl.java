package com.qf.qfv2serviceproduct.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.v2.common.IBaseDao;
import com.qf.v2.common.IBaseServiceImpl;
import com.qf.v2.entity.TProductType;
import com.qf.v2.mapper.TProductTypeMapper;
import com.qf.v2.product.api.IProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Service
public class ProductTypeServiceImpl extends IBaseServiceImpl<TProductType> implements IProductTypeService {

    @Autowired
    private TProductTypeMapper productTypeMapper;

    @Override
    public IBaseDao<TProductType> getIbaseDao() {
        return productTypeMapper;
    }

    public List<TProductType> getProductTypeList(){

        return productTypeMapper.list();
    }
}
