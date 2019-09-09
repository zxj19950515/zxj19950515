package com.qf.qfv2servicesearch.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.v2.common.pojo.PageResultBean;
import com.qf.v2.common.pojo.ResultBean;
import com.qf.v2.entity.TProduct;
import com.qf.v2.mapper.TProductMapper;
import com.qf.v2.search.api.ISearchService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Service
public class SearchServiceImpl implements ISearchService {

    @Autowired
    private TProductMapper productMapper;
    @Autowired
    private SolrClient solrClient;

    @Override
    public ResultBean<String> synAllData() {
        //1、查询数据库中的商品信息
        List<TProduct> productList = productMapper.list();
        //2、把查询的信息插入到索引库中
        for (TProduct product : productList) {
            SolrInputDocument document=new SolrInputDocument();
            document.addField("id",product.getId());
            document.addField("name",product.getName());
            document.addField("image",product.getImage());
            document.addField("price",product.getPrice());
            document.addField("sale_price",product.getSalePrice());
            document.addField("sale_point",product.getSalePoint());
            try {
                solrClient.add(document);
            } catch (Exception e) {
                e.printStackTrace();
                ResultBean.errorResult("添加索引库失败");
            }
        }
        try {
            solrClient.commit();
        } catch (Exception e) {
            e.printStackTrace();
            ResultBean.errorResult("添加索引库失败");
        }
        return ResultBean.successResult("添加索引库成功");
    }

    @Override
    public List<TProduct> getProudctByKeywords(String productKeywords) {

        List<TProduct> products=null;
        //1、查询索引库
        SolrQuery solrQuery=new SolrQuery();
        solrQuery.setQuery("product_keywords:"+productKeywords);
        //1.1添加高亮信息
        solrQuery.setHighlight(true);
        //1.2 设置高亮信息的字段
        solrQuery.addHighlightField("name");
        //1.3 高亮的本质就是html信息
        solrQuery.setHighlightSimplePre("<font color='red'>");
        solrQuery.setHighlightSimplePost("</font>");



        try {
            QueryResponse response = solrClient.query(solrQuery);
            SolrDocumentList documentList = response.getResults();
            products=new ArrayList<>(documentList.size());
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            for (SolrDocument entries : documentList) {
                TProduct product=new TProduct();
                product.setId(Long.parseLong(entries.getFieldValue("id").toString()));
                product.setImage(entries.getFieldValue("image").toString());
                product.setPrice(Long.parseLong(entries.getFieldValue("price").toString()));
                product.setSalePrice(Long.parseLong(entries.getFieldValue("sale_price").toString()));
                product.setSalePoint(entries.getFieldValue("sale_point").toString());
                //开始设置高亮信息
                Map<String, List<String>> highLightText = highlighting.get(entries.getFieldValue("id").toString());
                List<String> highList = highLightText.get("name");
                if (highList!=null){
                    //有高亮信息设置高亮
                    product.setName(highList.get(0));
                }else {
                    //没有高亮信息正常设置
                    product.setName(entries.getFieldValue("name").toString());
                }
                products.add(product);
            }
            return products;
        } catch (SolrServerException e) {
            e.printStackTrace();
            //记录日志
            //重试
            return new ArrayList<TProduct>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<TProduct>();
        }
    }

    @Override
    public PageResultBean<TProduct> getProductByKeywordsPage(String productKeywords,
                                                             Integer pageIndex,
                                                             Integer pageSize) {
        PageResultBean<TProduct> pageResultBean=new PageResultBean<>();
        //获取商品信息
        List<TProduct> products=null;
        SolrQuery solrQuery=new SolrQuery();
        //设置查询条件
        solrQuery.setQuery("product_keywords:"+productKeywords);
        //分页设置
        solrQuery.setStart((pageIndex-1)*pageSize);
        solrQuery.setRows(pageSize);
        //设置高亮
        solrQuery.setHighlight(true);
        solrQuery.addHighlightField("name");
        solrQuery.setHighlightSimplePre("<font color='red'>");
        solrQuery.setHighlightSimplePost("</font>");
        //执行查询
        try {
            QueryResponse response = solrClient.query(solrQuery);
            //获取总记录数
            long total = response.getResults().getNumFound();
            SolrDocumentList documentList = response.getResults();
            products=new ArrayList<>();
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            for (SolrDocument entries : documentList) {
                TProduct product=new TProduct();
                product.setId(Long.parseLong(entries.getFieldValue("id").toString()));
                product.setImage(entries.getFieldValue("image").toString());
                product.setPrice(Long.parseLong(entries.getFieldValue("price").toString()));
                product.setSalePrice(Long.parseLong(entries.getFieldValue("sale_price").toString()));
                product.setSalePoint(entries.getFieldValue("sale_point").toString());
                Map<String, List<String>> highlightText = highlighting.get(entries.getFieldValue("id")
                        .toString());
                List<String> highLightList = highlightText.get("name");
                if (highLightList!=null){
                    product.setName(highLightList.get(0));
                }else {
                    product.setName(entries.getFieldValue("name").toString());
                }
                products.add(product);
            }

            pageResultBean.setList(products);
            pageResultBean.setPageNum(pageIndex);
            pageResultBean.setPageSize(pageSize);
            pageResultBean.setTotal(total);
            pageResultBean.setPages((int) (total%pageSize==0?total/pageSize:total/pageSize+1));
            return pageResultBean;
        } catch (Exception e) {
            e.printStackTrace();
            //记录日志
            //重试
            return new PageResultBean<>();
        }

    }

    @Override
    public ResultBean saveOrUpdate(TProduct product) {

        SolrInputDocument document=new SolrInputDocument();
        document.addField("id",product.getId());
        document.addField("name",product.getName());
        document.addField("image",product.getImage());
        document.addField("price",product.getPrice());
        document.addField("sale_price",product.getSalePrice());
        document.addField("sale_point",product.getSalePoint());
        try {
            solrClient.add(document);
            solrClient.commit();
        } catch (Exception e) {
            e.printStackTrace();
            ResultBean.errorResult("添加索引库失败");
        }
        return ResultBean.successResult("添加索引库成功");
    }
}
