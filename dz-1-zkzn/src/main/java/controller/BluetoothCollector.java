package controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.lang.reflect.Method;


import thread.ConnectThread;
import utils.ToastUtils;

/**
 * Created by user on 2019/3/20.
 */

public class BluetoothCollector {
    BluetoothAdapter mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
    Context mContext = MyApplication.getContext();
    private Handler mHandler;
    private ConnectThread thread;

    public BluetoothCollector(/*BluetoothAdapter mBluetoothAdapter*/ Handler handler){
        //this.mBluetoothAdapter = mBluetoothAdapter;
            this.mHandler = handler;
    }

    //打开蓝牙
    public void openBluetooth(){
        //不支持蓝牙功能
        if (mBluetoothAdapter == null){
            ToastUtils.showShort(mContext,"本机不支持蓝牙功能，请更换手机！");
            return;
        }
        //蓝牙功能未打开时打开蓝牙
        if (!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
        }
    }

    //关闭蓝牙
    public void closeBluetooth(){
        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON){
            mBluetoothAdapter.disable();
        }
    }

    //使本机蓝牙在300秒内可被发现
    public void canDiscovered(){
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent discoverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);//设置被发现的时间
            mContext.startActivity(discoverIntent);
        }
    }

    public void connectBlue(String deviceAddress,int position){
        //判断是否在搜索，是的话就取消搜索
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
        pairDevice(device);
        Log.i("wp123","BluetoothCollector中 mHandler = "+mHandler);
        thread = new ConnectThread(device, mBluetoothAdapter, mHandler, position);
        thread.start();
    }

    // 开始搜索蓝牙设备
    public void doDiscovery() {

        //判断是否在搜索，是的话就取消搜索
        cancelDiscovery();
        /*if (mBluetoothAdapter.isDiscovering()){
            ToastUtils.showShort(MyApplication.getContext(),"正在搜索蓝牙中");
        }else{
            mBluetoothAdapter.startDiscovery();
        }*/
        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
    }

    //取消蓝牙搜索
    public void cancelDiscovery(){
        //判断是否在搜索，是的话就取消搜索
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }


    //取消配对
    public void unPairAllDevices() {

        for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
            String name = device.getName();
            if (name != null) {
                // if (name.contains("PuppyGo")) {
                try {
                    Method removeBond = device.getClass().getDeclaredMethod("removeBond");
                    removeBond.invoke(device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // }
            }
        }
    }
    //设备的配对
    public void pairDevice(BluetoothDevice device ){

        try {
            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");

            createBondMethod.invoke(device);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
