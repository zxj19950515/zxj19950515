package com.qf.qfv2websearch.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.v2.common.pojo.PageResultBean;
import com.qf.v2.common.pojo.ResultBean;
import com.qf.v2.entity.TProduct;
import com.qf.v2.search.api.ISearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("search")
public class SearchController {

    @Reference
    private ISearchService searchService;

    @RequestMapping("init_db_data")
    @ResponseBody
    ResultBean synAllData(){
        ResultBean<String> resultBean = searchService.synAllData();
        return resultBean;
    }
    @RequestMapping("searchByKeywords")
    public String searchByKeywords(String productKeywords, Model model){
        System.out.println(productKeywords);
        List<TProduct> productList=searchService.getProudctByKeywords(productKeywords);
        System.out.println(productList);
        model.addAttribute("products",productList);
        return "show_list";
    }

    //solr的分页功能
    @RequestMapping("searchByKeywords/{pageIndex}/{pageSize}")
    public String searchByKeywordsPage(String productKeywords,
                                       @PathVariable("pageIndex") Integer pageIndex,
                                       @PathVariable("pageSize") Integer pageSize,Model model){
        System.out.println(productKeywords);
        PageResultBean<TProduct> pageResultBean=searchService.getProductByKeywordsPage(productKeywords,pageIndex,pageSize);
        System.out.println(pageResultBean.toString());
        pageResultBean.setNavigatePages(3);
        model.addAttribute("pageInfo",pageResultBean);
        return "show_list";
    }
}
