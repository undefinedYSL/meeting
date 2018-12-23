package com.example.administrator.testonly;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyService extends Service {
    private Looper looper;
    private String s;
    private final String TAG = "startservice";
    private StartServiceHandler startServiceHandler;
    private String sendMessage = "ALL";
    //数据库操作
    private InformHelper newinformHelper;
    private SQLiteDatabase newinfromdb;
    private ContentValues values;
    private String[] str;
    private String responseData;

    public MyService() {
    }

    @Override
    public void onCreate() {
        //这里配置一些信息
        //启动运行服务的线程。
        //请记住我们要创建一个单独的线程，因为服务通常运行于进程的主线程中，可我们不想阻塞主线程。
        //我们还要赋予它后台运行的优先级，以便计算密集的工作不会干扰我们的UI。
        HandlerThread handlerThread = new HandlerThread("StartService");
        handlerThread.start();
        //获得HandlerThread的Looper队列并用于Handler
        looper = handlerThread.getLooper();
        startServiceHandler = new StartServiceHandler(looper);

        Log.e(TAG,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        s = intent.getStringExtra("StartServiceTest");
        //对于每一个启动请求，都发送一个消息来启动一个处理
        //同时传入启动ID，以便任务完成后我们知道该终止哪一个请求。
        Message message = startServiceHandler.obtainMessage();
        message.arg1 = 1;
        startServiceHandler.sendMessage(message);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    private class StartServiceHandler extends Handler{
        public StartServiceHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                sendRequestWithOkHttp();
//                storeResponse1();
            }catch (Exception e){
                e.printStackTrace();
            }
            //根据startId终止服务，这样我们就不会在处理其它工作的过程中再来终止服务
            //如果组件通过调用startService()（这会导致onStartCommand()的调用）启动了服务，那么服务将一直保持运行，直至自行用stopSelf()终止或由其它组件调用stopService()来终止它。
            //如果组件调用bindService()来创建服务（那onStartCommand()就不会被调用），则服务的生存期就与被绑定的组件一致。一旦所有客户端都对服务解除了绑定，系统就会销毁该服务。
            stopSelf(msg.arg1);
        }
    }

    private void storeResponse1() {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //ok实例化
                    OkHttpClient client = new OkHttpClient();
                    //表单数据
//                    FormBody.Builder builder = new FormBody.Builder();
//                    builder.add("inform",sendMessage);
//                    RequestBody formBody = builder.build();
                    RequestBody formBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"),sendMessage);
                    //发送请求
                    Request request = new Request.Builder()
                            .url("http://192.168.43.232:8888/myApps")
                            .post(formBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();

                    storeResponse(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void storeResponse(final String responseData) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                    //常规数据库操作
                    newinformHelper = new InformHelper(MyService.this,"NewInform.db",null,1);
                    newinfromdb = newinformHelper.getWritableDatabase();
                    values = new ContentValues();
                    values.put("informtitle","23333");
                    values.put("informcontent","666666");
                    long rowid = newinfromdb.insert("inform_table",null,values);

            }
        }).start();
//        str = responseData.split("@");
//        values.put("informtitle","23333");
//        values.put("informcontent","666666");
//        newinfromdb.insert("inform_table",null,values);
//        values.clear();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
