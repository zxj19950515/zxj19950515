package com.qf.v2.product.dto;

import com.qf.v2.entity.TProduct;

import java.io.Serializable;

public class ProductDto implements Serializable {

    //商品信息
    private TProduct tProduct;
    //商品详情信息
    private String productDesc;

    public ProductDto(TProduct tProduct, String productDesc) {
        this.tProduct = tProduct;
        this.productDesc = productDesc;
    }

    public ProductDto() {
    }

    public TProduct gettProduct() {
        return tProduct;
    }

    public void settProduct(TProduct tProduct) {
        this.tProduct = tProduct;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }
}
