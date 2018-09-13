package com.reeman.basebigman;

import android.app.Activity;
import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.reeman.basebigman.manager.NerveManager;
import com.speech.processor.SpeechPlugin;

import java.util.List;

import robot.boocax.com.sdkmodule.utils.init_files.NaviContext;

/**
 * Created by ye on 2017/11/8.
 */

public class ReemanApp extends MultiDexApplication {
//    public static final String FACE_COMPANY_APPID="EEjrqcmMsDPercU4";
    public static final String FACE_COMPANY_APPID="MNtKoND7UQGQ1CNf";
    public static final String JM_APPID = "bkWAfUs6pzvZEPjH";
    private List<Activity> allActivity;
    private static Application myAplication;
    public static ReemanApp mInstance;

    @Override
    public void onCreate () {
        super.onCreate();
        mInstance = this;
        NaviContext.context = this;
//        SpeechPlugin.CreateSpeechUtility(this, "5ac1f5a7", FACE_COMPANY_APPID);
        SpeechPlugin.CreateSpeechUtility(this, "586b9487", JM_APPID);
        NerveManager.getInstance();
    }

    public static ReemanApp getInstance() {
        return mInstance;
    }

    public void addActivity(Activity activity){
        /**
         * 判断当前list是否存在此活动
         */
        if (!allActivity.contains(activity)){
            allActivity.add(activity);
        }
    }

    /**
     * 从集合中移除活动
     * 销毁当前活动
     * @param activity
     */
    public void removeActivity(Activity activity){
        if (allActivity.contains(activity)){
            allActivity.remove(activity);
            activity.finish();
        }
    }

    /**
     * 通过遍历销毁当前所有活动
     */
    public void removeAllActivity(){
        for (Activity activity:allActivity){
            activity.finish();
        }
    }
}
