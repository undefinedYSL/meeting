package com.example.administrator.testonly;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.administrator.testonly.Constant.strurl;

public class InformActivity extends AppCompatActivity {

    private TextInputLayout tilInformtitle,tilInformcontent;
    private EditText etInformtitle;
    private TextInputEditText etInformcontent;
    private String informtitle,informcontent;
//    private Button btnsubmit;
    //单机数据库
    private InformHelper informHelper;
    private SQLiteDatabase infromdb;
    private ContentValues values;
    private String sendMessage;
    //改变编码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar7);
        toolbar.setTitle("发布通知");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tilInformtitle = (TextInputLayout)findViewById(R.id.til_informtitle);
        etInformtitle = (EditText)findViewById(R.id.et_informtitle);
        etInformtitle.addTextChangedListener(new MyTextWatcher(etInformtitle));
        tilInformcontent = (TextInputLayout)findViewById(R.id.til_informcontent);
        etInformcontent = (TextInputEditText)findViewById(R.id.et_informcontent);
        etInformcontent.addTextChangedListener(new MyTextWatcher(etInformcontent));
//        btnsubmit = (Button)findViewById(R.id.btn_submitinform);

        //常规数据库操作
        informHelper = new InformHelper(this,"Inform.db",null,1);
        infromdb = informHelper.getWritableDatabase();
        values = new ContentValues();

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

    public boolean isTitleValid(){
        try {
            informtitle = etInformtitle.getText().toString().trim();
            if (TextUtils.isEmpty(informtitle)){
                tilInformtitle.setErrorEnabled(true);
                tilInformtitle.setError("请输入正确标题");
                etInformtitle.requestFocus();
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        tilInformtitle.setErrorEnabled(false);
        return true;
    }

    public boolean isContentValid(){
        try {
            informcontent  = etInformcontent.getText().toString().trim();
            if (TextUtils.isEmpty(informcontent)){
                tilInformcontent.setErrorEnabled(true);
                tilInformcontent.setError("请输入正确内容");
                etInformcontent.requestFocus();
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        tilInformcontent.setErrorEnabled(false);
        return true;
    }

    public void click(View view) {
        if (!isTitleValid()){
            Toast.makeText(InformActivity.this,"请输入标题",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isContentValid()){
            Toast.makeText(InformActivity.this,"请输入内容",Toast.LENGTH_SHORT).show();
            return;
        }
//        Intent intent = new Intent();
//        intent.setClass(InformActivity.this,Main3Activity.class);
//        startActivity(intent);
        //在提交这里验证并将其保存到数据库
//        values.put("informtitle",informtitle);
//        values.put("informcontent",informcontent);
//        long rowid = infromdb.insert("inform_table",null,values);
//        values.clear();
        sendMessage = "inform"+"@"+informtitle+"@"+informcontent;
        sendRequestWithOkHttp();
        //此条toast测试用
//        Toast.makeText(InformActivity.this,"第"+Long.toString(rowid)+"条提交成功",Toast.LENGTH_SHORT).show();
        finish();
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
                case R.id.et_informtitle:
                    isTitleValid();
                    break;
                case R.id.et_informcontent:
                    isContentValid();
                    break;
                default:

            }
        }
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
                Toast.makeText(InformActivity.this,responseData,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
