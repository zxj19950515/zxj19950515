package com.qf.v2.cart.api;

import com.qf.v2.common.pojo.ResultBean;

public interface ICartService {

    //添加商品到购物车
    ResultBean addToCart(String key,Long productId,Integer count);
    //查看购物车内容
    ResultBean getCart(String key);
    //返回给前端的接口带有商品详情信息
    ResultBean getCartVO(String key);
    //重置购物车商品数量
    ResultBean resetCount(String key,Long productId,Integer count );
    //删除购物车
    ResultBean remove(String key,Long productId);
    //合并购物车
    ResultBean merge(String noLoginCartKey,String loginCartKey);
}
