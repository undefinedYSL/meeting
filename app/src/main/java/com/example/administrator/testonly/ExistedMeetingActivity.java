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

public class ExistedMeetingActivity extends AppCompatActivity {

    public static final String MEETING_NAME = "meeting_name";
    private String meetingName;

    //数据库操作
    private MeetingHelper mh;
    private SQLiteDatabase meetingdb;
    private String rmeetingtitle,rmeetingdate,rmeetingtime,rmeetingduration,rmeetingsite,rmeetingattendant,rmeetingdescription;
    private Cursor cursor;
    private EditText etrmtitle,etrmdate,etrmtime,etrmduration,etrmsite,etrmattendant,etrmdescription;
    private String sendMessage;

    //网络操作
    private String[] str;
    private String authority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existed_meeting);

        etrmattendant = (EditText)findViewById(R.id.et_rmattendant);
        etrmdate = (EditText)findViewById(R.id.et_rmdate);
        etrmtime = (EditText)findViewById(R.id.et_rmtime);
        etrmduration = (EditText)findViewById(R.id.et_rmduration);
        etrmsite = (EditText)findViewById(R.id.et_rmsite);
        etrmtitle = (EditText)findViewById(R.id.et_rmtitle);
        etrmdescription = (EditText)findViewById(R.id.et_rmdescription);

        Intent intent = getIntent();
        meetingName = intent.getStringExtra(MEETING_NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar8);
        toolbar.setTitle(meetingName);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        try {
            mh = new MeetingHelper(ExistedMeetingActivity.this,"Meeting.db",null,1);
            meetingdb = mh.getWritableDatabase();
            String sql = "select * from meeting_table where meetingtitle = ?";
            cursor = meetingdb.rawQuery(sql,new String[]{meetingName});
            if (cursor.moveToFirst()){
                do {
                    rmeetingtitle = cursor.getString(1);
                    rmeetingdate = cursor.getString(2);
                    rmeetingtime = cursor.getString(3);
                    rmeetingduration = cursor.getString(4);
                    rmeetingsite = cursor.getString(5);
                    rmeetingattendant = cursor.getString(6);
                    rmeetingdescription = cursor.getString(7);
                }while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        authority = Constant.mUsername;
        sendMessage = "getMeeting@"+meetingName;
        sendRequestWithOkhttp();

    }

    private void sendRequestWithOkhttp() {
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

                etrmtitle.setText(str[0]);
                etrmdate.setText(str[1]);
                etrmtime.setText(str[2]);
                etrmduration.setText(str[3]);
                etrmsite.setText(str[4]);
                etrmattendant.setText(str[5]);
                etrmdescription.setText(str[6]);
            }
        });
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
                    Toast.makeText(ExistedMeetingActivity.this,"您不具备此权限",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.delete3:
                if (authority.equals("B307")){
                    sendDeleteRequestWithOkhttp();
                    finish();
                }else {
                    Toast.makeText(ExistedMeetingActivity.this,"您不具备此权限",Toast.LENGTH_SHORT).show();
                }
                break;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendDeleteRequestWithOkhttp() {
        final String sendMessage1 = "deleteMeeting"+"@"+meetingName;
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
        final String sendMessage2 = "editMeeting"+"@"+meetingName;
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

    private void showResponse(final String responseData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ExistedMeetingActivity.this,responseData,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
