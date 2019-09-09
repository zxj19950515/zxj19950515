package com.qf.qfv2servicecart.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.v2.cart.api.ICartService;
import com.qf.v2.cart.api.pojo.CartItem;
import com.qf.v2.cart.api.vo.CartItemVo;
import com.qf.v2.common.constant.RedisConstant;
import com.qf.v2.common.pojo.ResultBean;
import com.qf.v2.entity.TProduct;
import com.qf.v2.mapper.TProductMapper;
import com.sun.org.apache.bcel.internal.generic.LSTORE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TProductMapper productMapper;

    @Override
    public ResultBean addToCart(String key, Long productId, Integer count) {


        redisTemplate.setKeySerializer(new StringRedisSerializer());
        List<CartItem> cart = (List<CartItem>) redisTemplate.opsForValue().get(key);
        if (cart==null){
            //没有购物车生成购物车
            cart=new ArrayList<>();
            return getResultBean(productId, count, key, cart);
        }
        //遍历购物车更改商品数量
        for (CartItem cartItem : cart) {
            if (cartItem.getProductId().longValue()==productId){
                cartItem.setCount(cartItem.getCount()+count);
                redisTemplate.setKeySerializer(new StringRedisSerializer());
                redisTemplate.opsForValue().set(key,cart);
                redisTemplate.expire(key,30, TimeUnit.DAYS);
                return new ResultBean(0,cart.size(),"添加购物车成功");
            }
        }

        //说明是一个新增的产品
        return getResultBean(productId, count, key, cart);

    }

    private ResultBean getResultBean(Long productId, Integer count, String uKey, List<CartItem> cart) {
        CartItem cartItem = new CartItem(productId, count, new Date());
        cart.add(cartItem);
        //存到redis中
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.opsForValue().set(uKey, cart);
        redisTemplate.expire(uKey, 30, TimeUnit.DAYS);
        return new ResultBean(0, cart.size(), "添加购物车成功");
    }

    @Override
    public ResultBean getCart(String key) { //uuid   userid



        redisTemplate.setKeySerializer(new StringRedisSerializer());
        List<CartItem> cart = (List<CartItem>) redisTemplate.opsForValue().get(key);

        if (cart==null || cart.size()==0){
            return new ResultBean(1,"","用户不存在购物车");
        }
        redisTemplate.expire(key,30,TimeUnit.DAYS);
        return new ResultBean(0,cart,"");
    }

    @Override
    public ResultBean getCartVO(String key) {

        ResultBean resultBean = getCart(key);
        if (resultBean.getCode()==0){
            List<CartItem> cart = (List<CartItem>) resultBean.getData();
            List<CartItemVo> cartItemVos=new ArrayList<>();
            for (CartItem cartItem : cart) {
                CartItemVo cartItemVo=new CartItemVo();
                //完成item和product的转换,先去缓存里面取,没有在去数据库里面取
                String rKey=new StringBuilder(RedisConstant.CART_PRODUCT_PRE).
                        append(cartItem.getProductId()).toString();
                redisTemplate.setKeySerializer(new StringRedisSerializer());
                TProduct product = (TProduct) redisTemplate.opsForValue().get(rKey);
                if (product==null){
                    //说明缓存中没有商品信息、去数据库查
                    product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                    redisTemplate.setKeySerializer(new StringRedisSerializer());
                    redisTemplate.opsForValue().set(rKey,product);
                    redisTemplate.expire(rKey,30,TimeUnit.DAYS);
                }
                cartItemVo.setCount(cartItem.getCount());
                cartItemVo.setProduct(product);
                cartItemVo.setUpdateTime(cartItem.getUpdateTime());
                cartItemVos.add(cartItemVo);
            }
            return new ResultBean(0,cartItemVos,"");
        }

        return resultBean;
    }

    @Override
    public ResultBean resetCount(String key, Long productId, Integer count) {
        ResultBean resultBean = getCart(key);
        if (resultBean.getCode().longValue()==0){
            List<CartItem> cartItems = (List<CartItem>) resultBean.getData();
            for (CartItem cartItem : cartItems) {
                if (cartItem.getProductId().longValue()==productId){
                    cartItem.setCount(count);
                    String rKey=new StringBuilder(RedisConstant.USER_CART_TOKEN_PRE).append(key).toString();
                    redisTemplate.setKeySerializer(new StringRedisSerializer());
                    redisTemplate.opsForValue().set(rKey,cartItems);
                    redisTemplate.expire(rKey,30,TimeUnit.DAYS);
                    return new ResultBean(0,cartItems,"");
                }
            }
        }
        return resultBean;
    }

    @Override
    public ResultBean remove(String key, Long productId) {

        ResultBean resultBean = getCart(key);
        if (resultBean.getCode().longValue()==0){
            List<CartItem> cartItems = (List<CartItem>)  resultBean.getData();
            for (CartItem cartItem : cartItems) {
                if (cartItem.getProductId().longValue()==productId){
                    cartItems.remove(cartItem);
                    String rKey=new StringBuilder(RedisConstant.USER_CART_TOKEN_PRE).append(key).toString();
                    redisTemplate.setKeySerializer(new StringRedisSerializer());
                    redisTemplate.opsForValue().set(rKey,cartItems);
                    redisTemplate.expire(rKey,30,TimeUnit.DAYS);
                    return new ResultBean(0,cartItems,"");
                }
            }
        }
        return resultBean;
    }

    @Override
    public ResultBean merge(String noLoginCartKey, String loginCartKey) {

        //获取未登录购物车内容
        ResultBean noLoginCart = getCart(noLoginCartKey);
        //获取登录购物车内容
        ResultBean loginCart = getCart(loginCartKey);
        //将未登录购物车内容合并到登录购物车中
        if (loginCart.getCode().longValue()==1){
            //这种情况是登录购物车没有商品需要将未登录购物车内容复制到已登录购物车
            List<CartItem> noLoginCartItems = (List<CartItem>) noLoginCart.getData();
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.opsForValue().set(loginCartKey,noLoginCartItems);
            redisTemplate.expire(loginCartKey,30,TimeUnit.DAYS);
            redisTemplate.delete(noLoginCartKey);
            return new ResultBean(0,"合并成功","");
        }
        //彼此都存在商品这时候就需要合并了
        Map<Long,CartItem> map=new HashMap<>();
        //先将未登录购物车内容放到里面
        List<CartItem> noLoginCartItems= (List<CartItem>) noLoginCart.getData();
        for (CartItem noLoginCartItem : noLoginCartItems) {
            map.put(noLoginCartItem.getProductId(),noLoginCartItem);
        }
        //在将已登录的购物车放到里面
        List<CartItem> loginCartItems= (List<CartItem>)loginCart.getData();
        for (CartItem loginCartItem : loginCartItems) {
            CartItem now = map.get(loginCartItem.getProductId());
            if (now==null){
                map.put(loginCartItem.getProductId(),loginCartItem);
            }else {
                //存在相同的产品 进行数量的合并
                now.setCount(loginCartItem.getCount()+now.getCount());
                map.put(loginCartItem.getProductId(),now);
            }
        }
        //得到合并后的购物车
        Collection<CartItem> cartItems = map.values();
        List<CartItem> loginCart1=new ArrayList<>(cartItems);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.opsForValue().set(loginCartKey,loginCart1);
        redisTemplate.expire(loginCartKey,30,TimeUnit.DAYS);
        //删除未登录购物车
        redisTemplate.delete(noLoginCartKey);
        //将合并后的购物车返回出去
        return new ResultBean(0,"购物车合并成功","");
    }
}
