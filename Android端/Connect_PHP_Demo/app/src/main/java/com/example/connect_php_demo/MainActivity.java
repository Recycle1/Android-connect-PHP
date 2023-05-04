package com.example.connect_php_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tv_result;
    private Button btn_data_single,btn_data_multiple,btn_file;
    private String result;
    private ArrayList<MultipleResult> result_list;
    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what){
                case -1:
                    tv_result.setText("传输数据失败");
                    break;
                case 0:
                    tv_result.setText(result);
                    break;
                case 1:
                    for(int i=0;i<result_list.size();i++){
                        if(i==0){
                            tv_result.setText(result_list.get(i).a1+" "+result_list.get(i).a2+" "+result_list.get(i).a3+"\n");
                        }
                        else{
                            tv_result.append(result_list.get(i).a1+" "+result_list.get(i).a2+" "+result_list.get(i).a3+"\n");
                        }
                    }
                    break;
                case 2:
                    tv_result.setText("文件传输成功");
                    break;
            }
            return false;
        }
    });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_interface();
        init_permission();
        init_listener();
    }

    //初始化界面
    void init_interface(){
        tv_result=findViewById(R.id.tv_result);
        btn_data_single=findViewById(R.id.btn_data_single);
        btn_data_multiple=findViewById(R.id.btn_data_multiple);
        btn_file=findViewById(R.id.btn_file);
        result_list=new ArrayList<>();
    }

    //初始化权限
    void init_permission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    //初始化监听器
    void init_listener(){
        btn_data_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //在新线程中获取数据，通过handler判断是否获取到数据
                            //n为向PHP传输的数据
                            String n="本地测试数据";
                            result=WebTool.singleData("https://www.recycle11.top/Connect_Android/get_single_data.php?n="+n);
                            if(!result.equals("failed")){
                                handler.sendEmptyMessage(0);
                            }
                            else{
                                handler.sendEmptyMessage(-1);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        btn_data_multiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //n为测试数据
                            result_list=WebTool.multipleData("本地测试数据");
                            handler.sendEmptyMessage(1);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        btn_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        WebTool.uploadFile(test_file_path(),"https://www.recycle11.top/Connect_Android/upload.php","test.txt");
                        handler.sendEmptyMessage(2);

                    }
                }).start();
            }
        });
    }

    String test_file_path(){
        File fileDir=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Connect_web_test");
        String filename="test.txt";

        //创建文件夹及文件
        FileUtil.createFileDir(fileDir);
        FileUtil.createFile(fileDir.getAbsolutePath(),filename);

        //写入文件信息
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(fileDir.getAbsolutePath()+"/"+filename);
            String content="本地测试数据";
            fos.write(content.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                //保证即使出现异常，fos也可以正常关闭
                fos.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return fileDir.getAbsolutePath()+"/"+filename;
    }

}