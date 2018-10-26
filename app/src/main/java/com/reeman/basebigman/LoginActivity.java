package com.reeman.basebigman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.iflytek.cloud.InitListener;
import com.reeman.basebigman.fragment.MainFragment;
import com.reeman.basebigman.manager.ChargeManager;
import com.reeman.basebigman.manager.NerveManager;
import com.reeman.basebigman.process.SpeechRecoProcess;
import com.reeman.basebigman.process.SpeechResultProcess;
import com.reeman.nerves.RobotActionProvider;
import com.speech.processor.SpeechPlugin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import robot.boocax.com.sdkmodule.TCP_CONN;
import robot.boocax.com.sdkmodule.entity.entity_app.LoginEntity;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.AllRobotMacList;
import robot.boocax.com.sdkmodule.setlog.SetLog;
import robot.boocax.com.sdkmodule.utils.init_files.NaviContext;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final String CONN_IP = "192.168.1.184";
    private MainFragment mMainFragment;
    public static Context mContext;

    /**
     * SDK内容
     */
    public static SharedPreferences sp_curDoc;
    public static SharedPreferences.Editor editor_curDoc;//用于记录文件(服务器传来)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();
        initView();
        init();
        initFilter();
        register();
        connectServer();
    }

    private void register() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    // 连接到服务端 startService
    private void connectServer() {

        initContext();
        instanceData();
        LoginEntity.serverIP = CONN_IP;
        //获取serverIP
        SetLog.recvJson_Debug = true;
        TCP_CONN.isSendReconn = true;               //是否开启重连
        TCP_CONN.loopMark = true;                   //开启TCP主循环;
        startService(new Intent(LoginActivity.this, MyService_verify.class));

    }

    /**
     * 同步app与SDK环境变量
     */
    private void initContext() {
        if (NaviContext.context == null) {
            NaviContext.context = getApplicationContext();
        }
        //创建SharedPreferences,用于记录文件清单
        sp_curDoc = getSharedPreferences("recordDoc", MODE_PRIVATE);
        editor_curDoc = sp_curDoc.edit();
        //获取SharedPreferences地址
        if (TCP_CONN.sp_curDoc == null) {
            TCP_CONN.sp_curDoc = sp_curDoc;
        }
        if (TCP_CONN.editor_curDoc == null) {
            TCP_CONN.editor_curDoc = editor_curDoc;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SpeechPlugin.getInstance().startRecognize();    //打开录音开关
        NerveManager.getInstance().init();  //初始化外设
    }

    @Override
    protected void onStop() {
        super.onStop();
        NerveManager.getInstance().uninit();
    }

    /**
     * 收到新传来的Robot的Mac
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getMac(AllRobotMacList allRobotMacList) {
        if (allRobotMacList != null) {
            Log.d("mac", "getMac: " + allRobotMacList.getList().get(0));
            LoginEntity.robotMac = allRobotMacList.getList().get(0);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        SpeechPlugin.getInstance().stopRecognize();
        SpeechPlugin.getInstance().stopSpeak();
        SpeechPlugin.getInstance().onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(receiver);
    }

    private void init() {
        // register this  <-----> to unregister it
        SpeechPlugin.CreateInstance(this);  //init ai
        Log.e("LoginActivity",
                "===:" + (SpeechPlugin.getInstance() == null) + " / " + (RobotActionProvider.getInstance() == null));
        SpeechPlugin.getInstance().setDevID(RobotActionProvider.getInstance().getRobotID());
        SpeechPlugin.getInstance().setRecognizeListener(new SpeechRecoProcess());  // 设置识别处理
        SpeechPlugin.getInstance().setResultProcessor(new SpeechResultProcess());    // 设置结果处理

        //        SpeechPlugin.getInstance().setViewSpeakListener(null);      //设置语音合成（文字转语音）回调 合成被打断，合成开始，合成结束
        SpeechPlugin.getInstance().setCanRecognizeAIUI(true);
        SpeechPlugin.getInstance().setAIScene("main");    //讯飞语料场景设置
        SpeechPlugin.getInstance().setRecognizeZonn("吉迈导航|艾芯智能|商务机器人闲聊"); //按优先级设置知识库
    }



    private void initFilter() {

        IntentFilter filter = new IntentFilter();
        filter.addAction("REEMAN_BROADCAST_WAKEUP");
        filter.addAction("REEMAN_BROADCAST_SCRAMSTATE");
        filter.addAction("REEMAN_LAST_MOVTION");
        filter.addAction("ACTION_POWER_CONNECTE_REEMAN");
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction("AUTOCHARGE_ERROR_DOCKNOTFOUND");
        filter.addAction("AUTOCHARGE_ERROR_DOCKINGFAILURE");
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "=====receiver:" + action);
            if ("REEMAN_BROADCAST_WAKEUP".equals(action)) { //唤醒广播
                RobotActionProvider.getInstance().setBeam(0);
                SpeechPlugin.getInstance().startRecognize();
                int angle = intent.getIntExtra("REEMAN_8MIC_WAY", 0);   //唤醒角度
                // 进行唤醒处理
                NerveManager.getInstance().wakeUp(angle);
            } else if ("REEMAN_BROADCAST_SCRAMSTATE".equals(action)) { //急停开关状态监听广播
                int stopState = intent.getIntExtra("SCRAM_STATE", -1);
                NerveManager.stopState = stopState;
            } else if ("REEMAN_LAST_MOVTION".equals(action)) {      //运动结束广播
                int type = intent.getIntExtra("REEMAN_MOVTION_TYPE", 0); // 16,17,18 前左右
            } else if ("ACTION_POWER_CONNECTE_REEMAN".equals(action) || Intent.ACTION_BATTERY_CHANGED.equals(
                    action) || "AUTOCHARGE_ERROR_DOCKNOTFOUND".equals(
                    action) || "AUTOCHARGE_ERROR_DOCKINGFAILURE".equals(action))    //电量，充电相关广播
            {
                ChargeManager.getInstance().batteryUpdate(intent);
            } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) { //网络状态改变

            }
        }
    };


    private void initView() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (mMainFragment == null) {
            mMainFragment = MainFragment.newInstance();
            transaction.add(R.id.am_fl_content, mMainFragment);
        }
        transaction.commit();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        View mDecorView = getWindow().getDecorView();

        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    /**
     * 定义SDK接收文件种类
     */
    private void instanceData() {
        LoginEntity.recvFileTypes = new ArrayList<>();
        LoginEntity.recvFileTypes.add("map.png");
        LoginEntity.recvFileTypes.add("restrict.dat");
        LoginEntity.recvFileTypes.add("anchor.dat");
        LoginEntity.recvFileTypes.add("poi.json");
        LoginEntity.recvFileTypes.add("agv_graph.json");
        //定义Android客户端接收的文件类型,SDK使用者根据自身客户端功能选择需要接收的文件
    }

}
