package com.reeman.basebigman;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.reeman.basebigman.constant.MyEvent;
import com.reeman.basebigman.manager.NavigationManager;
import com.reeman.basebigman.manager.NerveManager;
import com.reeman.basebigman.presenter.MainPresenter;
import com.speech.processor.SpeechPlugin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

public class MedicalVideoInitActivity extends AppCompatActivity {

    private static final String TAG = "MedicalVideoInitActivit";

    @BindView(R.id.medical_videoView)
    VideoView medicalVideoView;
    @BindView(R.id.medicalVideo_to_login)
    ImageView medicalVideoToLogin;

    private String medialName;
    private String[] mediaArray;
    private int type;
    private Handler mOffGroupHandler;
    private Handler mOffHandler;
    private Timer mOffTime;
    private TextView mOffTextView;
    private TimerTask mTimerTask;

    //作比较 1为再来一次, 2为播放结束
    private int compare = 0;
    private int compareDialog = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_video_init);
        hideNavigation();
        initView();
        EventBus.getDefault().register(this);
    }

    private void hideNavigation() {
        View decorView = this.getWindow().getDecorView();
        int option = SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(option);
    }

    private void initView() {
        ButterKnife.bind(this);
        medicalVideoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationManager.getInstance().navigationByName("充电站");
                startActivity(new Intent(MedicalVideoInitActivity.this, LoginActivity.class));
                finish();
            }
        });
        mOffTextView = new TextView(this);
        type = getIntent().getIntExtra("videoURLType", 2);
        switch (type) {
            case 1:
                /**
                 * 单个视频的情况
                 */
                medialName = getIntent().getStringExtra("medialData");
                Log.i("videoURlInit", medialName);
                initVideo(medialName);
                medicalVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        //播放完毕
                        final AlertDialog alertDialog = new AlertDialog.Builder(MedicalVideoInitActivity.this)
                                .setTitle(R.string.play_ended)
                                .setMessage(R.string.play_again_makesure)
                                .setView(mOffTextView)
                                .setCancelable(false)
                                .setNegativeButton(R.string.play_ended, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(), "点击了取消按钮", Toast.LENGTH_SHORT).show();
                                        NavigationManager.getInstance().navigationByName("充电站");
                                        startActivity(new Intent(MedicalVideoInitActivity.this, LoginActivity.class));
                                        dialog.cancel();
                                        mOffTime.cancel();
                                        finish();
                                    }
                                })
                                .setPositiveButton(R.string.play_again, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(), "点击了确认按钮", Toast.LENGTH_SHORT).show();
                                        mOffTime.cancel();
                                        mOffTextView = null;
                                        initVideo(medialName);
                                    }
                                }).create();
                        alertDialog.show();
                        compareDialog = 1;

                        mOffHandler = new Handler() {
                            public void handleMessage(Message msg) {

                                if (msg.what > 0) {
                                    ////动态显示倒计时
                                    mOffTextView.setText("    即将关闭："+msg.what);
                                    //语音控制1为再来一次, 2为播放结束
                                    switch (compare){
                                        case 1:
                                            alertDialog.dismiss();
                                            if (mTimerTask != null){
                                                mTimerTask.cancel();
                                                mTimerTask = null;
                                            }
                                            if (mOffTime != null){
                                                mOffTime.cancel();
                                                mOffTime.purge();
                                                mOffTime = null;
                                            }
                                            mOffTextView = null;
                                            initVideo(medialName);
                                            break;
                                        case 2:
                                            NavigationManager.getInstance().navigationByName("充电站");
                                            startActivity(new Intent(MedicalVideoInitActivity.this, LoginActivity.class));
                                            alertDialog.dismiss();
                                            mOffTime.cancel();
                                            finish();
                                        default:
                                            break;
                                    }
                                    compare = 0;
                                } else {
                                    ////倒计时结束自动关闭
                                    NavigationManager.getInstance().navigationByName("充电站");
                                    alertDialog.dismiss();
                                    mOffTime.cancel();
                                    startActivity(new Intent(MedicalVideoInitActivity.this, LoginActivity.class));
                                    finish();
                                }
                                super.handleMessage(msg);
                            }
                        };


                        mOffTime = new Timer(true);
                        mTimerTask = new TimerTask() {
                            int countTime = 10;
                            public void run() {
                                if (countTime > 0) {
                                    countTime--;
                                }
                                Message msg = new Message();
                                msg.what = countTime;
                                mOffHandler.sendMessage(msg);
                            }
                        };
                        mOffTime.schedule(mTimerTask, 1000, 1000);
                    }
                });
                break;
            case 2:
                /**
                 * 多个视频的情况
                 */
                Bundle b = this.getIntent().getExtras();
                mediaArray = b.getStringArray("videoArraty");
                if (mediaArray != null) {
                    initVideoGroup(mediaArray);
                }
                break;
            default:
                break;
        }
    }

    //初始化视频播放
    @SuppressLint("LongLogTag")
    private void initVideo(String medialName) {
        mOffTextView = new TextView(this);
        String path = medialName;
        Log.i("initVideo", path);
        Uri uri = Uri.parse(path);
        medicalVideoView.setVideoURI(uri);
        medicalVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                medicalVideoView.start();//开始播放视频
            }
        });
    }

    private void initVideoGroup(final String[] medialArray) {
        mOffTextView = new TextView(this);
        final int[] num = {0};
        initVideo(medialArray[num[0]]);
        num[0]++;
        medicalVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (num[0] == medialArray.length) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(MedicalVideoInitActivity.this)
                            .setTitle(R.string.play_ended)
                            .setView(mOffTextView)
                            .setMessage(R.string.play_again_makesure)
                            .setCancelable(false)
                            .setNegativeButton(R.string.play_ended, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "点击了取消按钮", Toast.LENGTH_SHORT).show();
                                    NavigationManager.getInstance().navigationByName("充电站");
                                    startActivity(new Intent(MedicalVideoInitActivity.this, LoginActivity.class));
                                    dialog.cancel();
                                    mOffTime.cancel();
                                    finish();
                                }
                            })
                            .setPositiveButton(R.string.play_again, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "点击了确认按钮", Toast.LENGTH_SHORT).show();
                                    mOffTime.cancel();
                                    mOffTextView = null;
                                    initVideoGroup(medialArray);
                                }
                            }).create();
                    alertDialog.show();
                    compareDialog = 1;

                    mOffGroupHandler = new Handler() {
                        public void handleMessage(Message msg) {

                            if (msg.what > 0) {
                                ////动态显示倒计时
                                mOffTextView.setText("    即将关闭："+msg.what);
                                //语音控制1为再来一次, 2为播放结束
                                switch (compare){
                                    case 1:
                                        alertDialog.dismiss();
                                        if (mTimerTask != null){
                                            mTimerTask.cancel();
                                            mTimerTask = null;
                                        }
                                        if (mOffTime != null){
                                            mOffTime.cancel();
                                            mOffTime.purge();
                                            mOffTime = null;
                                        }
                                        mOffTextView = null;
                                        initVideoGroup(medialArray);
                                        break;
                                    case 2:
                                        NavigationManager.getInstance().navigationByName("充电站");
                                        startActivity(new Intent(MedicalVideoInitActivity.this, LoginActivity.class));
                                        alertDialog.dismiss();
                                        mOffTime.cancel();
                                        finish();
                                    default:
                                        break;
                                }
                                compare = 0;
                            } else {
                                ////倒计时结束自动关闭
                                NavigationManager.getInstance().navigationByName("充电站");
                                alertDialog.dismiss();
                                mOffTime.cancel();
                                startActivity(new Intent(MedicalVideoInitActivity.this, LoginActivity.class));
                                finish();
                            }
                            super.handleMessage(msg);
                        }

                    };


                    mOffTime = new Timer(true);
                    mTimerTask = new TimerTask() {
                        int countTime = 10;
                        public void run() {
                            if (countTime > 0) {
                                countTime--;
                            }
                            Message msg = new Message();
                            msg.what = countTime;
                            mOffGroupHandler.sendMessage(msg);
                        }
                    };
                    mOffTime.schedule(mTimerTask, 1000, 1000);

                } else {
                    initVideo(medialArray[num[0]]);
                    num[0]++;
                }
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NerveManager.getInstance().uninit();  //初始化外设
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MyEvent.MainEvent event) {
        switch (event.action) {
            case MainPresenter.ACTION_UPDATE_VOL:
                int vol = (int) event.data;
                break;
            case MainPresenter.ACTION_SPEECH_VALUE:
                String value = (String) event.data;
                Log.i(TAG, "resultvaluemedical" + value);
                if ("再来一次".equals(value)){
                    compare = 1;
                }else if ("播放结束".equals(value)){
                    if (compareDialog == 0){
                        NavigationManager.getInstance().navigationByName("充电站");
                        startActivity(new Intent(MedicalVideoInitActivity.this, LoginActivity.class));
                        finish();
                    }else if (compareDialog == 1){
                        compare = 2;
                    }
                }
                break;
            case MainPresenter.ACTION_SPEECH_RESULT:
                String result = (String) event.data;
//                mMainView.setSpeechResult(result);
//                SpeechPlugin.getInstance().startSpeak(result);
                Log.i(TAG, "update: result = " + result);
                break;
        }
    }
}
