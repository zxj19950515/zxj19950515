package com.qf.qfv2webindex.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.v2.common.constant.CookieConstant;
import com.qf.v2.common.pojo.ResultBean;
import com.qf.v2.entity.TProductType;
import com.qf.v2.product.api.IProductTypeService;
import com.qf.v2.user.api.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ProductPreController {

    @Reference
    private IProductTypeService productTypeService;

    @Reference
    private IUserService userService;

    @RequestMapping("index")
    public String showIndex(Model model){
        List<TProductType> productTypes = productTypeService.list();
        model.addAttribute("productTypes",productTypes);
        System.out.println(productTypes);
        return "index";
    }


    @RequestMapping("checkIsLogin")
    @ResponseBody
    public ResultBean checkIsLogin(@CookieValue(value = CookieConstant.USER_TOKEN,
            required = false)String uuid){


        return userService.checkIsLogin(uuid);
    }

}
