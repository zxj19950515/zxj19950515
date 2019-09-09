package com.qf.qfv2serviceuser.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.v2.common.IBaseDao;
import com.qf.v2.common.IBaseServiceImpl;
import com.qf.v2.common.constant.RedisConstant;
import com.qf.v2.common.pojo.ResultBean;
import com.qf.v2.entity.TUser;
import com.qf.v2.mapper.TUserMapper;
import com.qf.v2.user.api.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Service
public class UserServiceImpl extends IBaseServiceImpl<TUser> implements IUserService {

    @Autowired
    private TUserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public IBaseDao<TUser> getIbaseDao() {
        return userMapper;
    }

    @Override
    public TUser checkIsExists(TUser user) {
        TUser currentUser = userMapper.selectByUsername(user.getUsername());
        //数据库中存储的密码都应该是密文
        System.out.println(user.getPassword());
        if (currentUser!=null){
            if (passwordEncoder.matches(user.getPassword(),currentUser.getPassword())){
                return currentUser;
            }
        }
        return null;
    }

    @Override
    public ResultBean checkIsLogin(String uuid) {

        System.out.println("当前用户的登录状态-----> "+uuid);
        if (uuid==null){
            return new ResultBean(1,null,"当前用户未登录");
        }
        //根据cookie的uuid去后去redis中的凭证信息
        String key=new StringBuilder(RedisConstant.USER_TOKEN_PRE).append(uuid).toString();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        TUser currentUser = (TUser) redisTemplate.opsForValue().get(key);
        if (currentUser!=null){
            //刷新redis有效期
            redisTemplate.expire(key,30, TimeUnit.MINUTES);
            return new ResultBean(0,currentUser,"");
        }

        return new ResultBean(1,null,"当前用户未登录");
    }
}
