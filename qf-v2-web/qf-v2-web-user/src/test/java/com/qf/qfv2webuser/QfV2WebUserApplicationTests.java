package com.qf.qfv2webuser;

import com.sun.javafx.fxml.builder.URLBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QfV2WebUserApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void testBasicFunc() throws IOException {

        //打开浏览器
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //输入网址
        String url="https://www.baidu.com";
        System.out.println(url);
        //敲回车,get请求
        HttpGet get=new HttpGet(url);
        //获取响应信息
        CloseableHttpResponse response = httpClient.execute(get);
        //解析响应信息
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode==200){
            //成功响应
            //获取响应信息
            HttpEntity entity = response.getEntity();
            //这个输入流对应服务器资源
            InputStream inputStream = entity.getContent();
            //输出
            int len=0;
            byte[] bs=new byte[1024];
            while ((len=inputStream.read(bs))!=-1){
                System.out.println(new String(bs,0,len));
            }
        }


    }

    @Test
    public void testHttpClient() throws IOException {

        //打开浏览器
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //输入网址
        String url="https://www.jd.com/?cu=true&utm_source=baidu-pinzhuan&utm_medium=cpc&utm_campaign=t_288551095_baidupinzhuan&utm_term=0f3d30c8dba7459bb52f2eb5eba8ac7d_0_58cb282a53df4640806893f7a5d75c23";
        System.out.println(url);
        //敲回车 get请求
        HttpGet get=new HttpGet(url);
        //获取响应信息
        CloseableHttpResponse response = httpClient.execute(get);
        //解析响应信息
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode==200){
            HttpEntity entity = response.getEntity();
            String info = EntityUtils.toString(entity, "utf-8");
            System.out.println(info);
        }
    }

    @Test
    public void testHttpClientWithParams() throws URISyntaxException, IOException {

        //打开浏览器
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url="https://www.baidu.com";
        System.out.println(url);
        URIBuilder builder=new URIBuilder(url);
        builder.addParameter("username","zhangsan");
        builder.addParameter("password","123");
        //敲回车 get请求
        HttpGet get=new HttpGet(builder.build());
        //获取响应信息
        CloseableHttpResponse response = httpClient.execute(get);
        //解析响应信息
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode==200){
            //成功响应
            HttpEntity entity = response.getEntity();
            String info = EntityUtils.toString(entity, "utf-8");
            System.out.println(info);
        }
    }

}
