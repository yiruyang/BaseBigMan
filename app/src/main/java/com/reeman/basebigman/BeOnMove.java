package com.reeman.basebigman;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.reeman.basebigman.basic.RecyclerViewAdapter;
import com.reeman.basebigman.manager.NavigationManager;
import com.reeman.basebigman.manager.NerveManager;
import com.reeman.basebigman.presenter.MainPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BeOnMove extends AppCompatActivity {

    @BindView(R.id.place)
    TextView placeView;
    @BindView(R.id.btn_back)
    Button btnBack;
    public static Context mContext;
    private SharedPreferences mSharedPreferences;
    private String[] mediaArray ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_be_on_move);
        NerveManager.getInstance().init();  //初始化外设
        initView();
    }

    public void initView() {
        ButterKnife.bind(this);
        mContext  = getApplicationContext();
        mSharedPreferences = getSharedPreferences(RecyclerViewAdapter.CURRENT_PLACE, 0);
        String placeName = mSharedPreferences.getString("placeName", null);
        Log.i("BeOnMove-placeName", placeName);
        String[] s = placeName.split("-");
        String place = s[0]+"-"+s[1];
        placeView.setText(place+"房间");
        mediaArray = this.getIntent().getExtras().getStringArray("videoArrat");
        NavigationManager.getInstance().navigationByName(placeName);
        NavigationManager.setListener(new NavigationManager.ReachListener() {
            @Override
            public void stopState() {
                btnBack.setVisibility(View.VISIBLE);
            }

            @Override
            public void reach() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("OnROSListener", "reach: ");
                        Intent intent = new Intent(BeOnMove.this, MedicalVideoInitActivity.class);
                        if (mediaArray != null){
                            Bundle bundle = new Bundle();
                            bundle.putStringArray("videoArraty", mediaArray);
                            Log.i("videoArraty", mediaArray[0]);
                            intent.putExtras(bundle);
                        }
                        intent.putExtra("videoURLType", getIntent().getIntExtra("type", 0));
                        Log.i("videoURLType", String.valueOf(getIntent().getIntExtra("type", 0)));
                        if (getIntent().getStringExtra("medialData") != null){
                            intent.putExtra("medialData", getIntent().getStringExtra("medialData"));
                        }
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationManager.getInstance().navigationByName("充电站");
                BeOnMove.this.finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
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
