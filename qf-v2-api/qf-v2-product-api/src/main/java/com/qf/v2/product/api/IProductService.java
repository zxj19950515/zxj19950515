package com.qf.v2.product.api;

import com.qf.v2.common.IBaseService;
import com.qf.v2.entity.TProduct;
import com.qf.v2.product.dto.ProductDto;

import java.util.List;

public interface IProductService extends IBaseService<TProduct> {

    //扩展些其他的方法

    public List<TProduct> getProductList();

    Long save(ProductDto dto);
    // 方法
}
