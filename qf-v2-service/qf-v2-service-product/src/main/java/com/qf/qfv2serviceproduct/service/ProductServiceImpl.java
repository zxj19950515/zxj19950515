package com.qf.qfv2serviceproduct.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qf.v2.common.IBaseDao;
import com.qf.v2.common.IBaseServiceImpl;
import com.qf.v2.entity.TProduct;
import com.qf.v2.entity.TProductDesc;
import com.qf.v2.mapper.TProductDescMapper;
import com.qf.v2.mapper.TProductMapper;
import com.qf.v2.product.api.IProductService;
import com.qf.v2.product.dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Service
public class ProductServiceImpl extends IBaseServiceImpl<TProduct> implements IProductService {

    @Autowired
    private TProductMapper productMapper;

    @Autowired
    private TProductDescMapper productDescMapper;

    @Override
    public IBaseDao<TProduct> getIbaseDao() {
        return productMapper;
    }


    public List<TProduct> getProductList(){

        return productMapper.list();
    }

    @Override
    public Long save(ProductDto dto) {
        //添加商品
        TProduct product = dto.gettProduct();
        productMapper.insertSelective(product);
        //添加商品详情
        Long id = product.getId();
        TProductDesc desc=new TProductDesc();
        desc.setProductId(id);
        desc.setProductDesc(dto.getProductDesc());
        productDescMapper.insertSelective(desc);
        //返回添加商品的id

        return id;
    }

    @Override
    public PageInfo<TProduct> getPageInfo(int pageNum, int pageSize) {
        //开始启动分页功能
        PageHelper.startPage(pageNum,pageSize);
        //给分页插件提供数据
        List<TProduct> productList = getProductList();
        //获取分页数据信息
        PageInfo<TProduct> pageInfo=new PageInfo<>(productList,3);

        return pageInfo;
    }
}
