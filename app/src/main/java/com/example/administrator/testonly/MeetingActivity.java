package com.example.administrator.testonly;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.administrator.testonly.Constant.strurl;

public class MeetingActivity extends AppCompatActivity {

    private TextInputLayout tilMeetingtitle,tilMeetingdate,tilMeetingtime,tilMeetingduration,tilMeetingsite,tilAttendants,tilMeetingdescription;
    private TextInputEditText etMeetingtitle,etMeetingdate,etMeetingtime,etMeetingduration,etMeetingsite,etAttendants,etMeetingdescription;
    private String meetingtitle,meetingdate,meetingtime,meetingduration,meetingsite,attendants,meetingdescription;
    private ScrollView scrollView = null;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private AlertDialog siteDailog;
    private Calendar calendar;

    //单机数据库
    private MeetingHelper meetingHelper;
    private SQLiteDatabase meetingdb;
    private ContentValues values;

    //联机专用
    private String sendMessage;//最后提交的数据
    private String requestMessage;//请求头

    private String[] rawstr;//第一次分割数组
    private String[] ripestr;//第二次分割数组
    private String[] rboardroom;
    private String rboardroom1;
    private int rboardroomlength;//接受到的数据长度
    private List<String> rList = new ArrayList<String>();
    private String str1;
    public static final int UPDATE_SITE = 1;
    private int choice = -1;

    private Handler sitehandler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_SITE:
                    etMeetingsite.setText(meetingsite);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar6);
        toolbar.setTitle("发布会议");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tilMeetingtitle = (TextInputLayout)findViewById(R.id.til_meetingtitle);
        tilMeetingdate = (TextInputLayout)findViewById(R.id.til_meetingdate);
        tilMeetingtime = (TextInputLayout)findViewById(R.id.til_meetingtime);
        tilMeetingduration = (TextInputLayout)findViewById(R.id.til_meetingduration);
        tilMeetingsite = (TextInputLayout)findViewById(R.id.til_meetingsite);
        tilAttendants = (TextInputLayout)findViewById(R.id.til_attendants);
        tilMeetingdescription = (TextInputLayout)findViewById(R.id.til_meetingdescription);

        etMeetingtitle = (TextInputEditText)findViewById(R.id.et_meetingtitle);
        etMeetingdate = (TextInputEditText)findViewById(R.id.et_meetingdate);
        etMeetingtime = (TextInputEditText)findViewById(R.id.et_meetingtime);
        etMeetingduration = (TextInputEditText)findViewById(R.id.et_meetingduration);
        etMeetingsite = (TextInputEditText)findViewById(R.id.et_meetingsite);
        etAttendants = (TextInputEditText)findViewById(R.id.et_attendants);
        etMeetingdescription = (TextInputEditText)findViewById(R.id.et_meetingdescription);
        
        etMeetingtitle.addTextChangedListener(new MyTextWatcher(etMeetingtitle));
        etMeetingdate.addTextChangedListener(new MyTextWatcher(etMeetingdate));
        etMeetingtime.addTextChangedListener(new MyTextWatcher(etMeetingtime));
        etMeetingduration.addTextChangedListener(new MyTextWatcher(etMeetingduration));
        etMeetingsite.addTextChangedListener(new MyTextWatcher(etMeetingsite));
        etAttendants.addTextChangedListener(new MyTextWatcher(etAttendants));
        etMeetingdescription.addTextChangedListener(new MyTextWatcher(etMeetingdescription));

        scrollView = (ScrollView)findViewById(R.id.sv_meeting);

        calendar = Calendar.getInstance();

        etMeetingdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (TextUtils.isEmpty(meetingdate)){
                        if (view.getId()==etMeetingdate.getId()){
                            showDateDialog();
                        }
                }else {
                    return;
                }

            }
        });

        etMeetingtime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (TextUtils.isEmpty(meetingtime)){
                    if(view.getId()==etMeetingtime.getId()){
                        showTimeDialog();
                    }
                }else {
                    return;
                }
            }
        });

        etMeetingsite.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (TextUtils.isEmpty(meetingsite)){
                    if (view.getId()==etMeetingsite.getId()){
                        if (requestMessage.equals("send")){
                            showSiteDialog();
                        }
                    }
                }else {
                    return;
                }
            }
        });

        //常规数据库操作
//        meetingHelper = new MeetingHelper(this,"Meeting.db",null,1);
//        meetingdb = meetingHelper.getWritableDatabase();
//        values = new ContentValues();

        getBoardroomRequestWithOkhttp();
    }

    private void getBoardroomRequestWithOkhttp() {
        final String sendMessage1 = "getBoardroom";
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
                    handleResponse(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleResponse(final String boardroomMessage) {
        rList.clear();
        rawstr = boardroomMessage.split("#");
        rboardroomlength = rawstr.length-1;
        String[] getMessage;
        getMessage = rawstr[0].split("@");
        requestMessage = getMessage[0];

        try {
            for (int i=1;i<=rboardroomlength;i++){
                ripestr = rawstr[i].split("@");
                rboardroom1 = ripestr[0];
                rList.add(rboardroom1);
            }
//            str1 = rList.get(0);
//            rList.toArray(rboardroom);错误示范
            rboardroom = rList.toArray(new String[rList.size()]);
        }catch (Exception e){
            e.printStackTrace();
        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(MeetingActivity.this,rboardroom[1],Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void showSiteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,1);
        builder.setTitle("会议室选择");
//        builder.setIcon(R.mipmap.rog);
        builder.setSingleChoiceItems(rboardroom, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                choice = i;
                Toast.makeText(MeetingActivity.this,"已选中"+Integer.toString(i),Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int i) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        meetingsite = rboardroom[choice];
                        Message message = new Message();
                        message.what = UPDATE_SITE;
                        sitehandler.sendMessage(message);
                    }
                }).start();
//                meetingsite = rboardroom[i];
//                etMeetingsite.setText(meetingsite);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showDateDialog() {
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dayofmonth) {
                meetingdate = String.valueOf(year) + "年" + String.valueOf(monthofyear + 1) + "月" + Integer.toString(dayofmonth)+"日";
                etMeetingdate.setText(meetingdate);

                //Log.d("测试", time);
            }
        },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
        datePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                etMeetingdate.clearFocus();
//
////                etMeetingtime.setFocusableInTouchMode(true);
////                etMeetingtime.setFocusable(true);
////                etMeetingtime.requestFocus();
////                etMeetingdate.setFocusable(false);
//            }
//        });
    }

    public void showTimeDialog(){
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String newhour,newminute;
                if (hour>=0 && hour<10){
                    newhour = "0"+Integer.toString(hour);
                }else {
                    newhour = Integer.toString(hour);
                }
                if (minute>=0 && minute<10){
                    newminute = "0"+Integer.toString(minute);
                }else {
                    newminute = Integer.toString(minute);
                }
                meetingtime = newhour+":"+newminute;
                etMeetingtime.setText(meetingtime);
            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);
        timePickerDialog.show();
        timePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

//    public void showTimeDialogTwo(){
//        View view = LayoutInflater.from(this).inflate(R.layout.dialog_time,null);
//        final TimePicker startTime = (TimePicker) view.findViewById(R.id.tp_meetingstart);
//        final TimePicker endTime = (TimePicker) view.findViewById(R.id.tp_meetingend);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("选择时间");
//        builder.setView(view);
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                String st1 = "";
//                String st2 = "";
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                    st1 = Integer.toString(startTime.getHour())+":"+Integer.toString(startTime.getMinute());
//                }
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                    st2 = Integer.toString(endTime.getHour())+":"+Integer.toString(endTime.getMinute());
//                }
//                meetingtime = st1+"-"+st2;
//                etMeetingtime.setText(meetingtime);
//            }
//        });
//        builder.setPositiveButton("取消",null);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void click(View view) {
        if (!isTitleValid()){
            Toast.makeText(MeetingActivity.this,"请输入标题",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isDateValid()){
            Toast.makeText(MeetingActivity.this,"请输入日期",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isTimeValid()){
            Toast.makeText(MeetingActivity.this,"请输入时间",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isDurationValid()){
            Toast.makeText(MeetingActivity.this,"请输入时长",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isSiteValid()){
            Toast.makeText(MeetingActivity.this,"请输入地点",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isAttendantsValid()){
            Toast.makeText(MeetingActivity.this,"请输入人员",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isDescriptionValid()){
            Toast.makeText(MeetingActivity.this,"请输入说明",Toast.LENGTH_SHORT).show();
            return;
        }
//        Intent intent = new Intent();
//        intent.setClass(InformActivity.this,Main3Activity.class);
//        startActivity(intent);
        //在提交这里验证并将其保存到数据库
//        values.put("meetingtitle",meetingtitle);
//        values.put("meetingdate",meetingdate);
//        values.put("meetingtime",meetingtime);
//        values.put("meetingduration",meetingduration);
//        values.put("meetingsite",meetingsite);
//        values.put("attendant",attendants);
//        values.put("meetingdescription",meetingdescription);
//        long rowid = meetingdb.insert("meeting_table",null,values);
//        values.clear();
        sendMessage = "meeting@"+meetingtitle+"@"+meetingdate+"@"+meetingtime+"@"+meetingduration+"@"+meetingsite+"@"+attendants+"@"+meetingdescription;
        sendRequestWithOkhttp();
        //此条toast测试用
//        Toast.makeText(MeetingActivity.this,"第"+Long.toString(rowid)+"条提交成功",Toast.LENGTH_SHORT).show();
        finish();
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
                Toast.makeText(MeetingActivity.this,responseData,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isTitleValid(){
        try {
            meetingtitle  = etMeetingtitle.getText().toString().trim();
            if (TextUtils.isEmpty(meetingtitle)){
                tilMeetingtitle.setErrorEnabled(true);
                tilMeetingtitle.setError("请输入正确标题");
                etMeetingtitle.requestFocus();
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        tilMeetingtitle.setErrorEnabled(false);
        return true;
    }

    public boolean isDateValid(){
        try {
            meetingdate  = etMeetingdate.getText().toString().trim();
            if (TextUtils.isEmpty(meetingdate)){
                tilMeetingdate.setErrorEnabled(true);
                tilMeetingdate.setError("请输入正确日期");
                etMeetingdate.requestFocus();
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        tilMeetingdate.setErrorEnabled(false);
        return true;
    }

    public boolean isTimeValid(){
        try {
            meetingtime  = etMeetingtime.getText().toString().trim();
            if (TextUtils.isEmpty(meetingtime)){
                tilMeetingtime.setErrorEnabled(true);
                tilMeetingtime.setError("请输入正确时间");
                etMeetingtime.requestFocus();
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        tilMeetingtime.setErrorEnabled(false);
        return true;
    }

    public boolean isDurationValid(){
        try {
            meetingduration  = etMeetingduration.getText().toString().trim();
            if (TextUtils.isEmpty(meetingduration)){
                tilMeetingduration.setErrorEnabled(true);
                tilMeetingduration.setError("请输入正确时长");
                etMeetingduration.requestFocus();
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        tilMeetingduration.setErrorEnabled(false);
        return true;
    }

    public boolean isSiteValid(){
        try {
            meetingsite  = etMeetingsite.getText().toString().trim();
            if (TextUtils.isEmpty(meetingsite)){
                tilMeetingsite.setErrorEnabled(true);
                tilMeetingsite.setError("请输入正确位置");
                etMeetingsite.requestFocus();
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        tilMeetingsite.setErrorEnabled(false);
        return true;
    }

    public boolean isAttendantsValid(){
        try {
            attendants  = etAttendants.getText().toString().trim();
            if (TextUtils.isEmpty(attendants)){
                tilAttendants.setErrorEnabled(true);
                tilAttendants.setError("请输入人员，用；隔开");
                etAttendants.requestFocus();
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        tilAttendants.setErrorEnabled(false);
        return true;
    }

    public boolean isDescriptionValid(){
        try {
            meetingdescription  = etMeetingdescription.getText().toString().trim();
            if (TextUtils.isEmpty(meetingdescription)){
                tilMeetingdescription.setErrorEnabled(true);
                tilMeetingdescription.setError("请输入正确描述");
                etMeetingdescription.requestFocus();
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        tilMeetingdescription.setErrorEnabled(false);
        return true;
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (view.getId()){
                case R.id.et_meetingtitle:
                    isTitleValid();
                    break;
                case R.id.et_meetingdate:
                    isDateValid();
                    break;
                case R.id.et_meetingtime:
                    isTimeValid();
                    break;
                case R.id.et_meetingduration:
                    isDurationValid();
                    break;
                case R.id.et_meetingsite:
                    isSiteValid();
                    break;
                case R.id.et_attendants:
                    isAttendantsValid();
                    break;
                case R.id.et_meetingdescription:
                    isDescriptionValid();
                    break;
                default:
            }
        }
    }


}
