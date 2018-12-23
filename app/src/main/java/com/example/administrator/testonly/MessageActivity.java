package com.example.administrator.testonly;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.administrator.testonly.Constant.strurl;

public class MessageActivity extends AppCompatActivity {

    private List<Inform> rList = new ArrayList<Inform>();

    private Cursor cursor;

    private List<Inform> informList = new ArrayList<>();


    private InformAdapter adapter;

    private SwipeRefreshLayout swipeRefresh;

    //网络接受数据
    private String[] rawstr;
    private String[] ripestr;
    private List<String> rinfrom;
    private int rinformlength;


    //数据库操作
    private InformHelper ih;
    private SQLiteDatabase informdb;

    private String rinformtitle,rinformcontent;

    private RecyclerView recyclerView;

    private String authority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle("消息");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //下拉刷新
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh2);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshInform();
            }
        });

//        getRequestWithOkhttp();

        //recyclerview初始化
        getRequestWithOkhttp();
        recyclerView = (RecyclerView)findViewById(R.id.rv_message);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        adapter = new InformAdapter(informList);
        recyclerView.setAdapter(adapter);

        authority = Constant.mUsername;
//        Toast.makeText(this,username,Toast.LENGTH_SHORT).show();
//        Toast.makeText(this,Integer.toString(informs.length),Toast.LENGTH_LONG).show();
    }

    private void getRequestWithOkhttp() {
        final String sendMessage = "getInform";
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
        rList.clear();
        rawstr = responseData.split("#");
        rinformlength = rawstr.length-1;
        try {
            for (int i=1;i<=rinformlength;i++){
                ripestr = rawstr[i].split("@");
                rinformtitle = ripestr[0];
                rinformcontent = ripestr[1];
                rList.add(new Inform(rinformtitle,rinformcontent));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        LoadingData loadingData = new LoadingData();
        loadingData.start();
    }

    private void readDatabase() {
        //数据库取数据
        ih = new InformHelper(MessageActivity.this,"Inform.db",null,1);
        informdb = ih.getWritableDatabase();
        cursor = informdb.query("inform_table",null,null,null,null,null,null);
        try {
            if(cursor.moveToFirst()) {
                do {
                    rinformtitle = cursor.getString(cursor.getColumnIndex("informtitle"));
                    rinformcontent = cursor.getString(cursor.getColumnIndex("informcontent"));
                    rList.add(new Inform(rinformtitle,rinformcontent));
//                    rcontentList.add(rinformcontent);
//                    informs = new Inform[]{new Inform(rinformtitle, rinformcontent)};
//                    int i = informs.length;
//                    informList.add(informs[i]);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void initInforms() {
//        readDatabase();
        informList.clear();


        try {
            //数据库用index
//            int index = cursor.getCount();
            //测试用
            for (int i = 0;i<rinformlength;i++){
                informList.add(rList.get(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x666:
                    initInforms();
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    private class LoadingData extends Thread{
        @Override
        public void run() {
            Message msg = new Message();
            MessageActivity.this.myHandler.sendEmptyMessage(0x666);
        }
    }

    private void refreshInform() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //改这条
                        getRequestWithOkhttp();
                        initInforms();
//                        recyclerView.invalidate();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar2,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search2:
                startActivity(new Intent(MessageActivity.this,SearchActivity.class));
                break;
            case R.id.delete2:
                if (authority.equals("B307")){
                    sendDeleteRequestWithOkhttp();
                }else {
                    Toast.makeText(MessageActivity.this,"您不具备此权限",Toast.LENGTH_LONG).show();
                }

                break;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendDeleteRequestWithOkhttp() {
        final String sendMessage1 = "deleteAllInform";
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

    private void showResponse(final String responseData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MessageActivity.this,responseData,Toast.LENGTH_SHORT);
            }
        });
    }

}
