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

public class BoardroomActivity extends AppCompatActivity {
    //数据库操作
    private BoardroomHelper bh;
    private SQLiteDatabase boardroomdb;
    private List<Boardroom> rList = new ArrayList<Boardroom>();
    private Cursor cursor;
    private String rboardroomname,rboardroomsite,rboardroomtelephone;
    private String rboardroomname1,rboardroomsite1,rboardroomtelephone1;


//    private Boardroom[] boardrooms = {new Boardroom("b307","研修室","博远楼三楼"),
//    new Boardroom("c306","办公室","育贤楼三楼")};
    private Boardroom[] boardrooms ;

    private List<Boardroom> boardroomList = new ArrayList<>();

    private BoardroomAdapter adapter;

    private SwipeRefreshLayout swipeRefresh;

    private RecyclerView recyclerView;

    //网络操作
    private String[] rawstr;
    private String[] ripestr;
    private List<String> rboardroom;
    private int rboardroomlength;

    private String authority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardroom);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        toolbar.setTitle("会议室");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //下拉刷新
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh1);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshBoardroom();
            }
        });

        //recyclerview初始化
        getRequestWithOkhttp();
        recyclerView = (RecyclerView)findViewById(R.id.rv_boardroom);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        adapter = new BoardroomAdapter(boardroomList);
        recyclerView.setAdapter(adapter);

//        readDatabase();
        authority = Constant.mUsername;

    }

    private void readDatabase() {
        //数据库取数据
        bh = new BoardroomHelper(BoardroomActivity.this,"Boardroom.db",null,1);
        boardroomdb = bh.getWritableDatabase();
        cursor = boardroomdb.query("boardroom_table",null,null,null,null,null,null);
        try {
            if(cursor.moveToFirst()) {
                do {
                    rboardroomname = cursor.getString(cursor.getColumnIndex("boardroomname"));
                    rboardroomtelephone = cursor.getString(cursor.getColumnIndex("boardroomtelephone"));
                    rboardroomsite = cursor.getString(cursor.getColumnIndex("boardroomsite"));
                    rList.add(new Boardroom(rboardroomname,rboardroomtelephone,rboardroomsite));
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

    private void initBoardroom() {
//        readDatabase();
        boardroomList.clear();

        try {
//            int index = cursor.getCount();
            //测试用
            for (int i = 0;i<rboardroomlength;i++){
                boardroomList.add(rList.get(i));
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
                    initBoardroom();
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
            BoardroomActivity.this.myHandler.sendEmptyMessage(0x666);
        }
    }

    public void isNeedRefresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    readDatabase();
                    int row1 = cursor.getCount();
                    int row2 = boardroomList.size();
                    if (row1 > row2){
                        if(cursor.moveToLast()) {
                            rboardroomname1 = cursor.getString(cursor.getColumnIndex("boardroomname"));
                            rboardroomtelephone1 = cursor.getString(cursor.getColumnIndex("boardroomtelephone"));
                            rboardroomsite1 = cursor.getString(cursor.getColumnIndex("boardroomsite"));
                            boardrooms = new Boardroom[]{new Boardroom(rboardroomname1, rboardroomsite1, rboardroomsite1)};
                        }
                    }else {
                        return;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
        int i = boardrooms.length;
        boardroomList.add(boardrooms[i]);
    }

    private void refreshBoardroom() {
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
                        //改这里
                        getRequestWithOkhttp();
                        initBoardroom();
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
        inflater.inflate(R.menu.toolbar1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search1:
                startActivity(new Intent(BoardroomActivity.this,SearchActivity.class));
                break;
            case R.id.add1:
                if (authority.equals("B307")){
                    Intent intent = new Intent();
                    intent.setClass(BoardroomActivity.this,AddBoardroomActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(BoardroomActivity.this,"您不具备此权限",Toast.LENGTH_SHORT).show();
                }
                break;
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }

    private void getRequestWithOkhttp() {
        final String sendMessage = "getBoardroom";
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
        rboardroomlength = rawstr.length-1;
        try {
            for (int i=1;i<=rboardroomlength;i++){
                ripestr = rawstr[i].split("@");
                rboardroomname = ripestr[0];
                rboardroomsite = ripestr[1];
                rboardroomtelephone = ripestr[4];
                rList.add(new Boardroom(rboardroomname,rboardroomsite,rboardroomtelephone));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        LoadingData loadingData = new LoadingData();
        loadingData.start();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        try {
//            String deleteposition = getIntent().getStringExtra("position");
//            try {
//                int position  = Integer.parseInt(deleteposition);
//                boardroomList.remove(position);
//                adapter.notifyItemRemoved(position);
//                adapter.notifyItemRangeChanged(0,boardroomList.size()-position);
//                adapter.notifyDataSetChanged();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//            Toast.makeText(this,deleteposition,Toast.LENGTH_SHORT).show();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//    }


}
