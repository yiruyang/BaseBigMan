package com.reeman.basebigman;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.reeman.basebigman.basic.CommonRecyclerViewAdapter;
import com.reeman.basebigman.basic.IllnessRecyclerViewAdapter;
import com.reeman.basebigman.basic.NoScrollViewPager;
import com.reeman.basebigman.basic.OperationRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PatientCommonUse extends AppCompatActivity {

    @BindView(R.id.patient_tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.patient_viewPager)
    NoScrollViewPager viewPager;
    @BindView(R.id.patient_to_main)
    ImageButton patientToMain;

    private RecyclerView before_list, illness_list, operation_list;

    private CommonRecyclerViewAdapter recyclerViewAdapter;
    private IllnessRecyclerViewAdapter illRecyclerViewAdapter;
    private OperationRecyclerViewAdapter operationRecyclerViewAdapter;

    private final static String IllnessURL = Environment.getExternalStorageDirectory().getPath()+"/video/疾病宣教";
    private final static String BeforeURL = Environment.getExternalStorageDirectory().getPath()+"/video/入院宣教";
    private final static String OperationURL = Environment.getExternalStorageDirectory().getPath()+"/video/手术宣教";
    private List<View> viewList;
    private TabLayout.Tab one, two, three;
    private String[]  operationData, illnessData;
    private ArrayAdapter<String>  mOperationAdapter, mIllnessAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_common_use);

        initView();

    }

    public void getExternalStorage(){
        if (ContextCompat.checkSelfPermission(PatientCommonUse.this, Manifest.
                permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PatientCommonUse.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},  1);
        }
        getData();
    }

    public void getData(){
        File path = new File(BeforeURL);

        path = new File(OperationURL);
        if (path != null){
            File[] files=path.listFiles();
            if(files!=null) {
                operationData = new String[files.length];
                for (int i = 0 ; i<files.length; i++){
                    operationData[i] = String.valueOf(files[i].getName());
                }
            }
        }
        path = new File(IllnessURL);
        if (path != null){
            File[] files=path.listFiles();
            illnessData = new String[files.length];
            if(files!=null) {
                for (int i = 0 ; i<files.length; i++){
                    illnessData[i] = String.valueOf(files[i].getName());
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getData();
                }else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void initView() {
        ButterKnife.bind(this);
        getExternalStorage();
        viewList = new ArrayList<>();
        recyclerViewAdapter= new CommonRecyclerViewAdapter(this);
        illRecyclerViewAdapter = new IllnessRecyclerViewAdapter(this);
        operationRecyclerViewAdapter = new OperationRecyclerViewAdapter(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View beforeLayout = layoutInflater.inflate(R.layout.before_common_use_item, null);
        View operationLayout = layoutInflater.inflate(R.layout.operation_common_use_item,null);
        View illnessLayout = layoutInflater.inflate(R.layout.illness_common_use_item,null);
        before_list = beforeLayout.findViewById(R.id.before_movie_list);
        illness_list = illnessLayout.findViewById(R.id.illness_movie_list);
        operation_list = operationLayout.findViewById(R.id.operation_movie_list);

        viewList.add(before_list);
        viewList.add(illness_list);
        viewList.add(operation_list);

        patientToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientCommonUse.this, MainActivity.class));
                finish();
            }
        });

        PagerAdapter adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            //对显示的资源进行初始化
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View v=viewList.get(position);
                ViewGroup parent = (ViewGroup) v.getParent();
                if (parent != null) {
                    parent.removeAllViews();
                }
                container.addView(viewList.get(position));
                return viewList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewList.get(position));
            }
        };

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        one = tabLayout.getTabAt(0);
        two = tabLayout.getTabAt(1);
        three = tabLayout.getTabAt(2);

        one.setText(R.string.before_hospital);
        two.setText(R.string.illness_teacher);
        three.setText(R.string.operation_teacher);

        //设置分割线
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,
                R.drawable.divider)); //设置分割线的样式
        linearLayout.setDividerPadding(10); //设置分割线间隔
        //tab监听事件
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        mOperationAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, operationData);
        mIllnessAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, illnessData);
        before_list.setLayoutManager(new GridLayoutManager(this, 4));//宫格布局
        operation_list.setLayoutManager(new GridLayoutManager(this, 4));//宫格布局
        illness_list.setLayoutManager(new GridLayoutManager(this, 4));//宫格布局
        before_list.setAdapter(recyclerViewAdapter);
        operation_list.setAdapter(operationRecyclerViewAdapter);
        illness_list.setAdapter(illRecyclerViewAdapter);
        recyclerViewAdapter.setItemListener(new CommonRecyclerViewAdapter.ItemListener() {
            @Override
            public void onclickRoom(int position, String[] data) {
                String videoName = data[position];
                Intent intent = new Intent(PatientCommonUse.this, VideoActivity.class);
                intent.putExtra("number", 1);
                intent.putExtra("MediaName", videoName + ".mp4");
                startActivity(intent);
                finish();
            }
        });

        operationRecyclerViewAdapter.setItemListener(new OperationRecyclerViewAdapter.ItemListener() {
            @Override
            public void onclickRoom(int position, String[] data) {
                String videoName = data[position];
                Intent intent = new Intent(PatientCommonUse.this, VideoActivity.class);
                intent.putExtra("number", 2);
                intent.putExtra("MediaName", videoName + ".mp4");
                startActivity(intent);
                finish();
            }
        });
        illRecyclerViewAdapter.setItemListener(new IllnessRecyclerViewAdapter.ItemListener() {
            @Override
            public void onclickRoom(int position, String[] data) {
                String videoName = data[position];
                Intent intent = new Intent(PatientCommonUse.this, VideoActivity.class);
                intent.putExtra("number", 3);
                intent.putExtra("MediaName", videoName + ".mp4");
                startActivity(intent);
                finish();
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
