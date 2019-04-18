package thread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import controller.MyApplication;


/**
 * Created by user on 2018/12/7.
 */

public class ConnectThread extends Thread {
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private BluetoothAdapter mAdapter;
    private Handler mHandler;
    int arg = 0;
    private ReadThread read;
    public ConnectThread(BluetoothDevice device, BluetoothAdapter adapter, Handler handler,int arg){
            mDevice = device;
        BluetoothSocket tmp = null;
        mAdapter = adapter;
        mHandler = handler;
        this.arg = arg;

        try {
            tmp = mDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        mSocket = tmp;


    }

    public BluetoothDevice getDevice(){
        return mDevice;
    }

    @Override
    public void run() {
        mAdapter.cancelDiscovery();

        try {
            Log.i("wp123","ConnectThread中 mHandler1 = "+mHandler);
             mSocket.connect();


        } catch (IOException e) {
            e.printStackTrace();
            try {
                Log.i("wp123","蓝牙连接失败 ");
                Message msg=mHandler.obtainMessage();
                msg.arg1 = arg;
                msg.what = 11111;
                msg.obj = mDevice.getAddress();
                msg.sendToTarget();
                mSocket.close();
            } catch (IOException closeException) {

            }
            return;
        }
        Log.i("wp123","ConnectThread中 mSocket = "+mSocket);
        if(mSocket!=null){

            manageConnectedSocket(mSocket,arg);
            MyApplication.addThreadsList(this);
            Message msg=mHandler.obtainMessage();
            msg.arg1 = arg;
            msg.what = 12345;
            msg.obj = mDevice.getAddress();
            msg.sendToTarget();
        }

    }
    public void cancel() {
        try {
            read.close();
            mSocket.close();
        } catch (IOException e) {

        }
    }

    private void manageConnectedSocket(BluetoothSocket socket,int arg) {
        read = new ReadThread(mDevice,socket,mHandler,arg);//开启读取数据线程；
        read.start();

    }

    public BluetoothSocket getsocket(){
        if (mSocket != null) {
            return mSocket;
        }
        return  null;
    }
}
