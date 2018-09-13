package com.reeman.basebigman.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.reeman.basebigman.MainActivity;
import com.reeman.basebigman.R;
import com.reeman.basebigman.base.BaseFragment;
import com.reeman.basebigman.contract.MainContract;
import com.reeman.basebigman.manager.NavigationManager;
import com.reeman.basebigman.presenter.MainPresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ye on 2017/11/8.
 */

public class MainFragment extends BaseFragment<MainContract.View, MainPresenter> implements MainContract.View {

    @BindView(R.id.fm_iv_vol)
    ImageView mIvVol;
    Unbinder unbinder;
    @BindView(R.id.fm_tv_response_result)
    TextView mSpeechResult;
    @BindView(R.id.fm_tv_speech_value)
    TextView mTvSpeechValue;
    @BindView(R.id.login_to_main)
    ImageButton loginToMain;
    @BindView(R.id.go_charge)
    ImageButton goCharge;
    @BindView(R.id.reset)
    Button reset;

//    ObjectAnimator animation;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        // any operation should below after view has been bind;
        unbinder = ButterKnife.bind(this, view);
//        setAnima();
        loginToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        goCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationManager.getInstance().navigationByName("充电站");
            }
        });

        // send reset command to SERVER
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start Anim
//                animation.start();
                mPresenter.reset();
            }
        });
        return view;
    }


    /**
     *   开启动画
     */
    private void setAnima() {
//        animation = ObjectAnimator.ofFloat(reset, "rotation", 0, 360);
//        animation.setDuration(500);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();

    }

    private void initData() {
        mPresenter.start();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getSendResponce(boolean send) {
        if (send) {
            Toast.makeText(getActivity(), "正在重定位，请稍后", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "正在重定位，请稍后", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setVol(int vol) {
//        Log.e("NerveManager", "====:" + (mIvVol == null) + " / " + vol);
        int v = vol / 3;
        switch (v) {
            case 1:
                mIvVol.setImageResource(R.drawable.rec_vol1);
                break;
            case 2:
                mIvVol.setImageResource(R.drawable.rec_vol2);
                break;
            case 3:
                mIvVol.setImageResource(R.drawable.rec_vol3);
                break;
            case 4:
                mIvVol.setImageResource(R.drawable.rec_vol4);
                break;
            case 5:
                mIvVol.setImageResource(R.drawable.rec_vol5);
                break;
            case 6:
                mIvVol.setImageResource(R.drawable.rec_vol6);
                break;
            default:
                mIvVol.setImageResource(R.drawable.rec_vol1);
                break;
        }
    }

    @Override
    public void setSpeechResult(String text) {
        mSpeechResult.setText(text);
    }

    @Override
    public void setSpeechValue(String text) {
        mTvSpeechValue.setText(text);
    }
}
