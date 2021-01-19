package com.example.crawlerjddemo.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.UUID;

@Component
public class HttpUtils {

    private PoolingHttpClientConnectionManager cm;

    private static final String picPath = "D:\\images\\";

    public HttpUtils(){
        this.cm = new PoolingHttpClientConnectionManager();

        //设置最大连接数
        this.cm.setMaxTotal(100);
        //设置主机最大连接数
        this.cm.setDefaultMaxPerRoute(10);
    }

    /**
     *
     * 根据请求地址下载页面数据
     * @param url
     * @return 页面数据
     */
    public String doGetHtml(String url){
        //获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        //设置httpGet请求对象，设置url地址
        HttpGet httpGet = new HttpGet(url);

        //设置请求信息
        httpGet.setConfig(this.getConfig());
        httpGet.setHeader("Accept", "*/*");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        httpGet.setHeader("refer", "https://search.jd.com/");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36");

        //使用httpClient发起请求，获取响应
        CloseableHttpResponse response= null;
        try {
            response = httpClient.execute(httpGet);
            //解析响应，返回结果
            if (response.getStatusLine().getStatusCode() == 200){
                //判断响应体entity是否不为空，如果不为空，则可以使用EntityUtils
                if(response.getEntity()!=null){
                    String content = EntityUtils.toString(response.getEntity(), "utf8");
                    return content;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (response!=null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "";

    }


    /**
     * 下载图片
     * @param url
     * @return 图片名称
     */
    public String doGetImage(String url){
        //获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        //设置httpGet请求对象，设置url地址
        HttpGet httpGet = new HttpGet(url);

        //设置请求信息
        httpGet.setConfig(this.getConfig());

        //使用httpClient发起请求，获取响应
        CloseableHttpResponse response= null;
        try {
            response = httpClient.execute(httpGet);
            //解析响应，返回结果
            if (response.getStatusLine().getStatusCode() == 200){
                //下载图片
                //判断响应体entity是否不为空，如果不为空，则可以使用EntityUtils
                if(response.getEntity()!=null){
                    //获取图片后缀
                    String extName = url.substring(url.lastIndexOf("."));

                    //创建图片名，重命名图片
                    String picName = UUID.randomUUID().toString()+extName;
                    //下载图片
                    //声明outputStream
                    OutputStream outputStream = new FileOutputStream(new File(picPath+picName));
                    response.getEntity().writeTo(outputStream);

                    //返回图片名称
                    return picName;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (response!=null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //如果有问题，返回空串
        return "";
    }

    // 设置请求信息
    private RequestConfig getConfig() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(1000) //创建连接的最长时间
                .setConnectionRequestTimeout(500) //获取连接的最长时间
                .setSocketTimeout(100000) //数据传输的最长时间
                .build();

        return config;
    }
}
