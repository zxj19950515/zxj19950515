package com.qf.qfv2webproduct.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.qf.v2.common.constant.RabbitConstant;
import com.qf.v2.entity.TProduct;
import com.qf.v2.item.api.ItemService;
import com.qf.v2.product.api.IProductService;
import com.qf.v2.product.dto.ProductDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class ProductController {

    @Reference
    private IProductService productService;

    @Reference
    private ItemService itemService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private FastFileStorageClient fileStorageClient;

    @Value("${image.http}")
    private String IMAGE_HTTP;

    @RequestMapping("product/{id}")
    @ResponseBody
    public String getProduct(@PathVariable("id") Long id){
        TProduct tProduct = productService.selectByPrimaryKey(id);
        System.out.println(tProduct);
        return tProduct.toString();
    }

    @RequestMapping("hello")
    public String hello(){

        return "product";
    }

    @RequestMapping("product/list")
    @ResponseBody
    public String productList(Model model){
        List<TProduct> products = productService.getProductList();
        itemService.batchCreatePages(products);
//        model.addAttribute("products",products);
        return "product";
    }

    @RequestMapping("product/page/{pageNum}/{pageSize}")
    public String productList(Model model,@PathVariable("pageNum")int pageNum,
                              @PathVariable("pageSize") int pageSize){
        PageInfo<TProduct> pageInfo = productService.getPageInfo(pageNum, pageSize);
        model.addAttribute("pageInfo",pageInfo);
        return "product";
    }
    /**
     * 添加商品
     */

    @RequestMapping("product/add")
    public String productAdd(ProductDto dto, MultipartFile file){

        System.out.println(dto);
        System.out.println(file);
        //用fastdfs进行图片上传 得到图片地址
        String originalFilename = file.getOriginalFilename();
        String ext=originalFilename.substring(originalFilename.lastIndexOf(".")+1);
        String fullPath=null;
        try {
            StorePath storePath = fileStorageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), ext, null);
            fullPath=storePath.getFullPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dto.gettProduct().setImage(IMAGE_HTTP+"/"+fullPath);
        Long typeId = dto.gettProduct().getTypeId();
        if (typeId.longValue()==1){
            dto.gettProduct().setTypeName("手机数码");
        }else {
            dto.gettProduct().setTypeName("家用电器");
        }
        Long productId = productService.save(dto);
        System.out.println("回显的productId--->   "+dto.gettProduct().getId());
        TProduct product = dto.gettProduct();
        product.setId(productId);
        //生成对应的商品详情页
        itemService.createItemPages(product);
        //将商品信息同步到索引库中
        rabbitTemplate.convertAndSend(RabbitConstant.PRODUCT_EXCHANGE,"product.add",product);
        System.out.println(productId);
        return "redirect:/product/page/1/1";
    }


}
