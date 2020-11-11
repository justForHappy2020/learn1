package com.example.myapplication;
import com.example.myapplication.utils.KeyboardUtils;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class register2 extends AppCompatActivity implements View.OnClickListener{

    private TextView tvGetPhoneNum;
    private EditText etVcode;
    private Button btNameNext;
    private TextView tvRequireAgain;
    private Intent intentAccept;
    private SharedPreferences saveSP;

    private Long userId;
    private String token;
    private Boolean isNewUser;
/*    private SharedPreferences readSP;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        initView();
        initData();

        //设置延迟并弹出小键盘
        etVcode.setFocusable(true);
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etVcode,InputMethodManager.SHOW_FORCED);
                etVcode.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus)KeyboardUtils.showKeyboard(etVcode);
                        else KeyboardUtils.hideKeyboard(etVcode);
                    }
                });
            }
        },998);
    }

    private void initData(){
/*        readSP = getSharedPreferences("saved_mobile",MODE_PRIVATE);
        String phoneNum = readSP.getString("mobile","");*/
        intentAccept = getIntent();
        String phoneNum = intentAccept.getStringExtra("mobile");

        if(phoneNum.equals("")) Toast.makeText(this,"没有手机号数据",Toast.LENGTH_SHORT).show();
        else tvGetPhoneNum.setText("+86-"+phoneNum);
    }

    private void initView(){
        tvGetPhoneNum = findViewById(R.id.getphone_num);
        etVcode = findViewById(R.id.vcode);
        btNameNext = findViewById(R.id.name_next);
        tvRequireAgain = findViewById(R.id.require_again);

        saveSP = getSharedPreferences("saved_token",MODE_PRIVATE);

/*        //弹出小键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘*/

        btNameNext.setOnClickListener(this);
        tvRequireAgain.setOnClickListener(this);

      /*  etVcode.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)KeyboardUtils.showKeyboard(etVcode);
                else KeyboardUtils.hideKeyboard(etVcode);
            }
        });*/
        //tvRequireAgain.setEnabled(Boolean.FALSE);

    }

    public void onFocusChange(View v,boolean hasFocus){

    }
    public void onClick(View view){
        Intent intent = new Intent(this,register3.class);
        Intent intent2 = new Intent(this, course_main.class);
        final String mobile = intentAccept.getStringExtra("mobile");
        switch (view.getId()){
            case R.id.require_again:
                intentAccept = getIntent();
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
                Toast.makeText(this,"验证码已重新发送",Toast.LENGTH_SHORT).show();

                break;
            case R.id.name_next:
                final String code = etVcode.getText().toString().trim();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //设置JSON数据
                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                            JSONObject json = new JSONObject();
                            try {
                                json.put("code", code);
                                json.put("phoneNumber",mobile);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //okhttp请求
                            OkHttpClient client = new OkHttpClient();
                            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                            Request request = new Request.Builder()
                                    .url("http://127.0.0.1:8080/api/user/login")
                                    .post(requestBody)
                                    .build();
                            Response response = client.newCall(request).execute();
                            if (!response.isSuccessful())
                                throw new IOException("Unexpected code" + response);
                            String responseData = response.body().string();
                            //getfeedback(responseData);
                            try {
                                JSONObject jsonObject1 = new JSONObject(responseData);
                                JSONArray jsonArray = jsonObject1.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    //相应的内容
                                    isNewUser = jsonObject.getBoolean("isNewUser");
                                    userId = jsonObject.getLong("userId");
                                    token = jsonObject.getString("token");
                                }
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                            SharedPreferences.Editor editor = saveSP.edit();
                            editor.putLong("userId",userId);
                            editor.putString("token",token);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

/*                    private void getfeedback(String responseData) {
                        try {
                            JSONObject jsonObject1 = new JSONObject(responseData);
                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                //相应的内容
                                String message = jsonObject.getString("message");
                                String data = jsonObject.getString("data");
                                int code = jsonObject.getInt("code");
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }*/

                }).start();
/*                //判定是否新用户，新用户跳转注册页面，旧用户跳转主页
                if(isNewUser) startActivity(intent);
                else startActivity(intent2);*/
                startActivity(intent);
                break;
        }
    }
}