package com.example.administrator.testonly;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2018/3/19 0019.
 */

public class OkHttpUtil {
    //封装Okhttp
    //地址，回调参数，用户名和密码的map表
    public static void post(String address, okhttp3.Callback callback, Map<String,String> map){
        OkHttpClient client = new OkHttpClient();//创建对象
        FormBody.Builder builder = new FormBody.Builder();//创建表单请求体
        if (map!=null){
            for (Map.Entry<String,String> entry:map.entrySet()){
                builder.add(entry.getKey(),entry.getValue());
            }
        }
        FormBody body = builder.build();//传递键值对参数
        Request request = new Request.Builder()//创建request对象
                .url(address)
                .post(body)//传递请求体
                .build();
        client.newCall(request).enqueue(callback);//回调方法的使用与get异步请求相同
    }

}
