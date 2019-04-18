package fragment;


import android.Manifest;
import android.app.Activity;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.zkzn.dz_1.FragmentActivity;
import com.example.zkzn.dz_1.R;

import adapter.BlueAdapter;
import controller.MyApplication;
import model.BlueInfo;
import service.MyService;
import thread.ConnectThread;

import static android.content.Context.BIND_AUTO_CREATE;


public class ConnectBlueFragment extends Fragment{
    private View mView;
    private Intent bindIntent;
    private Button btn_discovery;
    private RecyclerView mRecyclerView;

    //private ArrayList<BlueInfo> MyApplication.allBlueInfo = new ArrayList<>();
    private BlueAdapter mBlueAdapter;
    int bluePosition = 0;
    boolean connectFlag = false;
    boolean repeatFlag = true;
    private ConnectThread thread;
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
                if (MyApplication.allBlueInfo.size() > 0){
                    for (int i =0;i < MyApplication.allBlueInfo.size();i++){
                        if (MyApplication.allBlueInfo.get(i).getAddress().equals(address)){
                            MyApplication.allBlueInfo.get(i).setState("已连接");
                            mBlueAdapter.notifyDataSetChanged();
                        }
                    }
                }


            }else if (flag == 11111){

                String address = (String) msg.obj;

                if (MyApplication.allBlueInfo.size() > 0){
                    for (int i =0;i < MyApplication.allBlueInfo.size();i++){
                        if (MyApplication.allBlueInfo.get(i).getAddress().equals(address)){
                            MyApplication.allBlueInfo.get(i).setState("连接失败");
                            mBlueAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }else  if(flag == 10000){
                mBlueAdapter.notifyDataSetChanged();
            }



        }
    };

    //声明及实现接口的方法
    private FragmentActivity.onListener myOnClick = new FragmentActivity.onListener() {


        @Override
        public void OnListener(int flag) {
           // Log.i("wp123","接收到回调信息了");
            //表示蓝牙断开连接
            mHandler.obtainMessage(flag, 1, -1, "").sendToTarget();
        }
    };
    private FragmentActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //绑定Service

        mView = inflater.inflate(R.layout.connect_fragment, container, false);

        btn_discovery = (Button) mView.findViewById(R.id.btn_discovery);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.RecyclerView);
        bindIntent = new Intent(this.getContext(), MyService.class);
        getActivity().getApplicationContext().bindService(bindIntent, connection, BIND_AUTO_CREATE);
        //注册广播接收器
        registerBroadcast();

        /*activity.setListener(new FragmentActivity.onListener() {
           @Override
           public void OnListener(int flag) {
               Log.i("wp123","接收到回调信息了");
               mHandler.obtainMessage(flag, 1, -1, "").sendToTarget();
           }
       });*/

        btn_discovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //安卓6.0蓝牙搜索还要动态设置模糊定位权限
                requestBluetoothPermission();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBlueAdapter = new BlueAdapter(MyApplication.allBlueInfo);
        mRecyclerView.setAdapter(mBlueAdapter);
        //设置Item增加、移除动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        //列表中按钮的点击事件
        mBlueAdapter.setOnItemClickLitener(new BlueAdapter.OnItemClickLitener()
        {

            @Override
            public void onItemClick(View view, int position)
            {
                //判断是否在搜索，是的话就取消搜索
              //  mBinder.cancelDiscovered();

                if (MyApplication.allBlueInfo.get(position).getState().equals("已连接")){
                    Toast.makeText(getActivity(), "该蓝牙已经连接过了！",
                            Toast.LENGTH_SHORT).show();
                }else {
                    String deviceAddress = MyApplication.allBlueInfo.get(position).getAddress();
                    mBinder.connectd(deviceAddress,position);
                }
            }

        });
        return mView;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity  = (FragmentActivity) context;
            //注册接口
        activity.setListener(myOnClick);
    }
    private void openBluetooth() {
        //蓝牙未开启时开启蓝牙
        mBinder.openBluetooth();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBlueAdapter.notifyDataSetChanged();
    }

    //注册广播接收器
    private void registerBroadcast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver,intentFilter);

        //监听系统蓝牙状态的广播
        IntentFilter filter1=new IntentFilter();
        filter1.addAction("android.intent.action.connectfailed");
        filter1.addAction("android.intent.action.connect");
        filter1.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        getActivity().registerReceiver(connectReceiver, filter1);



    }
    //监听系统的蓝牙断开信息
    private final BroadcastReceiver connectReceiver = new BroadcastReceiver() {
        public void onReceive (Context context, Intent intent) {
            String action = intent.getAction();

          if(intent.getAction().equals("android.intent.action.connectfailed")){
                Toast.makeText(MyApplication.getContext(),"蓝牙连接失败", Toast.LENGTH_SHORT).show();
                    String message = intent.getStringExtra("message");
                Log.i("wp123","message = "+message);
                mHandler.obtainMessage(11111, 1, -1, message).sendToTarget();
            }else if(intent.getAction().equals("android.intent.action.connect")){
              //Toast.makeText(MyApplication.getContext(),"蓝牙连接失败", Toast.LENGTH_SHORT).show();
              String message = intent.getStringExtra("message");
              mHandler.obtainMessage(12345, 1, -1, message).sendToTarget();
           }else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
              Log.i("wp123", "ConnectBlueFragment 中的断开广播收到 ");
               /* BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("wp123", " device = "+device);
                Toast.makeText(MyApplication.getContext(), device.getName()+"蓝牙断开连接", Toast.LENGTH_SHORT).show();

                if (MyApplication.allBlueInfo.size() > 0){
                    for (int i =0 ;i < MyApplication.allBlueInfo.size();i++){
                        if(MyApplication.allBlueInfo.get(i).getAddress().equals(device.getAddress())){
                            MyApplication.allBlueInfo.get(i).setState("已断开");
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
                }*/

            }
        }

    };

    //广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
               // Toast.makeText(getActivity(),"发现设备"+device.getName(),Toast.LENGTH_SHORT).show();
                Log.i("wp123","MyApplication.allBlueInfo.size = "+MyApplication.allBlueInfo.size());
                //防止重复添加设备信息
                if (MyApplication.allBlueInfo != null && MyApplication.allBlueInfo.size() > 0){
                    for (int i = 0; i< MyApplication.allBlueInfo.size();i++){

                        if (MyApplication.allBlueInfo.get(i).getAddress().equals(device.getAddress())){
                            //表示包含重复蓝牙设备地址
                            repeatFlag = false;
                        }
                    }
                    Log.i("wp123","repeatFlag = "+repeatFlag);
                    if (repeatFlag){
                        MyApplication.allBlueInfo.add(new BlueInfo(device.getName(),device.getAddress(),"未连接"));
                        mBlueAdapter.notifyDataSetChanged();
                        repeatFlag = true;
                    }
                }else if (MyApplication.allBlueInfo != null && MyApplication.allBlueInfo.size() == 0){
                    MyApplication.allBlueInfo.add(new BlueInfo(device.getName(),device.getAddress(),"未连接"));
                    mBlueAdapter.notifyDataSetChanged();
                }

                // 搜索完成时
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                Toast.makeText(getActivity(), "搜索完成，点击连接蓝牙", Toast.LENGTH_SHORT).show();
                btn_discovery.setText("搜索完成");
            }
        }
    };
    //安卓开发6.0蓝牙权限授权
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        Log.i("wp123", "requestCode=" +requestCode);

        switch (requestCode) {
            case 23:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //授权成功
                   // Toast.makeText(getActivity(), "正在搜索蓝牙设备...", Toast.LENGTH_SHORT).show();
                    btn_discovery.setText("正在搜索蓝牙设备...");
                    Log.i("wp123","MBinder = "+ mBinder);
                    mBinder.doDiscovery();
                } else {
                    //授权拒绝
                    Toast.makeText(getActivity(), "授权失败！", Toast.LENGTH_SHORT).show();

                }
                break;


        }
    }

    private void requestBluetoothPermission(){
        //判断系统版本
        Log.i("wp123", "系统版本为" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
            //判断这个权限是否已经授权过

            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){

                //判断是否需要 向用户解释，为什么要申请该权限,该方法只有在用户在上一次已经拒绝过你的这个权限申请才会调用。
                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION))
                    Toast.makeText(getActivity(), "Need bluetooth permission.", Toast.LENGTH_SHORT).show();

                  /*  参数1 Context
                * 参数2 需要申请权限的字符串数组，支持一次性申请多个权限，对话框逐一询问
                * 参数3 requestCode 主要用于回调的时候检测*/
                Log.i("wp123", "ActivityCompat 1 ");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},23);
                return;
            }else{
                Log.i("wp123", "ActivityCompat 2 ");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 23);
                btn_discovery.setText("正在搜索蓝牙设备...");
                mBinder.doDiscovery();

            }
        } else {
           // Toast.makeText(getActivity(), "正在搜索蓝牙设备...", Toast.LENGTH_SHORT).show();
            btn_discovery.setText("正在搜索蓝牙设备...");
            mBinder.doDiscovery();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 取消广播注册
        getActivity().unregisterReceiver(mReceiver);
        getActivity().unregisterReceiver(connectReceiver);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消广播注册
       /* getActivity().unregisterReceiver(mReceiver);
        getActivity().unregisterReceiver(connectReceiver);*/

    }


}
