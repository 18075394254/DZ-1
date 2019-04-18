package thread;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import utils.DataTrans;

/**
 * Created by user on 2018/12/7.
 */

public class ReadThread extends Thread{

    private BluetoothSocket socket;
    private boolean flag = true;
    private BluetoothDevice mDevice;
    int arg =0;
    byte[] all=new byte[1024*1024];
    private int index=0;
    private StringBuilder sb=new StringBuilder();
    private Handler mHandler;
             public ReadThread(BluetoothDevice device, BluetoothSocket socket, Handler handler,int arg) {
                 this.socket = socket;
                 mHandler = handler;
                  mDevice = device;
                 this.arg = arg;
              }

             public void run() {

                  //byte[] buffer = new byte[1024];
                 int bytes;
                 byte[] buffer;

                 int availableBytes;
                 byte[] bt;
                InputStream mmInStream = null;
                try {
                          mmInStream = this.socket.getInputStream();
                     } catch (IOException e1) {
                             // TODO Auto-generated catch block
                          e1.printStackTrace();
                       }

                 while (flag) {
                     try {
                         /*try {
                             ReadThread.sleep(100);
                         } catch (InterruptedException e) {
                             e.printStackTrace();
                         }*/
                         //获得的有效字节数
                         availableBytes = mmInStream.available();
                         buffer = new byte[availableBytes];

                         bytes = mmInStream.read(buffer);
                         Log.i("wp628254", "bytes = " + bytes);
                         if(bytes>0) {
                             for (int i = 0; i < bytes; i++) {
                                 all[index]=buffer[i];
                                 index++;
                             }
                         }
                         if(index>15) {
                             byte[] last=new byte[6];
                             for(int i=0;i<6;i++){
                                 last[i]=all[index-6+i];
                             }
                             Log.i("cyy628254","? END*B2 ? ="+DataTrans.BytesToString(last));
                             if (DataTrans.BytesToString(last).equals("END*B1") || DataTrans.BytesToString(last).equals("END*B2")) {
                                 byte[] length=new byte[4];
                                 for(int i=0;i<4;i++){
                                     length[i]=all[i+1];
                                 }
                                 int datalength=DataTrans.byte2int(length);
                                 int datanumber=DataTrans.TwoBytesToInt(all[7],all[8]);
                                 byte[] bt0=new byte[1];
                                 bt0[0]=all[0];
                                 // sb.append(DataTrans.BytesToString(bt0)+" "+datalength+" ");
                                 Log.i("cyy628254", "datalength =" + datalength + " " + DataTrans.BytesToString(bt0));
                                 byte[] bt56=new byte[2];
                                 bt56[0]=all[5];
                                 bt56[1]=all[6];
                                 // sb.append(DataTrans.BytesToString(bt56) + " " + datanumber + " ");
                                 Log.i("cyy628254", "datanumber =" + DataTrans.BytesToString(bt56) + " " + datanumber + " ");
                                 for (int i=9;i<index-6;i+=2){
                                     if(i<index-6-2) {
                                         sb.append(DataTrans.TwoBytesToInt(all[i], all[i + 1]) + " , ");
                                     }else{
                                         sb.append(DataTrans.TwoBytesToInt(all[i], all[i + 1]));
                                     }
                                 }
                                 // sb.append(DataTrans.BytesToString(last));
                                 //mHandler.obtainMessage(BluetoothState.MESSAGE_READ, bytes, -1, sb.toString()).sendToTarget();
                                 Message msg = new Message();
                                 msg.what = 10000;
                                 Bundle bundle = new Bundle();
                                 bundle.putString("name", mDevice.getName());
                                 bundle.putString("address", mDevice.getAddress());
                                 bundle.putString("message",sb.toString());
                                 msg.setData(bundle);
                                 mHandler.sendMessage(msg);
                                 all=new byte[1024*1024];
                                 index=0;
                                 sb.delete(0,sb.length());

                             }else{
                                 // mHandler.obtainMessage(BluetoothState.MESSAGE_READ, bytes, -1, "数据接收错误").sendToTarget();
                             }
                         }
                         // Read from the InputStream
                        /* if ((bytes = mmInStream.read(buffer)) > 0) {
                             byte[] buf_data = new byte[bytes];
                             for (int i = 0; i < bytes; i++) {
                                 buf_data[i] = buffer[i];
                             }
                             String s = new String(buf_data);
                             Log.i("ReadThred", "s = "+ s);
                             Message msg = new Message();
                             msg.what = 10000;
                             Bundle bundle = new Bundle();
                             bundle.putString("name", mDevice.getName());
                             bundle.putString("address", mDevice.getAddress());
                             bundle.putString("message",s);
                             msg.setData(bundle);
                             mHandler.sendMessage(msg);
                         }*/
                     } catch (IOException e) {
                         try {
                             mmInStream.close();
                         } catch (IOException e1) {
                             // TODO Auto-generated catch block
                             e1.printStackTrace();
                         }
                         break;
                     }

                 }
     }


    public void close() {
        flag = false;
        socket = null;
    }
}
