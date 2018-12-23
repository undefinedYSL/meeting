package com.example.administrator.testonly;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.administrator.testonly.Constant.strurl;

public class AddBoardroomActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private TextInputLayout tilBoardroomname,tilBoardroomsite,tilBoardroomconfiguration,tilBoardroomcapacity,tilBoardroomtelephone,tilBoardroomremark;
    private TextInputEditText etBoardroomname,etBoardroomsite,etBoardroomconfiguration,etBoardroomcapacity,etBoardroomtelephone,etBoardroomremark;
    private String boardroomname,boardroomsite,boardroomconfiguration,boardroomcapacity,boardroomtelephone,boardroomremark;


    //单机数据库
    private BoardroomHelper boardroomHelper;
    private SQLiteDatabase boardroomdb;
    private ContentValues values;

    private String sendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_boardroom);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar11);
        toolbar.setTitle("增加会议室");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        scrollView = (ScrollView)findViewById(R.id.sv_boardroom);

        tilBoardroomname = (TextInputLayout)findViewById(R.id.til_boardroomname);
        tilBoardroomsite = (TextInputLayout)findViewById(R.id.til_boardroomsite);
        tilBoardroomconfiguration = (TextInputLayout)findViewById(R.id.til_boardroomconfiguration);
        tilBoardroomcapacity = (TextInputLayout)findViewById(R.id.til_boardroomcapacity);
        tilBoardroomtelephone = (TextInputLayout)findViewById(R.id.til_boardroomtelephone);
        tilBoardroomremark = (TextInputLayout)findViewById(R.id.til_boardroomremark);

        etBoardroomname = (TextInputEditText)findViewById(R.id.et_boardroomname);
        etBoardroomsite = (TextInputEditText)findViewById(R.id.et_boardroomsite);
        etBoardroomconfiguration = (TextInputEditText)findViewById(R.id.et_boardroomconfiguration);
        etBoardroomcapacity = (TextInputEditText)findViewById(R.id.et_boardroomcapacity);
        etBoardroomtelephone = (TextInputEditText)findViewById(R.id.et_boardroomtelephone);
        etBoardroomremark = (TextInputEditText)findViewById(R.id.et_boardroomremark);

        etBoardroomremark.addTextChangedListener(new MyTextWatcher(etBoardroomremark));
        etBoardroomname.addTextChangedListener(new MyTextWatcher(etBoardroomname));
        etBoardroomtelephone.addTextChangedListener(new MyTextWatcher(etBoardroomtelephone));
        etBoardroomcapacity.addTextChangedListener(new MyTextWatcher(etBoardroomcapacity));
        etBoardroomconfiguration.addTextChangedListener(new MyTextWatcher(etBoardroomconfiguration));
        etBoardroomsite.addTextChangedListener(new MyTextWatcher(etBoardroomsite));

//        boardroomHelper = new BoardroomHelper(this,"Boardroom.db",null,1);
//        boardroomdb = boardroomHelper.getWritableDatabase();
//        values = new ContentValues();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public boolean isNameValid(){
        try {
            boardroomname = etBoardroomname.getText().toString().trim();
            if (TextUtils.isEmpty(boardroomname)){
                tilBoardroomname.setErrorEnabled(true);
                tilBoardroomname.setError("请输入正确名称");
                etBoardroomname.requestFocus();
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        tilBoardroomname.setErrorEnabled(false);
        return true;
    }

    public boolean isSiteValid(){
        try {
            boardroomsite = etBoardroomsite.getText().toString().trim();
            if (TextUtils.isEmpty(boardroomsite)){
                tilBoardroomsite.setErrorEnabled(true);
                tilBoardroomsite.setError("请输入正确地址");
                etBoardroomsite.requestFocus();
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        tilBoardroomsite.setErrorEnabled(false);
        return true;
    }

    public boolean isConfigurationValid(){
        try {
            boardroomconfiguration = etBoardroomconfiguration.getText().toString().trim();
            if (TextUtils.isEmpty(boardroomconfiguration)){
                tilBoardroomconfiguration.setErrorEnabled(true);
                tilBoardroomconfiguration.setError("请输入设备配置，用；隔开");
                etBoardroomconfiguration.requestFocus();
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        tilBoardroomconfiguration.setErrorEnabled(false);
        return true;
    }

    public boolean isCapacityValid(){
        try {
            boardroomcapacity = etBoardroomcapacity.getText().toString().trim();
            if (TextUtils.isEmpty(boardroomcapacity)){
                tilBoardroomcapacity.setErrorEnabled(true);
                tilBoardroomcapacity.setError("请输入合理人数");
                etBoardroomcapacity.requestFocus();
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        tilBoardroomcapacity.setErrorEnabled(false);
        return true;
    }

    public boolean isTelephoneValid(){
        try {
            boardroomtelephone = etBoardroomtelephone.getText().toString().trim();
            if (TextUtils.isEmpty(boardroomtelephone)){
                tilBoardroomtelephone.setErrorEnabled(true);
                tilBoardroomtelephone.setError("请输入正确电话号码");
                etBoardroomtelephone.requestFocus();
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        tilBoardroomtelephone.setErrorEnabled(false);
        return true;
    }

    public boolean isRemarkValid(){
        try {
            boardroomremark = etBoardroomremark.getText().toString().trim();
            if (TextUtils.isEmpty(boardroomremark)){
                tilBoardroomremark.setErrorEnabled(true);
                tilBoardroomremark.setError("请输入备注");
                etBoardroomremark.requestFocus();
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        tilBoardroomremark.setErrorEnabled(false);
        return true;
    }

    public void click(View view) {
        if (!isNameValid()){
            Toast.makeText(AddBoardroomActivity.this,"请输入名称",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isSiteValid()){
            Toast.makeText(AddBoardroomActivity.this,"请输入地址",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isConfigurationValid()){
            Toast.makeText(AddBoardroomActivity.this,"请输入配置",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isCapacityValid()){
            Toast.makeText(AddBoardroomActivity.this,"请输入人数",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isTelephoneValid()){
            Toast.makeText(AddBoardroomActivity.this,"请输入电话",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isRemarkValid()){
            Toast.makeText(AddBoardroomActivity.this,"请输入备注",Toast.LENGTH_SHORT).show();
            return;
        }

//        values.put("boardroomname",boardroomname);
//        values.put("boardroomsite",boardroomsite);
//        values.put("boardroomconfiguration",boardroomconfiguration);
//        values.put("boardroomcapacity",boardroomcapacity);
//        values.put("boardroomtelephone",boardroomtelephone);
//        values.put("boardroomremark",boardroomremark);
//        long rowid = boardroomdb.insert("boardroom_table",null,values);
//        values.clear();
        sendMessage = "boardroom@"+boardroomname+"@"+boardroomsite+"@"+boardroomconfiguration+"@"+boardroomcapacity+"@"+boardroomtelephone+"@"+boardroomremark;
        sendRequestWithOkhttp();
        //此条toast测试用
//        Toast.makeText(AddBoardroomActivity.this,"第"+Long.toString(rowid)+"条提交成功",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AddBoardroomActivity.this,responseData,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        public MyTextWatcher(View view) {
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
                case R.id.et_boardroomname:
                    isNameValid();
                    break;
                case R.id.et_boardroomsite:
                    isSiteValid();
                    break;
                case R.id.et_boardroomconfiguration:
                    isConfigurationValid();
                    break;
                case R.id.et_boardroomcapacity:
                    isCapacityValid();
                    break;
                case R.id.et_boardroomtelephone:
                    isTelephoneValid();
                    break;
                case R.id.et_boardroomremark:
                    isRemarkValid();
                    break;
                default:
            }
        }
    }
}
