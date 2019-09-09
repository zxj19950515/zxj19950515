package com.qf.v2.user.api;

import com.qf.v2.common.IBaseService;
import com.qf.v2.common.pojo.ResultBean;
import com.qf.v2.entity.TUser;

public interface IUserService extends IBaseService<TUser> {

    //检查用户是否存在
    TUser checkIsExists(TUser user);

    //检查用户是否登录
    ResultBean checkIsLogin(String uuid);
}
