package fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zkzn.dz_1.R;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapter.Adapter;
import controller.MyApplication;
import model.ItemBean;

/**
 * Created by user on 2019/3/26.
 */

public class OpenFileFragment extends Fragment implements Adapter.OnShowItemClickListener{
    private TextView pathText;
    private ListView fileslist;
    private View mView;
    private ImageView backView;
    private List<ItemBean> dataList;
    private List<ItemBean> selectList;
    private static boolean isShow=false; // 是否显示CheckBox标识

    private LinearLayout lay;

    String m_strpath = null;
    //记录当前父文件夹；
    File curdir;
    //记录当前路径下所有文件夹的文件数组；
    File[] currentFiles;
    FileFilter filter;

    private static String m_lastPath = "";
    private String m_openPath = null;

    ArrayList<String> listDs = new ArrayList<>();
    private RelativeLayout layout2;
    private RelativeLayout layout;
    private Adapter adapter;
    private File dirfile;
    private File[] dirfiles;
    private int count1=1;
    private int count2=1;
    private int count3=1;
    private String name=null;
   /* PictureDatabase pictureDB;
    SQLiteDatabase db;*/

    String xlsName = "DGZ-1S数据表格.xls";
    private int type =0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.openfile_fragment, container, false);
        fileslist = (ListView) mView.findViewById(R.id.files);
        pathText = (TextView) mView.findViewById(R.id.tvpath);
        layout = (RelativeLayout)mView.findViewById(R.id.relative);
        lay= (LinearLayout) mView.findViewById(R.id.lay);
        dataList = new ArrayList<ItemBean>();
        selectList = new ArrayList<ItemBean>();

        backView = (ImageView)mView.findViewById(R.id.back);
        //添加权限
        setPermissionRW();


        return mView;
    }

    //开启读写权限
    private void setPermissionRW() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)   //可读
                            != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)  //可写
                                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }else{
                initView();
            }
        }else{
            initView();
        }
    }

    public void initView() {
        //Intent data = getIntent();
        // m_strpath = data.getStringExtra("path");
        // 获取系统的SDCard目录；
        if (m_strpath == null) {
            if (!m_lastPath.equals("")) {
                m_strpath = m_lastPath;
            } else {
                m_strpath = Environment.getExternalStorageDirectory().getAbsolutePath();
                Log.i("===", "m_strpath" + m_strpath);
            }

        }


        filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                boolean boo = file.isFile();
                String name = file.getName();
                String pathName = file.getAbsolutePath();
                if (!boo == true && pathName.contains("DGZ-1S")){
                    return true;
                }else if(boo == true && name.contains("dgz") ){
                    return true;
                }
                return false;
            }
        };
        File root = new File(m_strpath);
        //如果SD卡存在；
        if (root.exists()) {

            curdir = root;
            currentFiles = root.listFiles(filter);

            // 使用当前目录下的全部文件、文件夹来填充ListView
            inflateListView(currentFiles);
        }
        fileslist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                if (isShow) {
                    return false;
                } else {
                    isShow = true;
                    for (ItemBean bean : dataList) {
                        bean.setShow(true);
                    }
                    adapter.notifyDataSetChanged();
                    showOpervate();
                    fileslist.setLongClickable(false);
                }
                return true;
            }
        });
        fileslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (isShow) {
                    ItemBean bean = dataList.get(position);
                    boolean isChecked = bean.isChecked();
                    if (isChecked) {
                        bean.setChecked(false);
                    } else {
                        bean.setChecked(true);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    //如果点击的是第一行
                    if (position == 0) {
                        try {
                            if (!curdir.getCanonicalPath().equals(m_strpath)) {

                                // 获取上一级目录
                                curdir = curdir.getParentFile();
                                // 列出当前目录下的所有文件
                                currentFiles = curdir.listFiles(filter);
                                // 再次更新ListView
                                inflateListView(currentFiles);

                            } else {
                                //finish();
                            }

                            if (curdir.getCanonicalPath().equals("/")) {
                                //finish();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    if (position > 1 || position == 1) {
                          // 如果用户单击了文件
                        if (currentFiles[position - 1].isFile()) {
                            //取得文件名
                            String fileName = currentFiles[position - 1].getName();
                            String strExt = null;
                            if (fileName.length() > 3) {
                                strExt = fileName.substring(fileName.length() - 3);
                            } else {
                                strExt = fileName + " 1 ";
                            }
                            if (strExt.equals("dgz")) {
                                //传递需要打开的文件名给MainActivity;
                                // Intent intent = new Intent(OpenAllActivity.this, OpenPictureActivity.class);
                               /* Intent intent = new Intent(OpenAllActivity.this, DataLookActivity.class);
                                String mPath = curdir.getAbsolutePath();
                                m_lastPath = mPath;

                                //mPath += "/" + fileName;
                                //只传递文件的路径
                                intent.putExtra("path", mPath);
                                intent.putExtra("filename", fileName);
                                intent.putExtra("position", position - 1);
                                //startActivity(intent);
                                startActivityForResult(intent, 0x01);
*/
                                //如果是xls文件，就调用系统分享到微信QQ等
                            } else {
                                Toast.makeText(getActivity(), "不是钢丝绳张紧力测试文件！", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } else {

                            // 获取用户点击的文件夹 下的所有文件
                            File[] tem = currentFiles[position - 1].listFiles(filter);
                            //获取用户单击的列表项对应的文件夹，设为当前的文件夹
                            curdir = currentFiles[position - 1];
                            //保存当前的父文件夹内的全部文件和文件夹
                            currentFiles = tem;
                            // 再次更新ListView
                            inflateListView(currentFiles);
                        }
                    }
                }
            }

        });
    }
        /**
         * 根据文件夹填充ListView
         *
         * @param files
         */
        public void inflateListView(File[] files) {
            if (files == null) {
                return;
            }
            dataList.clear();
            dataList.add(new ItemBean("..",R.mipmap.folder,false,false));
            for (int i = 0; i < files.length; i++) {

                if (currentFiles[i].isDirectory()) {
                    //如果是文件夹就显示的图片为文件夹的图片
                    dataList.add(new ItemBean( currentFiles[i].getName(),R.mipmap.folder,false,false));
                } else if(currentFiles[i].isFile() ){
                    dataList.add(new ItemBean(currentFiles[i].getName(),R.mipmap.file,false,false));
                }

            }
            adapter = new Adapter(dataList, getContext());
            fileslist.setAdapter(adapter);
            adapter.setOnShowItemClickListener(this);
            //填充数据集
             fileslist.setAdapter(adapter);
       /* if (curdir.getName().equals("DGZ-1S")){

            createExcel.setVisibility(View.VISIBLE);
        }else{
            createExcel.setVisibility(View.GONE);
        }*/
            try {
                pathText.setText(curdir.getCanonicalPath());
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    /**
     * 显示操作界面
     */
    private void showOpervate() {
        layout.setVisibility(View.VISIBLE);
        layout2.setVisibility(View.GONE);
        lay.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.operate_in);
        lay.setAnimation(anim);
        // 返回、删除、全选和反选按钮初始化及点击监听
        Button tvBack =(Button) mView.findViewById(R.id.operate_back);
        Button tvDelete = (Button) mView.findViewById(R.id.operate_delete);
        Button tvSelect = (Button) mView.findViewById(R.id.operate_select);
        Button tvInvertSelect = (Button) mView.findViewById(R.id.invert_select);

        tvBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isShow) {
                    selectList.clear();
                    for (ItemBean bean : dataList) {
                        bean.setChecked(false);
                        bean.setShow(false);
                    }
                    adapter.notifyDataSetChanged();
                    isShow = false;
                    fileslist.setLongClickable(true);
                    dismissOperate();

                }
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (ItemBean bean : dataList) {
                    if (!bean.isChecked()) {
                        bean.setChecked(true);
                        if (!selectList.contains(bean)) {
                            selectList.add(bean);
                        }
                    }
                }
                Log.i("cyy123", "selectList.size()1 = " + selectList.size());
                adapter.notifyDataSetChanged();
            }
        });
        tvInvertSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ItemBean bean : dataList){
                    if (!bean.isChecked()){
                        bean.setChecked(true);
                        if (!selectList.contains(bean)) {
                            selectList.add(bean);
                        }
                    }else {
                        bean.setChecked(false);
                        if (selectList.contains(bean)) {
                            selectList.remove(bean);
                        }
                    }
                }
                Log.i("cyy123", "selectList.size()2 = " + selectList.size());
                adapter.notifyDataSetChanged();
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectList != null && selectList.size() > 0) {
                    for(int i=0;i<selectList.size();i++) {
                        for (int j = 0; j < dataList.size(); j++) {
                            if (selectList.get(i).getMsg().equals(dataList.get(j).getMsg())) {
                                m_openPath = pathText.getText().toString();
                                dirfile = new File(m_openPath + "/" + selectList.get(i).getMsg());
                                String filename = dirfile.getName();
                                if (filename != null) {
                                    String str = null;
                                    if (filename.length() > 3) {
                                        str = filename.substring(filename.length() - 3);
                                    } else {
                                        str = filename + 1;
                                    }

                                    if (dirfile == null || !dirfile.exists()) {
                                        return;
                                    } else if (dirfile.isDirectory() || str.equals("..")) {
                                        if (count1 == 1) {
                                            Toast.makeText(getActivity(), "不能删除文件夹", Toast.LENGTH_SHORT).show();
                                            count1++;
                                        }

                                    } else if (str.equals("dgz") || filename.equals(xlsName) || filename.equals("data.png")|| filename.equals("测试报告.pdf")) {

                                        dirfile.delete();
                                        dataList.remove(j);
                                        if (str.equals("dgz")) {
                                            //pictureDB.delete(db, MyApplication.DGZFORCE, filename);
                                        }

                                    } else {
                                        if (count2 == 1) {
                                            Toast.makeText(getActivity(), "不能删除DGZ-1S以外的文件", Toast.LENGTH_SHORT).show();
                                            count2++;
                                        }
                                    }

                                }
                            }
                        }
                    }
                    dataList.removeAll(selectList);
                    if (isShow) {
                        selectList.clear();
                        for (ItemBean bean : dataList) {
                            bean.setChecked(false);
                            bean.setShow(false);
                        }
                        adapter.notifyDataSetChanged();
                        isShow = false;
                        fileslist.setLongClickable(true);
                        dismissOperate();
                    }
                    currentFiles = curdir.listFiles(filter);

                    // 使用当前目录下的全部文件、文件夹来填充ListView
                    inflateListView(currentFiles);

                    selectList.clear();
                    fileslist.setAdapter(adapter);
                } else {
                    Toast.makeText(getActivity(), "请选择条目", Toast.LENGTH_SHORT).show();
                }
                count1 = 1;
                count2 = 1;
            }
        });
    }

    /**
     * 隐藏操作界面
     */
    private void dismissOperate() {
        layout2.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.operate_out);
        lay.setVisibility(View.GONE);
        lay.setAnimation(anim);
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                //监听返回键按钮
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK){

                        if (isShow) {
                            selectList.clear();
                            for (ItemBean bean : dataList) {
                                bean.setChecked(false);
                                bean.setShow(false);
                            }
                            adapter.notifyDataSetChanged();
                            isShow = false;
                            fileslist.setLongClickable(true);
                            dismissOperate();
                        } else {
                            try {
                                Log.i("mtag", "curdir.getCanonicalPath() = " + curdir.getCanonicalPath());
                                if (!curdir.getCanonicalPath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {

                                    // 获取上一级目录
                                    curdir = curdir.getParentFile();
                                    Log.i("mtag", "curdir = " + curdir);
                                    // 列出当前目录下的所有文件
                                    currentFiles = curdir.listFiles(filter);
                                    // 再次更新ListView
                                    inflateListView(currentFiles);

                                } else {
                                    // finish();
                                }

                                if (curdir.getCanonicalPath().equals("/")) {
                                    //  finish();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                    }
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onShowItemClick(ItemBean bean) {
        if (bean.isChecked() && !selectList.contains(bean)) {
            selectList.add(bean);
        } else if (!bean.isChecked() && selectList.contains(bean)) {
            selectList.remove(bean);
        }
    }

}
