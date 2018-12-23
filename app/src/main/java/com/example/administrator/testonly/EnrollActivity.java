package com.example.administrator.testonly;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.administrator.testonly.Constant.strurl;

public class EnrollActivity extends AppCompatActivity {

    private EditText etenrollusername,etenrollpassword;
    private String enrollusername,enrollpassword;
    private Button confirm;
    private String sendMessage;
    private String responseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);
        etenrollusername = (EditText)findViewById(R.id.et_newusername);
        etenrollpassword = (EditText)findViewById(R.id.et_newpassword);
        confirm = (Button)findViewById(R.id.btn_enrollconfirm);
    }

    public void click(View view) {

        try {
            enrollusername = etenrollusername.getText().toString().trim();
            enrollpassword = etenrollpassword.getText().toString().trim();
        }catch (Exception e){
            e.printStackTrace();
        }


        sendMessage = "enroll"+"@"+enrollusername+"@"+enrollpassword;

        if (TextUtils.isEmpty(enrollusername)||TextUtils.isEmpty(enrollpassword)) {
            Toast.makeText(EnrollActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
        }else {
            sendRequestWithOkHttp();
        }
        EnrollActivity.this.finish();
//        if (responseData.equals("enroll success")){
//            EnrollActivity.this.finish();
//        }
    }

    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //表单数据
//                    FormBody.Builder builder = new FormBody.Builder();
//                    builder.add("inform",sendMessage);
//                    RequestBody formBody = builder.build();
                    RequestBody formBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"),sendMessage);
                    //发送请求
                    Request request = new Request.Builder()
                            .url(strurl)
                            .post(formBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    showResponse(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showResponse(final String responseData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(EnrollActivity.this,responseData,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
