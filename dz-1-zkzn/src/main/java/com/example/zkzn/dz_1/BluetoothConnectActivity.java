package com.example.zkzn.dz_1;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import adapter.BlueAdapter;
import controller.BaseActivity;
import controller.MyApplication;
import model.BlueInfo;
import service.MyService;
import thread.ConnectThread;

public class BluetoothConnectActivity extends BaseActivity {
    private Button btn_discovery;
    private RecyclerView mRecyclerView;

    private ArrayList<BlueInfo> infolist = new ArrayList<>();
    private BlueAdapter mBlueAdapter;
    private Intent bindIntent;
    int bluePosition = 0;
    boolean connectFlag = false;
    private ConnectThread thread;
    private MyReceiver receiver;

    //Service与Activity通信的介质
    private MyService.DiscoveryBinder mBinder;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取Service端的Messenger
            mBinder =(MyService.DiscoveryBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }


    };

    //表示当前是设备几
    int currentFlag = 0;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int flag = msg.what;
            Log.i("wp123","flag = "+flag);
            if (flag == 12345){
                int arg = msg.arg1;
                bluePosition = arg;
                connectFlag = true;
                String address = (String) msg.obj;
                //MyApplication.allThreads.add((ConnectThread) msg.obj);
                if (infolist.size() > 0){
                   for (int i =0;i < infolist.size();i++){
                       if (infolist.get(i).getAddress().equals(address)){
                           infolist.get(i).setState("已连接");
                           mBlueAdapter.notifyDataSetChanged();
                       }
                   }
                }


            }else{
               /* // mDeviceName = msg.getData().getString(BluetoothState.DEVICE_NAME);
                // String  mDeviceAddress = msg.getData().getString("address");
                String message = (String) msg.obj;
                mStringList.add("设备 "+flag+" 发送消息："+message);
                myAdapter.notifyDataSetChanged();*/
            }



        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connect);
        btn_discovery = getView(R.id.btn_discovery);
        mRecyclerView = getView(R.id.RecyclerView);
        //注册广播接收器
        registerBroadcast();

        //绑定Service
        bindIntent = new Intent(this, MyService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);


        btn_discovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //安卓6.0蓝牙搜索还要动态设置模糊定位权限
                requestBluetoothPermission();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBlueAdapter = new BlueAdapter(infolist);
        mRecyclerView.setAdapter(mBlueAdapter);
        //设置Item增加、移除动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(BluetoothConnectActivity.this, DividerItemDecoration.HORIZONTAL));
        //列表中按钮的点击事件
        mBlueAdapter.setOnItemClickLitener(new BlueAdapter.OnItemClickLitener()
        {

            @Override
            public void onItemClick(View view, int position)
            {
                //判断是否在搜索，是的话就取消搜索
               mBinder.cancelDiscovered();

                if (infolist.get(position).getState().equals("已连接")){
                    Toast.makeText(BluetoothConnectActivity.this, "该蓝牙已经连接过了！",
                            Toast.LENGTH_SHORT).show();
                }else {
                    String deviceAddress = infolist.get(position).getAddress();
                    mBinder.connectd(deviceAddress,position);
                }
            }

        });
    }

    private void openBluetooth() {
        //蓝牙未开启时开启蓝牙
        mBinder.openBluetooth();
    }

    //注册广播接收器
    private void registerBroadcast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver,intentFilter);

        //监听系统蓝牙状态的广播
        IntentFilter filter1=new IntentFilter();
        filter1.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        filter1.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        BluetoothConnectActivity.this.registerReceiver(connectReceiver, filter1);

        //蓝牙状态以及接收数据的广播
        receiver = new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("android.intent.action.mainActivity");
        filter.addAction("android.intent.action.connect");
        // filter.addAction("android.intent.action.disconnect");
        filter.addAction("android.intent.action.connectfailed");
        BluetoothConnectActivity.this.registerReceiver(receiver, filter);

    }
    //监听系统的蓝牙断开信息
    private final BroadcastReceiver connectReceiver = new BroadcastReceiver() {
        public void onReceive (Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //connectFlag = true;
                //Toast.makeText(MyApplication.getContext(), "连接到蓝牙"+device+" 上 ", Toast.LENGTH_SHORT).show();
                Bundle bundle = intent.getExtras();
                String message=bundle.getString("msg");
                // String message1="连接到蓝牙 " + message + "上";
             /*   int flag = intent.getIntExtra("flag",0);
                int position = intent.getIntExtra("position",0);
                Log.i("wp123","flag = "+flag+" position = "+position);*/
                mHandler.obtainMessage(12345, 0, -1, device.getAddress()).sendToTarget();

            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                Log.d("aaa", " ACTION_ACL_DISCONNECTED");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(MyApplication.getContext(), device.getName()+"蓝牙断开连接", Toast.LENGTH_SHORT).show();

                if (infolist.size() > 0){
                    for (int i =0 ;i < infolist.size();i++){
                        if(infolist.get(i).getAddress().equals(device.getAddress())){
                            infolist.get(i).setState("已断开");
                            mBlueAdapter.notifyItemChanged(i);
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

            }
        }

    };
    //表示蓝牙连接状态以及接收蓝牙发送数据的广播
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            //接收数据信息
            if (intent.getAction().equals("android.intent.action.mainActivity")) {
                Bundle bundle = intent.getExtras();
                String message = bundle.getString("msg");
                mHandler.obtainMessage(0, 1, -1, message).sendToTarget();
                //蓝牙连接信息
            }else if(intent.getAction().equals("android.intent.action.connect")){
                Bundle bundle = intent.getExtras();
                String message=bundle.getString("msg");
               // String message1="连接到蓝牙 " + message + "上";
                int flag = intent.getIntExtra("flag",0);
                int position = intent.getIntExtra("position",0);
                Log.i("wp123","flag = "+flag+" position = "+position);
                mHandler.obtainMessage(flag, position, -1, message).sendToTarget();
                //蓝牙断开连接的状态监听不到，在下方用监听系统的蓝牙状态判断了
            }else if(intent.getAction().equals("android.intent.action.disconnect")){

                //蓝牙连接失败的信息
            }else if(intent.getAction().equals("android.intent.action.connectfailed")){
                Bundle bundle = intent.getExtras();
                String message=bundle.getString("msg");
                mHandler.obtainMessage(3, 1, -1, message).sendToTarget();
            }
        }
    }
    //广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(BluetoothConnectActivity.this,"发现设备"+device.getName(),Toast.LENGTH_SHORT).show();
                //防止重复添加设备信息
                if (infolist != null && infolist.size() > 0){
                    for (int i = 0; i< infolist.size();i++){
                        if (!infolist.get(i).getAddress().equals(device.getAddress())){
                            infolist.add(new BlueInfo(device.getName(),device.getAddress(),"未连接"));
                            mBlueAdapter.notifyDataSetChanged();
                        }
                    }
                }else if (infolist != null && infolist.size() == 0){
                    infolist.add(new BlueInfo(device.getName(),device.getAddress(),"未连接"));
                    mBlueAdapter.notifyDataSetChanged();
                }

                // 搜索完成时
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                Toast.makeText(BluetoothConnectActivity.this, "搜索完成，点击连接蓝牙", Toast.LENGTH_SHORT).show();
                btn_discovery.setText("搜索完成");
            }
        }
    };
    //安卓开发6.0蓝牙权限授权
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        switch (requestCode) {
            case 23:
                Log.i("wp123", "grantResults[0]=" + grantResults[0]);
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //授权成功
                    Toast.makeText(BluetoothConnectActivity.this, "正在搜索蓝牙设备...", Toast.LENGTH_SHORT).show();
                   Log.i("wp123","MBinder = "+ mBinder);
                    mBinder.doDiscovery();
                } else {
                    //授权拒绝
                    Toast.makeText(BluetoothConnectActivity.this, "授权失败！", Toast.LENGTH_SHORT).show();

                }
                break;

        }
    }

    private void requestBluetoothPermission(){
        //判断系统版本
        Log.i("wp123", "系统版本为" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            //判断这个权限是否已经授权过

            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){

                //判断是否需要 向用户解释，为什么要申请该权限,该方法只有在用户在上一次已经拒绝过你的这个权限申请才会调用。
                if(ActivityCompat.shouldShowRequestPermissionRationale(BluetoothConnectActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION))
                    Toast.makeText(this, "Need bluetooth permission.", Toast.LENGTH_SHORT).show();

                  /*  参数1 Context
                * 参数2 需要申请权限的字符串数组，支持一次性申请多个权限，对话框逐一询问
                * 参数3 requestCode 主要用于回调的时候检测*/

                ActivityCompat.requestPermissions(BluetoothConnectActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},23);
                return;
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 23);

            }
        } else {
            Toast.makeText(BluetoothConnectActivity.this, "正在搜索蓝牙设备...", Toast.LENGTH_SHORT).show();
            mBinder.doDiscovery();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消广播注册
        this.unregisterReceiver(mReceiver);
        this.unregisterReceiver(connectReceiver);
        this.finish();
    }
}
