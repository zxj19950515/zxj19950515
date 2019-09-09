package com.qf.qfv2servicesms.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.v2.sms.api.ISmsServce;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Service
public class SmsServiceImpl implements ISmsServce {

    static String account = "JSM42639";
    static String password = "l43d7myq";
    static String veryCode = "irnsloy2kjhd";
    static String http_url  = "http://112.74.76.186:8030";

    @Override
    public void sendSms(String phoneNumber, String content) {

        sendSms1(phoneNumber,content);
    }

    public  String sendSms1(String mobile,String content){
        String sendSmsUrl = http_url + "/service/httpService/httpInterface.do?method=sendMsg";
        StringBuilder param = new StringBuilder();
        param.append("&username=").append(account);
        param.append("&password=").append(password);
        param.append("&veryCode=").append(veryCode);
        param.append("&mobile=").append(mobile);
        param.append("&content=").append(content);
        param.append("&msgtype=").append("1");
        param.append("&code=").append("utf-8");
        StringEntity entity;
        entity = new StringEntity(param.toString(),"utf-8");
        entity.setContentType("application/x-www-form-urlencoded");
        entity.setContentEncoding("UTF-8");
        String result = sendPostHttp(sendSmsUrl, entity);
        System.out.println(result);
        return result;
    }

    /**
     * 发送HttpPOST请求
     * @param postUrl 请求地址
     * @param entity 请求参数实体
     * @return
     * String
     */
    public static String sendPostHttp(String postUrl,StringEntity entity){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String str = "";
        HttpPost method = new HttpPost(postUrl);
        method.setEntity(entity);
        try {
            HttpResponse result = httpClient.execute(method);
            if(result.getStatusLine().getStatusCode() == 200){
                str = EntityUtils.toString(result.getEntity());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
}
