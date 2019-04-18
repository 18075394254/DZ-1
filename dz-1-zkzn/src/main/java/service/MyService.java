package service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import java.util.Date;

import controller.BluetoothCollector;
import controller.MyApplication;
import thread.ConnectThread;
import utils.BlueUtils;
import utils.ToastUtils;

/**
 * Created by Administrator on 16-11-18.
 */
public class MyService extends Service {

    private DiscoveryBinder mBinder=new DiscoveryBinder();
    private int state=0;
    private Intent intent;
    Handler mHandler ;
    BluetoothCollector mBluetoothCollector;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("wp123","服务中的onCreate开启");
    }

    @Override
    public int onStartCommand(final Intent intent1, int flags, int startId) {
        Log.i("wp123","服务中的onStartCommand开启");
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                 int flag = msg.what;
                Log.i("wp123","MyService中的Hander接收到信息");
                intent = new Intent();

                if (flag == 12345){
                    String message = (String) msg.obj;
                    intent.putExtra("position",msg.arg1);
                    intent.putExtra("flag",msg.what);
                    intent.putExtra("message",message);
                    intent.setAction("android.intent.action.connect");
                }else if(flag == 11111){
                    String message = (String) msg.obj;
                    intent.putExtra("position",msg.arg1);
                    intent.putExtra("flag",msg.what);
                    intent.putExtra("message",message);
                    intent.setAction("android.intent.action.connectfailed");
                }else if(flag == 10000){
                    Bundle bundle = msg.getData();
                    intent.putExtras(bundle);
                    intent.setAction("android.intent.action.ontestfragment");
                }

                Log.i("wp123","Service 发送蓝牙连接广播");
                sendBroadcast(intent);

            }
        };
        Log.i("wp123","Service中 mHandler = "+mHandler);
        mBluetoothCollector = new BluetoothCollector(mHandler);
        //开启蓝牙
        mBluetoothCollector.openBluetooth();

        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public IBinder onBind(Intent intent) {
        Log.i("wp123", "onBind()");
        return mBinder;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("wp123", "onDestroy();");

    }

    public class DiscoveryBinder extends Binder {

        //连接设备
        public void connectd(String deviceAddress,int position){

            mBluetoothCollector.connectBlue(deviceAddress,position);
        }

        //发送字符串
        public void sendMessage(String message, int activity){
           // bt.send(message, true);
            state=activity;
            if (MyApplication.allThreads.size() > 0){
                for(int i =0; i < MyApplication.allThreads.size();i++){
                    BlueUtils.sendMessage(message,MyApplication.allThreads.get(i).getsocket());

                }
            }else{
                ToastUtils.showShort(MyApplication.getContext(),"当前无可用蓝牙");
            }
        }
        //发送字符数组
        public void sendbytes(byte[] bytes,int activity){
          //  bt.send(bytes,false);
            state=activity;
        }
        //发送字符数组
        public void setState(int activity){
            state=activity;
        }

        //开启蓝牙
        public void openBluetooth(){
            mBluetoothCollector.openBluetooth();
        }

        //关闭蓝牙
        public void closeBluetooth(){
            mBluetoothCollector.closeBluetooth();
        }

        //取消搜索
        public void cancelDiscovered(){
            mBluetoothCollector.canDiscovered();
        }

        //开始搜索
        public void doDiscovery() {
            mBluetoothCollector.doDiscovery();
        }


        public void closeConnect(){
          //  bt.stopService();
        }


    }


}
