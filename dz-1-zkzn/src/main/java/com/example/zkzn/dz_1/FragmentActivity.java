package com.example.zkzn.dz_1;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapter.MyFragmentAdapter;
import controller.MyApplication;
import fragment.ConnectBlueFragment;
import fragment.OnTestFragment;
import fragment.OpenFileFragment;
import fragment.SetInfoFragment;

public class FragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        initView();
        //监听系统蓝牙断开状态的广播
        IntentFilter filter1=new IntentFilter();
        filter1.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        this.registerReceiver(connectBreakReceiver, filter1);
    }

    /**
     * 定义一个接口
     */
    public interface onListener{
        void OnListener(int flag);
    }
    /**
     *定义一个变量储存数据
     */
    private onListener listener;
    /**
     *提供公共的方法,并且初始化接口类型的数据
     */
    public void setListener(onListener listener){
        this.listener = listener;
    }
    /**
     * 初始化各控件
     */
    private void initView(){

        //获取数据 在values/arrays.xml中进行定义然后调用
        String[] tabTitle = getResources().getStringArray(R.array.tab_titles);
        //将fragment装进列表中
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new ConnectBlueFragment());
        fragmentList.add(new SetInfoFragment());
        fragmentList.add(new OnTestFragment());
        fragmentList.add(new OpenFileFragment());

        //声明viewPager
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        //viewpager加载adapter
        viewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), fragmentList, tabTitle));
        //viewPager事件
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //定义TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        //TabLayout的事件
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //选中了tab的逻辑
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //未选中tab的逻辑
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //再次选中tab的逻辑
            }
        });
        //TabLayout加载viewpager
        //一行代码和ViewPager联动起来，简单粗暴。
        tabLayout.setupWithViewPager(viewPager);
        Drawable d = null;
        for (int i = 0; i < tabLayout.getTabCount(); i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            switch (i){
                case 0:
                    d = getResources().getDrawable(R.drawable.tab_bluetooth);
                    break;
                case 1:
                    d = getResources().getDrawable(R.drawable.tab_setinfo);
                    break;
                case 2:
                    d = getResources().getDrawable(R.drawable.tab_test);
                    break;
                case 3:
                    d = getResources().getDrawable(R.drawable.tab_openfile);
                    break;
                case 4:
                   // d = getResources().getDrawable(R.drawable.tab5);
                    //tintManager.setStatusBarTintResource(R.color.colorAccent);
                    break;
            }
            tab.setIcon(d);
        }


    }
    //监听系统的蓝牙断开信息
    private final BroadcastReceiver connectBreakReceiver = new BroadcastReceiver() {
        public void onReceive (Context context, Intent intent) {
            String action = intent.getAction();

           if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
               Log.i("wp123", "FragmentActivity 中的断开广播收到 ");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("wp123", " device = "+device);
                Toast.makeText(MyApplication.getContext(), device.getName()+"蓝牙断开连接", Toast.LENGTH_SHORT).show();

                if (MyApplication.allBlueInfo.size() > 0){
                    for (int i =0 ;i < MyApplication.allBlueInfo.size();i++){
                        if(MyApplication.allBlueInfo.get(i).getAddress().equals(device.getAddress())){
                            MyApplication.allBlueInfo.get(i).setState("已断开");
                            //mBlueAdapter.notifyItemChanged(i);
                        }
                    }
                }
                if (MyApplication.allThreads.size() > 0){
                    for (int i =0 ;i < MyApplication.allThreads.size();i++){
                        if(MyApplication.allThreads.get(i).getDevice().getAddress().equals(device.getAddress())){
                            MyApplication.allThreads.get(i).cancel();
                            MyApplication.allThreads.remove(i);
                            break;
                        }

                    }
                }

                /**
                * 在合适的位置给其调用接口，给其赋值
                */
                Log.i("wp123","listener = "+listener);
               if (listener!=null) {
                   listener.OnListener(10000);
               }

            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(connectBreakReceiver);
    }
}
