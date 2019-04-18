package com.example.zkzn.dz_1;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import controller.BaseActivity;
import service.MyService;

public class MainActivity extends BaseActivity {
    private Button connect,setinfo,ontest,openfile,exit;
    //配置信息
    public static String s_mLiftId="";
    public static String s_mOperator="";
    public static String s_mLocation="";
    public static String s_mCompany="";
    public static String s_mRatedSpeed="";
    public static String s_mRatedLoad="";
    public static String s_mSupplement="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化控件
        initView();
        //点击事件
        onButtonClick();
        //获得设置的参数信息的方法
        loadConfig();

    }

    //初始化控件
    private void initView() {
        connect = getView(R.id.connect);
        setinfo = getView(R.id.setinfo);
        ontest  = getView(R.id.ontest);
        openfile = getView(R.id.openfile);
        exit = getView(R.id.exit);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    //点击事件
    private void onButtonClick() {
        //蓝牙连接
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this,BluetoothConnectActivity.class));
            }
        });

        //设置信息
        setinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //开始测试
        ontest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //打开文件
        openfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //退出程序
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    //获得设置的参数信息
    public void loadConfig()
    {
        SharedPreferences sp = getSharedPreferences("info", Context.MODE_PRIVATE);
        s_mLiftId = sp.getString("liftid", "");
        s_mOperator = sp.getString("operator", "");
        s_mLocation = sp.getString("location", "");
        s_mCompany = sp.getString("company", "");
        s_mRatedSpeed = sp.getString("ratedSpeed", "");
        s_mRatedLoad = sp.getString("ratedLoad", "");
        s_mSupplement = sp.getString("supplement", "");
    }
}
