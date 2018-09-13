package com.reeman.basebigman;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.reeman.basebigman.manager.NavigationManager;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends AppCompatActivity {

    private final static String TAG = "VideoActivity";

    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.video_to_patient)
    ImageButton videoToPatient;

    private String medialName;
    private int number;
    private Handler mOffHandler;
    private Timer mOffTime;
    private TextView mOffTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);

        initView();
    }

    private void setListenter() {

        videoToPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VideoActivity.this, PatientCommonUse.class));
                finish();
            }
        });
    }

    private void initView() {
        ButterKnife.bind(this);
        setListenter();
        mOffTextView = new TextView(this);
        number = getIntent().getIntExtra("number", 1);
        medialName = getIntent().getStringExtra("MediaName");
        initVideo(number, medialName);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //播放完毕
                AlertDialog alertDialog = new AlertDialog.Builder(VideoActivity.this)
                        .setTitle(R.string.play_ended)
                        .setMessage(R.string.play_again_makesure)
                        .setView(mOffTextView)
                        .setNegativeButton(R.string.play_ended, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "点击了取消按钮", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(VideoActivity.this, PatientCommonUse.class));
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
                                initVideo(number,medialName);
                            }
                        }).create();
                alertDialog.show();
                mOffHandler = new Handler() {
                    public void handleMessage(Message msg) {

                        if (msg.what > 0) {
                            ////动态显示倒计时
                            mOffTextView.setText("    即将关闭："+msg.what);

                        } else {
                            ////倒计时结束自动关闭
                            startActivity(new Intent(VideoActivity.this, LoginActivity.class));
                            mOffTime.cancel();
                            finish();
                        }
                        super.handleMessage(msg);
                    }
                };


                mOffTime = new Timer(true);
                TimerTask tt = new TimerTask() {
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
                mOffTime.schedule(tt, 1000, 1000);
            }
        });
    }

    //初始化视频播放
    private void initVideo(int number, String medialName) {
        mOffTextView = new TextView(this);
        String path = null;
        switch (number){
            case 1:
                path = Environment.getExternalStorageDirectory().getPath() + "/video/入院宣教/" + medialName;
                break;
            case 2:
                path = Environment.getExternalStorageDirectory().getPath() + "/video/手术宣教/" + medialName;
                break;
            case 3:
                path = Environment.getExternalStorageDirectory().getPath() + "/video/疾病宣教/" + medialName;
                break;
                default:
                    break;
        }
        Log.i(TAG, path);
        Uri uri = Uri.parse(path);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                videoView.start();//开始播放视频
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

}
