package com.example.administrator.testonly;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.administrator.testonly.Constant.strurl;

public class ExistedInformActivity extends AppCompatActivity {

    public static final String INFORM_NAME = "inform_name";
    private String informName;


    //数据库操作
    private InformHelper ih;
    private SQLiteDatabase informdb;
    private String rinformtitle,rinformcontent;
    private Cursor cursor;

    private EditText etrititle,etricontent;
    private String sendMessage;
    private String[] str;

    private String authority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existed_inform);

        etrititle = (EditText)findViewById(R.id.et_rititle);
        etricontent = (EditText)findViewById(R.id.et_ricontent);


        Intent intent = getIntent();
        informName = intent.getStringExtra(INFORM_NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar9);
        toolbar.setTitle(informName);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

//        //单机数据库
//        try {
//            ih = new InformHelper(ExistedInformActivity.this,"Inform.db",null,1);
//            informdb = ih.getWritableDatabase();
//            String sql ="select * from inform_table where informtitle = ?";
//            cursor = informdb.rawQuery(sql,new String[]{informName});
//            if (cursor.moveToFirst()){
//                do {
//                    rinformtitle = cursor.getString(1);
//                    rinformcontent = cursor.getString(2);
//                }while (cursor.moveToNext());
//            }
//            cursor.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        authority = Constant.mUsername;
        sendMessage = "getInform@"+informName;
        sendRequestWithOkHttp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar3,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit3:
                if (authority.equals("B307")){
                    sendEditRequestWithOkhttp();
                    finish();
                }else {
                    Toast.makeText(ExistedInformActivity.this,"您不具备此权限",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.delete3:
                if (authority.equals("B307")){
                    sendDeleteRequestWithOkhttp();
                    finish();
                }else {
                    Toast.makeText(ExistedInformActivity.this,"您不具备此权限",Toast.LENGTH_SHORT).show();
                }
                break;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendDeleteRequestWithOkhttp() {
        final String sendMessage1 = "deleteInform"+"@"+informName;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //表单数据
//                    FormBody.Builder builder = new FormBody.Builder();
//                    builder.add("inform",sendMessage);
//                    RequestBody formBody = builder.build();
                    RequestBody formBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"),sendMessage1);
                    //发送请求
                    Request request = new Request.Builder()
                            .url(strurl)
                            .post(formBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    showResponse(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }



    private void sendEditRequestWithOkhttp() {
        final String sendMessage2 = "editInform"+"@"+informName;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //表单数据
//                    FormBody.Builder builder = new FormBody.Builder();
//                    builder.add("inform",sendMessage);
//                    RequestBody formBody = builder.build();
                    RequestBody formBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"),sendMessage2);
                    //发送请求
                    Request request = new Request.Builder()
                            .url(strurl)
                            .post(formBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    showResponse(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
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
                    String responseData = response.body().string();
                    handleResponse(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleResponse(final String responseData) {
        str = responseData.split("@");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                etricontent.setText(str[1]);
                etrititle.setText(str[0]);
            }
        });
    }

    private void showResponse(final String responseData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ExistedInformActivity.this,responseData,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
