package com.reeman.basebigman.presenter;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.reeman.basebigman.LoginActivity;
import com.reeman.basebigman.base.BasePresenter;
import com.reeman.basebigman.constant.MyEvent;
import com.reeman.basebigman.contract.MainContract;
import com.reeman.basebigman.manager.NerveManager;
import com.reeman.nerves.RobotActionProvider;
import com.speech.processor.SpeechPlugin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import robot.boocax.com.sdkmodule.APPSend;
import robot.boocax.com.sdkmodule.TCP_CONN;
import robot.boocax.com.sdkmodule.entity.entity_app.LoginEntity;
import robot.boocax.com.sdkmodule.utils.sdk_utils.SendUtil;

/**
 * Created by ye on 2017/11/8.
 */

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {
    private static final String TAG = MainPresenter.class.getSimpleName();
    public static final int ACTION_UPDATE_VOL = 0;
    public static final int ACTION_SPEECH_VALUE = 1;
    public static final int ACTION_SPEECH_RESULT = 2;

    //
    private double x = 2;
    private double y = -1.7;
    private double yaw = 0;


    private MainContract.View mMainView;

    public MainPresenter(MainContract.View mainView) {
        this.mMainView = mainView;
    }



    @Override
    public void onStart () {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop () {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MyEvent.MainEvent event) {
        Log.i(TAG, "====update===action:" + event.action);
        switch (event.action) {
            case ACTION_UPDATE_VOL:
                int vol = (int) event.data;
                mMainView.setVol(vol);
                break;
            case ACTION_SPEECH_VALUE:
                String value = (String) event.data;
                mMainView.setSpeechValue(value);
                Log.i(TAG, "resultvalueMainpresent" + value);
                break;
            case ACTION_SPEECH_RESULT:
                String result = (String) event.data;
                mMainView.setSpeechResult(result);
                SpeechPlugin.getInstance().startSpeak(result);
                Log.i(TAG, "update: result = " + result);
                break;
        }
    }


    @Override
    public void start () {
        NerveManager.stopState = RobotActionProvider.getInstance().getScramState();
    }

    public void reset() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("message_type","reset");
                jsonObject.addProperty("robot_mac_address",LoginEntity.robotMac);
                jsonObject.addProperty("x",x);
                jsonObject.addProperty("y",y);
                jsonObject.addProperty("yaw",yaw);
                boolean send = SendUtil.send(jsonObject.toString(), TCP_CONN.channel);
                Log.d(TAG, "run: "+ send);
                // todo post to main thread
                // send true  表示发送成功
                // send false 表示发送失败
                EventBus.getDefault().post(send);

            }
        }).start();
    }
}
