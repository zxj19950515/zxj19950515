package com.qf.qfv2serviceitem.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.v2.common.pojo.ResultBean;
import com.qf.v2.entity.TProduct;
import com.qf.v2.item.api.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
@Service
public class ItemServiceImpl implements ItemService {

    //核心线程数的选择根据cpu的核数
    private static final int CORE_THREAD_COUNT=Runtime.getRuntime().availableProcessors();

    private ExecutorService pool=new ThreadPoolExecutor(CORE_THREAD_COUNT, CORE_THREAD_COUNT*2,
            10L, TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>(100));

    //Executors.newSingleThreadExecutor();  //单线程模式 保证按顺序执行
    //Executors.newFixedThreadPool();  //固定数量
    //Executors.newScheduledThreadPool()  定时任务
    // Executors.newCachedThreadPool() //缓存 特点 只要jvm内存够大 就无限创建
    //以上四个一个都不用 内存溢出
    @Autowired
    private Configuration configuration;

    @Value("${template.path}")
    private String TEMPLATE_PATH;

    //模板+数据=输出
    @Override
    public ResultBean createItemPages(TProduct product) {
        try {
            //模板
            Template template = configuration.getTemplate("product_detail.ftl");
            //数据
            Map<String,Object> data=new HashMap<>();
            data.put("product",product);
            //输出
            FileWriter out= new FileWriter(TEMPLATE_PATH+product.getId()+".html");
            template.process(data,out);

        } catch (Exception e) {
            e.printStackTrace();
            return ResultBean.errorResult("生成模板失败");
        }

        return ResultBean.successResult("生成模板成功");
    }

    //电脑 明明是多核 多核单线程
    //线程是不是越多越好?
    //单核多线程  线程切换交叉执行
    //多核多线程   64核 --》同时64个线程执行
    public ResultBean batchCreatePages(List<TProduct> products){

        ResultBean resultBean=null;
        List<Future> futures=new ArrayList<>();
        for (TProduct product : products) {
//            resultBean=createItemPages(product);
            Future<Boolean> future = pool.submit(new MyTask2(product));
            futures.add(future);
        }
        for (Future future : futures) {
            try {
                System.out.println(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }



        return resultBean;
    }

    class MyThread extends Thread{

        @Override
        public void run() {
            super.run();
        }
    }

    class MyTask1 implements Runnable{

        @Override
        public void run() {

        }
    }

    class MyTask2 implements Callable<Boolean>{

        private TProduct product;
        public MyTask2(TProduct product) {
            this.product=product;
        }

        @Override
        public Boolean call() throws Exception {

            //写实现生产商品详情页的逻辑
            return true;
        }
    }
}
