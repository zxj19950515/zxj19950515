package com.qf.v2.mail.api;

import com.qf.v2.entity.TUser;

public interface IMailService {

    void sendActiveMail(String subject, String to, TUser user);
}
