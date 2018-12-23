package com.example.administrator.testonly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Main2Activity extends AppCompatActivity {

    private EditText etUserName;
    private  EditText etPassword;
    private String UserName;
    private String Password;
    private SharedPreferences sp;
    private CheckBox checkBox;
    private ImageView nameClear;
    private ImageView passwordClear;

    //http通信,实验室沿用
    private String service_LoginMessage;

    public String getService_LoginMessage(){
        return service_LoginMessage;
    }

    public void setService_LoginMessage( String service_LoginMessage){
        this.service_LoginMessage = service_LoginMessage;
    }

    //OKhttp通信使用的参数
//    private String url = "http://192.168.43.232:8888/";
    private String url = "http://39.104.87.35:8888/myApps";
    private Map<String,String> map = new HashMap<>();
    public static String TAG = "Main2Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        sp = this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);

        etUserName = (EditText)findViewById(R.id.et_username);
        etPassword = (EditText)findViewById(R.id.et_password);
        nameClear = (ImageView)findViewById( R.id.iv_usernameclear);
        passwordClear = (ImageView)findViewById(R.id.iv_passwordclear);
//        btnLogin = (Button)findViewById(R.id.btn_login);
        checkBox = (CheckBox)findViewById(R.id.cb_checkbox);

        EditTextClearTools.addClearListener(etUserName,nameClear);
        EditTextClearTools.addClearListener(etPassword,passwordClear);

        if (sp.getBoolean("ISCHECKED",true)){
            checkBox.setChecked(true);
            etUserName.setText(sp.getString("USERNAME",""));
            etPassword.setText(sp.getString("PASSWORD",""));

        }
        checkBox.setOnCheckedChangeListener(new MyListerner1());
    }

    public void click(View view) {
        SharedPreferences.Editor editor = sp.edit();

        try {
            UserName = etUserName.getText().toString().trim();
            Password = etPassword.getText().toString().trim();
        }catch (Exception e){
            e.printStackTrace();
        }
        if (checkBox.isChecked()){
            editor.putString("USERNAME",UserName);
            editor.putString("PASSWORD",Password);
            editor.commit();
        }
        final String sendMessage = "login"+"@"+UserName+"@"+Password;
        if (TextUtils.isEmpty(UserName)||TextUtils.isEmpty(Password)) {
            Toast.makeText(Main2Activity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
        }

        //Okhttp测试
//        else{
//            map.put("username",UserName);
//            map.put("password",Password);
//            OkHttpUtil.post(url, new okhttp3.Callback(){
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    Log.e(TAG,"onFailure",e);
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    String responseBody = response.body().string();
//                    if (true){
//                        try {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Intent intent = new Intent(Main2Activity.this,Main3Activity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            });
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//
//                }
//            },map);
//        }

        //Okhttp测试1
//        else{
//            try {
//                OkHttpClient client = new OkHttpClient();
//                //表单数据
//                FormBody.Builder builder = new FormBody.Builder();
//                builder.add("hehe","test");
//                RequestBody formBody = builder.build();
//                //发送请求
//                Request request = new Request.Builder()
//                        .url("http://192.168.43.16:8888/myApps")
//                        .post(formBody)
//                        .build();
//                Response response = client.newCall(request).execute();
//                String responseData = response.body().string();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }

        //实验室沿用框架
        else{
            new Thread(){
                public void run(){
                    final String result = send_ToService.send_Message(sendMessage);
                    if (result!=null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setService_LoginMessage(result);
                                checklogin();
                                Main2Activity.this.finish();
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Main2Activity.this,"连接超时",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.putExtra("str","NO");
                                intent.setClass(Main2Activity.this,Main3Activity.class);
                                startActivity(intent);
                                Main2Activity.this.finish();

                            }
                        });
                    }
                }
            }.start();
        }

        //离线登录
//            else if (UserName.equals("admin")&&Password.equals("admin")){
//            Toast.makeText(Main2Activity.this,"登陆成功",Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent();
//            intent.setClass(Main2Activity.this,Main3Activity.class);
//            startActivity(intent);
//            }

    }

    private void checklogin() {
        String str = getService_LoginMessage();
        String[] strs = str.split("@");
        String enter = strs[1];
        if ("OK".equals(enter)){
            Toast.makeText(Main2Activity.this,"登陆成功",Toast.LENGTH_SHORT).show();
            //在多个activity传值
            Constant.mUsername = UserName;
            Intent intent = new Intent();
            intent.putExtra("str","OK");
            intent.setClass(Main2Activity.this,Main3Activity.class);
            startActivity(intent);
        }else {
            Toast.makeText(Main2Activity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
        }
    }

    public void click1(View view) {
        Intent intent1 = new Intent();
        intent1.setClass(Main2Activity.this,EnrollActivity.class);
        startActivity(intent1);
    }

    private class MyListerner1 implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (checkBox.isChecked()){
                sp.edit().putBoolean("ISCHECKED",true).commit();
            }else {
                sp.edit().putBoolean("ISCHECKED",false).commit();
            }
        }
    }

}
