package controller;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;

import model.BlueInfo;
import service.MyService;
import thread.ConnectThread;

/*import java.util.ArrayList;

import model.Point;
import model.User;
import utils.MyService;*/


/**
 * Created by Administrator on 18-5-22.
 */
public class MyApplication extends Application {

    private static Context context;
    public static String DGZFORCE="DGZForceDB";

    public static String alldataOne;
    public static String alldataTwo;
    public static String alldataThree;
    public static ArrayList<ConnectThread> allThreads = new ArrayList<>();
    public static ArrayList<BlueInfo> allBlueInfo = new ArrayList<>();
    public
    SQLiteDatabase db;

    public static String getStringOne() {
        return MyApplication.alldataOne;
    }

    public static void setStringOne(String alldata) {
        MyApplication.alldataOne = alldata;
        Log.i("MyApplication", "alldata = " + alldata);
    }

    public static String getStringTwo() {
        return MyApplication.alldataTwo;
    }

    public static void setStringTwo(String alldata) {
        MyApplication.alldataTwo = alldata;
        Log.i("MyApplication", "alldata = " + alldata);
    }

    public static String getStringThree() {
        return MyApplication.alldataThree;
    }

    public static void setStringThree(String alldata) {
        MyApplication.alldataThree = alldata;
        Log.i("MyApplication", "alldata = " + alldata);
    }

    public static ArrayList<ConnectThread> getThreadsList() {
        return MyApplication.allThreads;
    }

    public static void addThreadsList(ConnectThread thread) {
        MyApplication.allThreads.add(thread);
        Log.i("wp123", "thread = " + thread);
    }

    public static void removeThreadsList(ConnectThread thread) {
        if (allThreads.size() > 0){
            for (int i = 0; i < allThreads.size(); i++){
                if (thread.equals(allThreads.get(i))){
                    allThreads.remove(i);
                    break;
                }
            }
        }
    }

    public static ArrayList<BlueInfo> getBlueInfoList() {
        return allBlueInfo;
    }

    public static void addBlueInfoList(BlueInfo blueInfo) {
        allBlueInfo.add(blueInfo);
        Log.i("wp123", "blueInfo = " + blueInfo);
    }
    public static void removeBlueInfoList(BlueInfo blueInfo) {
        if (allBlueInfo.size() > 0){
            for (int i = 0; i < allBlueInfo.size(); i++){
                if (blueInfo.getAddress().equals(allBlueInfo.get(i).getAddress())){
                    allBlueInfo.remove(i);
                    break;
                }
            }
        }
        //MyApplication.allBlueInfo.remove(blueInfo);
        Log.i("wp123", "blueInfo = " + blueInfo);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();

        startService(new Intent(context, MyService.class));
        Log.i("wp123", "开启Service服务");

    }
    public static Context getContext(){
        return context;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
       stopService(new Intent(context, MyService.class));
        allThreads.clear();
    }
    /** 获取屏幕宽度 */
    public static int getWindowWidth() {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int width=wm.getDefaultDisplay().getWidth();
        Log.i("ooo", "width = " + width);
            return width;
    }
    /** 获取屏幕高度 */
    @SuppressWarnings("deprecation")
    public static int getWindowHeight() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height=wm.getDefaultDisplay().getHeight();
        Log.i("ooo", "height = " + height);
        return height;
    }



    /**
     * 判断是否为平板
     *
     * @return
     */
    public boolean isPad() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        // 屏幕宽度
        float screenWidth = display.getWidth();
        // 屏幕高度
        float screenHeight = display.getHeight();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        // 屏幕尺寸
        double screenInches = Math.sqrt(x + y);
        // 大于6尺寸则为Pad
        return screenInches >= 6.0;
    }
}
