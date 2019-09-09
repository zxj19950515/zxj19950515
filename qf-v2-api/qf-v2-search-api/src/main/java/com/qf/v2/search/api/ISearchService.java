package com.qf.v2.search.api;

import com.qf.v2.common.pojo.PageResultBean;
import com.qf.v2.common.pojo.ResultBean;
import com.qf.v2.entity.TProduct;

import java.util.List;

public interface ISearchService {

    //同步数据库中的数据到索引库
    ResultBean<String> synAllData();

    List<TProduct>  getProudctByKeywords(String productKeywords);

    PageResultBean<TProduct> getProductByKeywordsPage(String productKeywords, Integer pageIndex, Integer pageSize);

    ResultBean saveOrUpdate(TProduct product);
}
