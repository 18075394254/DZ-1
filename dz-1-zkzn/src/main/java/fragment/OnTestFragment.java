package fragment;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zkzn.dz_1.R;

import java.util.ArrayList;

import controller.MyApplication;
import service.MyService;
import thread.ConnectThread;
import utils.BlueUtils;

/**
 * Created by user on 2019/3/26.
 */

public class OnTestFragment extends Fragment {
    private View mView;
   /* private Button sendbtn;
    private EditText sendedit;
    private TextView receivetext;*/
   private TextView curConnectCount;
    private Button startBtn,lookDataBtn;
    private ListView equipmentList;
    private ArrayList<ConnectThread> threadList = MyApplication.getThreadsList();
    private ArrayList<String> StringsList = new ArrayList<>();
    private ArrayList<String> list1 = new ArrayList<>();
    private ArrayList<String> list2 = new ArrayList<>();
    private ArrayList<String> list3 = new ArrayList<>();
    StringBuilder mStrings = new StringBuilder();
    ArrayAdapter<String> adapter;
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
            if (flag == 10000) {
                Bundle bundle = msg.getData();
                String deviceName = bundle.getString("name");
                String deviceAddress = bundle.getString("address");

                mStrings.append((String) msg.obj + "\n");
                //receivetext.setText(mStrings.toString());
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       mView = inflater.inflate(R.layout.test_fragment,container,false);

        startBtn = mView.findViewById(R.id.startTest);
        curConnectCount = mView.findViewById(R.id.curConnect);
        lookDataBtn = mView.findViewById(R.id.lookData);
        equipmentList = mView.findViewById(R.id.equipmentList);
        //监听系统蓝牙状态的广播
        IntentFilter filter1=new IntentFilter();
        filter1.addAction("android.intent.action.ontestfragment");
        getActivity().registerReceiver(connectReceiver, filter1);

        if (threadList != null){

            curConnectCount.setText("当前连接蓝牙数："+threadList.size()+" 个");
            for (int i = 0;i < threadList.size();i++ ){
                StringsList.add(threadList.get(i).getDevice().getName()+"");
            }

        }
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,StringsList);
        equipmentList.setAdapter(adapter);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  if (sendedit.getText().toString() != ""){
                threadList = MyApplication.getThreadsList();
                    Log.i("wp123","threadList = "+threadList.size());
                    if (threadList != null && threadList.size() > 0){

                            for (int i = 0;i < threadList.size();i++){
                                BlueUtils.sendMessage("A1",threadList.get(i).getsocket());
                            }
                        }else{
                            Toast.makeText(getActivity(),"没有蓝牙设备连接",Toast.LENGTH_SHORT).show();

                        }
                  //  }
                }

        });

        lookDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return mView;
    }


    //监听数据的信息
    private final BroadcastReceiver connectReceiver = new BroadcastReceiver() {
        public void onReceive (Context context, Intent intent) {
            String action = intent.getAction();

            //接收数据信息
            if (intent.getAction().equals("android.intent.action.ontestfragment")) {
                /*String deviceName = intent.getStringExtra("name");
                String deviceAddress = intent.getStringExtra("address");
                String message = intent.getStringExtra("message");*/
                Message msg = new Message();
                Bundle bundle = intent.getExtras();
                msg.setData(bundle);
                mHandler.obtainMessage(10000, 1, -1, msg).sendToTarget();

            }
        }

    };

}
