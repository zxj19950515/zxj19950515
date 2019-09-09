package com.qf.v2.item.api;

import com.qf.v2.common.pojo.ResultBean;
import com.qf.v2.entity.TProduct;

import java.util.List;

public interface ItemService {

    //生成商品详情页
    ResultBean createItemPages(TProduct product);
    //批量生成商品详情页
    public ResultBean batchCreatePages(List<TProduct> products);
}
