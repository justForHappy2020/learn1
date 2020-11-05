package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

public class register1 extends AppCompatActivity implements View.OnClickListener{

    private EditText etPhoneNum;
    private Button btAcquireCode;

    //private SharedPreferences saveSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);

        initView();
    }

    private void initView(){
        etPhoneNum = findViewById(R.id.phone_num);
        btAcquireCode = findViewById(R.id.acquire_vcode);
        //saveSP = getSharedPreferences("saved_mobile",MODE_PRIVATE);
        btAcquireCode.setOnClickListener(this);//监听获取验证码按钮
        btAcquireCode.setBackgroundColor(Color.GRAY);
        btAcquireCode.setEnabled(Boolean.FALSE);

        //没有输入手机号时为灰色按钮
        etPhoneNum.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(etPhoneNum.getText()) && !TextUtils.isEmpty(etPhoneNum.getText())) {
                    btAcquireCode.setBackgroundColor(Color.parseColor("#4CAF50"));
                    btAcquireCode.setEnabled(Boolean.TRUE);//启用按钮
                }else{
                    //btAcquireCode.setBackgroundColor(Color.GREEN);
                    btAcquireCode.setBackgroundColor(Color.GRAY);
                    btAcquireCode.setEnabled(Boolean.FALSE);//不启用按钮
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    public void onClick(View view){
        final String mobile = etPhoneNum.getText().toString().trim();
        //SharedPreferences.Editor editor = saveSP.edit();//获取SharedPreferences实例保存数据
        Intent intent2 = new Intent(this,register2.class);
        if(mobile.length()!=11){
            Intent intent1 = new Intent(this, register1.class);
            startActivity(intent1);
            finish();
            Toast.makeText(this,  "手机号格式不正确，请重新输入", Toast.LENGTH_SHORT).show();
        }
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //设置JSON数据
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JSONObject json = new JSONObject();
                        try {
                            json.put("phoneNumber", mobile);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //okhttp请求
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                        Request request = new Request.Builder()
                                .url("http://127.0.0.1:8080/api/user/getCode")
                                .post(requestBody)
                                .build();
                        Response response = client.newCall(request).execute();
                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code" + response);
                        String responseData = response.body().string();
                        getfeedback(responseData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                private void getfeedback(String responseData) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(responseData);
                        JSONArray jsonArray = jsonObject1.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            //相应的内容
                            String message = jsonObject.getString("message");
                            Object data = jsonObject.getJSONObject("data");
                            int code = jsonObject.getInt("code");
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }).start();

            //editor.putString("mobile",mobile);//保存手机号在本地
            intent2.putExtra("mobile",mobile);
            startActivity(intent2);
        }
    }
}