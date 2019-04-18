package utils;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by user on 2018/12/10.
 */

public class BlueUtils {

    private Activity activity;

    public BlueUtils(Activity activity){
            this.activity=activity;
    };

    public static void sendMessage(String s, BluetoothSocket mSocket) {
        // String s = ed_message.getText().toString()+"";
        if (mSocket == null) {
            Log.i("ReadThred", "socket == null");
        } else {
            try {
                Log.i("ReadThred", "socket发送之前");
                OutputStream os = mSocket.getOutputStream();
                os.write(s.getBytes());
                Log.i("ReadThred", "socket发送成功");

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


}
