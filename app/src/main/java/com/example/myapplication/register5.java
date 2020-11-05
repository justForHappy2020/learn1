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

public class register5 extends AppCompatActivity implements View.OnClickListener{
    private Intent intentAccept;
    private String file_url;
    private String nickName;
    private String gender;
    private Long userId;
    private Double weight;
    private Double height;

    private EditText etRegWeight;
    private EditText etRegHeight;
    private Button btRegNext;
    private SharedPreferences readSP;


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

    }

    private void initData() {
        intentAccept = getIntent();
        file_url = intentAccept.getStringExtra("file_url");
        nickName = intentAccept.getStringExtra("nickName");
        gender = intentAccept.getStringExtra("gender");
        readSP = getSharedPreferences("saved_mobile",MODE_PRIVATE);
        userId = readSP.getLong("userId",userId);//?
    }

    public void onClick(View view){
        weight = Double.parseDouble(etRegWeight.getText().toString().trim());
        height = Double.parseDouble(etRegHeight.getText().toString().trim());
        if(weight*10%1 == 0&&height*10%1 == 0) {

        }
        else Toast.makeText(this,  "最多一位小数，请重新输入", Toast.LENGTH_SHORT).show();
    }
}



