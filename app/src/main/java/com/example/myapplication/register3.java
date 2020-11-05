package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class register3 extends AppCompatActivity implements View.OnClickListener {

    private ImageButton ibUpdatePhoto;
    private TextView tvName;
    private Button btNameNext;
    private String file_url;
    private InputStream headPortrait;
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        initView();
    }

    private void initView() {
        ibUpdatePhoto = findViewById(R.id.update_photo);
        tvName = findViewById(R.id.name);
        btNameNext = findViewById(R.id.name_next);

        btNameNext.setBackgroundColor(Color.GRAY);
        btNameNext.setEnabled(Boolean.FALSE);

        btNameNext.setOnClickListener(this);
        ibUpdatePhoto.setOnClickListener(this);

        //昵称框有输入才可以点下一步
        tvName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(tvName.getText()) && !TextUtils.isEmpty(tvName.getText())) {
                    btNameNext.setBackgroundColor(Color.parseColor("#4CAF50"));
                    btNameNext.setEnabled(Boolean.TRUE);//启用按钮
                } else {
                    btNameNext.setBackgroundColor(Color.GRAY);
                    btNameNext.setEnabled(Boolean.FALSE);//不启用按钮
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    public void onClick(View view) {
        final String nickName = tvName.getText().toString().trim();
        switch (view.getId()) {
            case R.id.update_photo:
                //上传头像


                break;
            case R.id.name_next:
                Intent intent2 = new Intent(this, register4.class);
                if (nickName.length() >= 20) {
                    Intent intent1 = new Intent(this, register3.class);
                    startActivity(intent1);
                    finish();
                    Toast.makeText(this, "昵称过长，请重新输入", Toast.LENGTH_SHORT).show();
                } else {
                    //editor.putString("mobile",mobile);//保存手机号在本地
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //设置JSON数据
                                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                                JSONObject json = new JSONObject();
                                try {
                                    json.put("headPortrait", headPortrait);
                                    json.put("filename", filename);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //okhttp请求
                                OkHttpClient client = new OkHttpClient();
                                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                                Request request = new Request.Builder()
                                        .url("http://127.0.0.1:8080/api/user/uploadImage")
                                        .post(requestBody)
                                        .build();
                                Response response = client.newCall(request).execute();
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code" + response);
                                String responseData = response.body().string();
                                try {
                                    JSONObject jsonObject1 = new JSONObject(responseData);
                                    JSONArray jsonArray = jsonObject1.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        //相应的内容
                                        file_url = jsonObject.getString("file_url");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }).start();
                    intent2.putExtra("file_url", file_url);
                    intent2.putExtra("nickName", nickName);
                    startActivity(intent2);
                    break;
                }
        }
    }
}