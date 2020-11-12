package com.example.myapplication.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpUtils {

    static public String connectHttp(String url,JSONObject json) throws Exception {
        //设置JSON数据
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        //okhttp请求
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60000, TimeUnit.MILLISECONDS)
                .readTimeout(30000, TimeUnit.MILLISECONDS)
                .build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        System.out.println("requestBody");
        System.out.println(requestBody);
        System.out.println("request");
        System.out.println(request);

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful())
        {throw new IOException("Unexpected code" + response);}
        String responseData = response.body().string();
        return  responseData;
    }
}
