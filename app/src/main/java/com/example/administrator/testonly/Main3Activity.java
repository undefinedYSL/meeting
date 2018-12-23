package com.example.administrator.testonly;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.administrator.testonly.Constant.strurl;

public class Main3Activity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;

    private SwipeRefreshLayout swipeRefresh;

    private FloatingActionButton fab;

    private boolean isAdd = false;
    //一个FAB的相对布局
    private RelativeLayout rlAddnote;
    //线性布局编号
    private int[] llID = new int[]{R.id.ll_releasemeeting,R.id.ll_releaseinform};
    //这个不太懂
    private LinearLayout[] ll = new LinearLayout[llID.length];
    //FAB编号
    private int[] fabID = new int[]{R.id.fab_rm,R.id.fab_ri};
    //这个依然不太懂
    private FloatingActionButton[] addfab = new FloatingActionButton[fabID.length];
    private AnimatorSet addNoteTranslate1,addNoteTranslate2;
    //测试专用1
//    private Meeting[] meetings = {new Meeting("123","12","哦","1999-1-1,3:00",R.drawable.ic_home),
//    new Meeting("321","34","喝","00年3月4号4点",R.drawable.ic_home)};
    //测试专用2
    private List<Meeting> meetingList = new ArrayList<>();
    //测试专用3
    private MeetingAdapter adapter;

    private RecyclerView recyclerView;

    //数据库操作
    private MeetingHelper mh;
    private SQLiteDatabase meetingdb;
    private List<Meeting> rList = new ArrayList<Meeting>();
    private Cursor cursor;
    private String rmeetingtitle,rmeetingsite,rmeetingdate,rmeetingtime,rmeetinghost;

    //网络操作
    private String[] rawstr;
    private String[] ripestr;
    private List<String> rmeeting;
    private int rmeetinglength;

    private String username1,password1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //滑动窗口的添加
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        //导航窗口的添加
        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
        View headerlayout = navView.inflateHeaderView(R.layout.nav_header);
        CircleImageView circleImageView = (CircleImageView)headerlayout.findViewById(R.id.icon_image);// 加入可以点击头像
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_h = new Intent();
                intent_h.setClass(Main3Activity.this,HeaderActivity.class);
                startActivity(intent_h);
            }
        });

        //定义侧边栏点击事件
        setupDrawerContent(navView);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu3);
        }


        //初始化新添fab的控件
        rlAddnote = (RelativeLayout)findViewById(R.id.rl_addnote);

        for (int i = 0;i<llID.length;i++){
            ll[i] = (LinearLayout)findViewById(llID[i]);
        }
        for (int i = 0;i<fabID.length;i++){
            addfab[i] = (FloatingActionButton)findViewById(fabID[i]);
        }
        //加载动画
        addNoteTranslate1 = (AnimatorSet)AnimatorInflater.loadAnimator(this,R.animator.add_note_anim);
        addNoteTranslate2 = (AnimatorSet)AnimatorInflater.loadAnimator(this,R.animator.add_note_anim);

        for (int i = 0;i<fabID.length;i++){
            addfab[i].setOnClickListener(this);
        }

        //FAB点击事件
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setImageResource(isAdd ? R.drawable.ic_navigatebefore:R.drawable.ic_navigateafter);
                isAdd = !isAdd;
                rlAddnote.setVisibility(isAdd ? View.VISIBLE:View.GONE);
                if (isAdd){
                    addNoteTranslate1.setTarget(ll[0]);
                    addNoteTranslate1.start();
                    addNoteTranslate2.setTarget(ll[1]);
                    addNoteTranslate2.start();
                }
            }
        });
//        //下拉刷新
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMeeting();
            }
        });
        //recyclerview初始化
        getRequestWithOkhttp();
        recyclerView = (RecyclerView)findViewById(R.id.rv_main);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        adapter = new MeetingAdapter(meetingList);
        recyclerView.setAdapter(adapter);
    }


    private void refreshMeeting() {
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
                        getRequestWithOkhttp();
                        initMeetings();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void initMeetings() {
//        readDatabase();
        meetingList.clear();
//        getRequestWithOkhttp();
        try {
//            int index = cursor.getCount();
            //测试用
            for (int i = 0;i<rmeetinglength;i++){
                meetingList.add(rList.get(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void readDatabase() {
        //数据库取数据
        mh = new MeetingHelper(Main3Activity.this,"Meeting.db",null,1);
        meetingdb = mh.getWritableDatabase();
        cursor = meetingdb.query("meeting_table",null,null,null,null,null,null);
        try {
            if(cursor.moveToFirst()) {
                do {
                    rmeetingtitle = cursor.getString(cursor.getColumnIndex("meetingtitle"));
                    rmeetingsite = cursor.getString(cursor.getColumnIndex("meetingsite"));
                    rmeetinghost = cursor.getString(cursor.getColumnIndex("attendant"));
                    rmeetingdate = cursor.getString(cursor.getColumnIndex("meetingdate"));
                    rmeetingtime = cursor.getString(cursor.getColumnIndex("meetingtime"));
                    rList.add(new Meeting(rmeetingtitle,rmeetingsite,rmeetinghost,rmeetingdate,rmeetingtime,R.drawable.ic_home));
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

    private void getRequestWithOkhttp() {
        final String sendMessage = "getMeeting";
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

    private void handleResponse(final String responseData){
        rList.clear();
        rawstr = responseData.split("#");
        rmeetinglength = rawstr.length-1;
        try {
            for (int i=1;i<=rmeetinglength;i++){
                ripestr = rawstr[i].split("@");
                rmeetingtitle = ripestr[0];
                rmeetingdate = ripestr[1];
                rmeetingtime = ripestr[2];
                rmeetingsite = ripestr[4];
                rmeetinghost = ripestr[5];
                rList.add(new Meeting(rmeetingtitle,rmeetingsite,rmeetinghost,rmeetingdate,rmeetingtime,R.drawable.ic_home));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        LoadingData loadingData = new LoadingData();
        loadingData.start();
    }

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x666:
//                    recyclerView.setAdapter(adapter);
                    initMeetings();
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
            Main3Activity.this.myHandler.sendEmptyMessage(0x666);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected( MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.nav_boardroom:
                                Intent intent1 = new Intent();
                                intent1.setClass(Main3Activity.this,BoardroomActivity.class);
                                startActivity(intent1);
                                break;
                            case R.id.nav_message:
                                Intent intent2 = new Intent();
                                intent2.setClass(Main3Activity.this,MessageActivity.class);
                                startActivity(intent2);
                                break;
                            case R.id.nav_maillist:
                                Intent intent3 = new Intent();
                                intent3.setClass(Main3Activity.this,MaillistActivity.class);
                                startActivity(intent3);
                                break;
                            case R.id.nav_more:
                                Intent intent4 = new Intent();
                                intent4.setClass(Main3Activity.this,MoreActivity.class);
                                startActivity(intent4);
                                break;
                        }
                        return true;
                    }
                }
        );
    }

    //toolbar右上角菜单的添加
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar,menu);
        return  true;
    }

    //顶部各图标点击效果
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.search:
                startActivity(new Intent(Main3Activity.this,SearchActivity.class));
                break;
                default:
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_rm:
                Intent intent1 = new Intent();
                intent1.setClass(Main3Activity.this,MeetingActivity.class);
                startActivity(intent1);
                rlAddnote.setVisibility(View.GONE);
                fab.setImageResource(R.drawable.ic_navigatebefore);
                isAdd = false;
                break;
            case R.id.fab_ri:
                Intent intent2 = new Intent();
                intent2.setClass(Main3Activity.this,InformActivity.class);
                startActivity(intent2);
                rlAddnote.setVisibility(View.GONE);
                fab.setImageResource(R.drawable.ic_navigatebefore);
                isAdd = false;
                break;
        }
    }

    //添加返回键监听
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK){
            dialog();
            return true;
        }else {
            return super.onKeyDown(keyCode,event);
        }
    }

    protected void dialog() {
        Dialog dialog = new AlertDialog.Builder(this).setTitle("会议管理平台").setMessage(
                "确定退出应用程序？").setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
//                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        }).create();
        dialog.show();

    }
}
