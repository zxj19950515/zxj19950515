package com.qf.v2.mapper;

import com.qf.v2.common.IBaseDao;
import com.qf.v2.entity.TUser;

public interface TUserMapper extends IBaseDao<TUser> {

    TUser selectByUsername(String username);
}