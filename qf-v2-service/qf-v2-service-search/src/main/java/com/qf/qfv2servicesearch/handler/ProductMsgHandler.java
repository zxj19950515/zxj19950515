package com.qf.qfv2servicesearch.handler;

import com.qf.qfv2servicesearch.service.SearchServiceImpl;
import com.qf.v2.common.constant.RabbitConstant;
import com.qf.v2.common.pojo.ResultBean;
import com.qf.v2.entity.TProduct;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMsgHandler {

    @Autowired
    private SearchServiceImpl searchService;

    @RabbitListener(queues = RabbitConstant.PRODUCT_ADD_QUEUE)
    public void processSaveOrUpdate(TProduct product){
        System.out.println("收到商品添加信息:    "+product.toString());
        ResultBean resultBean = searchService.saveOrUpdate(product);
        System.out.println("更新索引库成功"+resultBean);
    }
}
