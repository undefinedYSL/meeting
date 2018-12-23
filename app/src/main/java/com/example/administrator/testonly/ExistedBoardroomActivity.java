package com.example.administrator.testonly;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.administrator.testonly.Constant.strurl;

public class ExistedBoardroomActivity extends AppCompatActivity {

    public static final String BOARDROOM_NAME = "boardroom_name";
    public static final String BOARDROOM_POSITION = "position";
    private String boardroomName;
    private int position;
    private String str_position;


    //数据库操作
    private BoardroomHelper bh;
    private SQLiteDatabase boardroomdb;
    private String rboardroomname,rboardroomsite,rboardroomconfiguration,rboardroomcapacity,rboardroomtelephone,rboardroomremark;
    private Cursor cursor;

    //网络操作
    private EditText etrbname,etrbsite,etrbconfiguration,etrbcapacity,etrbtelephone,etrbremark;
    private String sendMessage;
    private String[] str;

    private String authority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existed_boardroom);

        etrbtelephone = (EditText)findViewById(R.id.et_rbtelephone);
        etrbsite = (EditText)findViewById(R.id.et_rbsite);
        etrbremark = (EditText)findViewById(R.id.et_rbremark);
        etrbconfiguration = (EditText)findViewById(R.id.et_rbconfiguration);
        etrbcapacity = (EditText)findViewById(R.id.et_rbcapacity);
        etrbname = (EditText)findViewById(R.id.et_rbname);

        Intent intent = getIntent();
        boardroomName = intent.getStringExtra(BOARDROOM_NAME);
        position = intent.getIntExtra(BOARDROOM_POSITION,0);
        str_position = Integer.toString(position);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar10);
        toolbar.setTitle(boardroomName);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        sendMessage = "getBoardroom@"+boardroomName;
        sendRequestWithOkHttp();

        //数据库操作
//        try{
//            bh = new BoardroomHelper(ExistedBoardroomActivity.this,"Boardroom.db",null,1);
//            boardroomdb = bh.getWritableDatabase();
//            String sql = "select * from boardroom_table where boardroomname = ?";
//            cursor = boardroomdb.rawQuery(sql,new String[]{boardroomName});
//            if (cursor.moveToFirst()){
//                do {
//                    rboardroomname = cursor.getString(1);
//                    rboardroomsite = cursor.getString(2);
//                    rboardroomconfiguration = cursor.getString(3);
//                    rboardroomcapacity = cursor.getString(4);
//                    rboardroomtelephone = cursor.getString(5);
//                    rboardroomremark = cursor.getString(6);
//                }while (cursor.moveToNext());
//            }
//            cursor.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        authority = Constant.mUsername;
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
                    Toast.makeText(ExistedBoardroomActivity.this,"您不具备此权限",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.delete3:
                if (authority.equals("B307")){
                    sendDeleteRequestWithOkhttp();
                    finish();
                }else {
                    Toast.makeText(ExistedBoardroomActivity.this,"您不具备此权限",Toast.LENGTH_SHORT).show();
                }

                //测试失败
//                Intent intent = new Intent(ExistedBoardroomActivity.this,BoardroomActivity.class);
//                intent.putExtra("position",str_position);
//                startActivity(intent);

                break;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendDeleteRequestWithOkhttp() {
        final String sendMessage1 = "deleteBoardroom"+"@"+boardroomName;
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
        final String sendMessage2 = "editBoardroom@"+boardroomName;
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

    //请求该项数据
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
                etrbname.setText(str[0]);
                etrbsite.setText(str[1]);
                etrbcapacity.setText(str[2]);
                etrbconfiguration.setText(str[3]);
                etrbtelephone.setText(str[4]);
                etrbremark.setText(str[5]);
            }
        });
    }

    private void showResponse(final String responseData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ExistedBoardroomActivity.this,responseData,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
