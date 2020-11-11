package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class register5 extends AppCompatActivity implements View.OnClickListener{
    private Intent intentAccept;
    private String file_url;
    private String nickName;
    private String gender;
    private Long userId;
    private Double weight;
    private Double height;
    private String token;

    private EditText etRegWeight;
    private EditText etRegHeight;
    private Button btRegNext;
    private SharedPreferences readSP;
    private SharedPreferences saveSP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register5);

        initView();
        initData();
    }

    private void initView(){
        etRegWeight = findViewById(R.id.reg_weight);
        etRegHeight = findViewById(R.id.reg_height);
        btRegNext = findViewById(R.id.reg_next);

        btRegNext.setOnClickListener(this);

        saveSP = getSharedPreferences("saved_token",MODE_PRIVATE);
    }

    private void initData() {
        intentAccept = getIntent();
        file_url = intentAccept.getStringExtra("file_url");
        nickName = intentAccept.getStringExtra("nickName");
        gender = intentAccept.getStringExtra("gender");
        readSP = getSharedPreferences("saved_mobile",MODE_PRIVATE);
        userId = readSP.getLong("userId",0);
    }

    public void onClick(View view){
        Intent intent = new Intent(this,course_main.class);
        if(etRegWeight.getText().toString().trim()!=null)weight = Double.parseDouble(etRegWeight.getText().toString().trim());
        if(etRegHeight.getText().toString().trim()!=null)height = Double.parseDouble(etRegHeight.getText().toString().trim());
        if(weight*10%1 == 0&&height*10%1 == 0) {
            //http请求
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //设置JSON数据
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JSONObject json = new JSONObject();
                        try {
                            json.put("userId", userId);
                            json.put("nickName", nickName);
                            json.put("headPortraitUrl", file_url);
                            json.put("gender", gender);
                            json.put("weight", weight);
                            json.put("height", height);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //okhttp请求
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                        Request request = new Request.Builder()
                                .url("http://127.0.0.1:8080/api/user/setProfile")
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
                                token = jsonObject.getString("token");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor editor = saveSP.edit();
                        editor.putString("token",token);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }).start();
            startActivity(intent);
        }
        else Toast.makeText(this,  "最多一位小数，请重新输入", Toast.LENGTH_SHORT).show();
    }
}


