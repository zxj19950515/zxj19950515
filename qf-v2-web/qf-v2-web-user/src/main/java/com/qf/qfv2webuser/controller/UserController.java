package com.qf.qfv2webuser.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.v2.common.constant.CookieConstant;
import com.qf.v2.common.constant.RedisConstant;
import com.qf.v2.common.pojo.ResultBean;
import com.qf.v2.common.utils.HttpClientUtils;
import com.qf.v2.entity.TUser;
import com.qf.v2.user.api.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("user")
public class UserController {

    @Reference
    private IUserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/show/login")
    public String showLogin(){

        return "login";
    }

    @RequestMapping("isExists")
    @ResponseBody
    public String checkIsExists(){
        TUser user=new TUser();
        user.setUsername("123");
        user.setPassword("123");
        TUser user1 = userService.checkIsExists(user);
        return user1.toString();
    }


    @RequestMapping("login")
    public String login(TUser user, HttpServletResponse response,
                        @CookieValue(name = CookieConstant.USER_CART_TOKEN,required = false)String cartToken
                        ){

        //检查用户是否存在
        TUser currentUser = userService.checkIsExists(user);
        if (currentUser==null){
            return "login";
        }
        //生成UUID
        String uuid= UUID.randomUUID().toString();
        //拼接redis的key保存到redis中
        String userKey = new StringBuilder(RedisConstant.USER_TOKEN_PRE).append(uuid).toString();
        //currentUser的密码是密文这里我们不需要
        currentUser.setPassword(null);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.opsForValue().set(userKey,currentUser);
        //设置reddis的key的有效期
        redisTemplate.expire(userKey,30, TimeUnit.MINUTES);

        //生成cookie
        Cookie cookie=new Cookie(CookieConstant.USER_TOKEN,uuid);
        cookie.setPath("/");
        //表示该cookie无法用客户端的脚本拿到,只能通过后端程序拿到
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        //添加合并购物车功能
        Map<String,String> params=new HashMap<>();
        StringBuilder cookieValue=new StringBuilder(CookieConstant.USER_CART_TOKEN).append("=").append(cartToken);
        cookieValue.append(";");
        cookieValue.append(CookieConstant.USER_TOKEN);
        cookieValue.append("=");
        cookieValue.append(uuid);
        params.put("Cookie",cookieValue.toString());
        HttpClientUtils.doGetWithHeaders("http://localhost:9094/cart/merge",params);
        return "redirect:https://www.baidu.com";
    }

    @RequestMapping("/check/login")
    @ResponseBody
    public ResultBean checkIsLogin(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies==null){
            return new ResultBean(1,"","当前用户未登录");
        }
        //遍历cookie
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(CookieConstant.USER_TOKEN)){
                String uuid = cookie.getValue();
                redisTemplate.setKeySerializer(new StringRedisSerializer());
                String key=new StringBuilder(RedisConstant.USER_TOKEN_PRE).append(uuid).toString();
                TUser user = (TUser) redisTemplate.opsForValue().get(key);
                if (user!=null){
                    //刷新redis的userKey的有效期
                    redisTemplate.expire(key,30,TimeUnit.MINUTES);
                    return new ResultBean(0,user,"当前用户已登录");
                }
            }
        }

        return  new ResultBean(1,null,"当前用户未登录");
    }

    @RequestMapping("logout")
    @ResponseBody
    public ResultBean logout(@CookieValue(value = CookieConstant.USER_TOKEN,required = false)
                                         String uuid,HttpServletResponse response){

        if (uuid==null){
            return new ResultBean(1,"","当前用户未登录");
        }
        //删除redis中的key
        String key=new StringBuilder(RedisConstant.USER_TOKEN_PRE).append(uuid).toString();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.delete(key);

        //删除cookie
        Cookie cookie=new Cookie(CookieConstant.USER_TOKEN,uuid);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return new ResultBean(0,null,"退出登录成功");
    }

}
