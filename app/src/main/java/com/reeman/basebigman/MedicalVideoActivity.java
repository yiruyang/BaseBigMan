package com.reeman.basebigman;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.reeman.nerves.RobotActionProvider;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MedicalVideoActivity extends AppCompatActivity {

    @BindView(R.id.left_listView)
    ListView leftListView;
    @BindView(R.id.right_listView)
    ListView rightListView;
    @BindView(R.id.video_type)
    TextView videoType;
    @BindView(R.id.medial_listView)
    ListView medialListView;
    @BindView(R.id.multiple_choice)
    Button multipleChoice;
    @BindView(R.id.video_play)
    Button videoPlay;
    @BindView(R.id.right_listViewRelative)
    RelativeLayout right_listViewRelative;
    @BindView(R.id.medicalVideo_to_medical)
    ImageButton medicalVideoToMedical;

    private String[] leftData, rightData, medialData, videoName;
    private ArrayAdapter<String> mLeftAdapter, mRightAdapter;
    private ListViewAdapter mMedialAdapter;
    //组成文件目录路径s
    private String one, two, three;
    private String mUrl;
    private String mSingleUrl = Environment.getExternalStorageDirectory().getPath()+"/videoMedical/";
    private String mmUrl = Environment.getExternalStorageDirectory().getPath()+"/videoMedical/";
    private ModeCallback callback;
    private List<String> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_medical_video);
        initView();
    }

    //初始化视图
    private void initView() {
        ButterKnife.bind(this);
        //左列数据
        leftData = getResources().getStringArray(R.array.leftListView);

        medicalVideoToMedical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MedicalVideoActivity.this, MedicalActivity.class));
                finish();
            }
        });
        mLeftAdapter = new ArrayAdapter<String>
                (this, R.layout.array_adapter, leftData);
        leftListView.setAdapter(mLeftAdapter);
        leftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                right_listViewRelative.setVisibility(View.VISIBLE);
                rightListView.setVisibility(View.VISIBLE);
                switch (position){
                    case 0:
                        rightData = getResources().getStringArray(R.array.before);
                        getRightListView(rightData, position);
                        break;
                    case 1:
                        rightData = getResources().getStringArray(R.array.illness);
                        getRightListView(rightData, position);
                        break;
                    case 2:
                        rightData = getResources().getStringArray(R.array.operation);
                        getRightListView(rightData, position);
                        break;
                    default:
                        break;
                }
            }
        });
        multipleChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medialListView.setItemChecked(0, true);
                medialListView.clearChoices();
                callback.updateSelectedCount();
            }
        });

        rightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                two = rightData[position] + "/";
                mUrl =mmUrl + one + two ;
                operateRightListView(position);
            }
        });
        //设置多选模式
        medialListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        callback = new ModeCallback();
        medialListView.setMultiChoiceModeListener(callback);

        medialListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog alertDialog = new AlertDialog.Builder(MedicalVideoActivity.this)
                        .setTitle(R.string.medical_video_title)
                        .setMessage(R.string.medical_video_message)
                        .setCancelable(false)
                        .setNegativeButton(R.string.medical_video_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(R.string.medical_video_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (0 == RobotActionProvider.getInstance().getScramState()){
                                    Toast.makeText(getApplicationContext(), "请打开急停按钮", Toast.LENGTH_SHORT).show();
                                }else {
                                    dialog.cancel();
                                    Intent intent = new Intent(MedicalVideoActivity.this, BeOnMove.class);
                                    three = medialData[position];
                                    mUrl = mSingleUrl + one + two + three;
                                    intent.putExtra("type", 1);
                                    intent.putExtra("medialData", mUrl);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }).create();
                alertDialog.show();
            }
        });

        videoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(MedicalVideoActivity.this)
                        .setTitle(R.string.medical_video_title)
                        .setMessage(R.string.medical_video_message)
                        .setCancelable(false)
                        .setNegativeButton(R.string.medical_video_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(R.string.medical_video_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mList.size() > 0){
                                    if (0 == RobotActionProvider.getInstance().getScramState()){
                                        Toast.makeText(getApplicationContext(), "请打开急停按钮", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Bundle bundle = new Bundle();
                                        videoName = new String[medialListView.getCheckedItemCount()];
                                        List newList = new ArrayList(new HashSet(mList));
                                        Log.i("newList", String.valueOf(newList.size()));
                                        for(int i = 0; i<newList.size(); i++){
                                            videoName[i] = mmUrl + one + two + newList.get(i);
                                        }
                                        bundle.putStringArray("videoArrat", videoName);
                                        Intent intent = new Intent(MedicalVideoActivity.this, BeOnMove.class);
                                        intent.putExtra("type", 2);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        finish();
                                    }
                                }else {
                                    Toast.makeText(getApplicationContext(), "请选择视频!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).create();
                alertDialog.show();
            }
        });
    }

    public void getRightListView(String[] data, int position){
        mRightAdapter = new ArrayAdapter<String>
                (this, R.layout.right_array_adapter, data);
        rightListView.setAdapter(mRightAdapter);
        if (rightListView.getVisibility() == View.GONE){
            rightListView.setVisibility(View.VISIBLE);
        }
        one = leftData[position] + "/";
    }

    public void operateRightListView(int position){
        String catalog = rightData[position];
        videoType.setText(catalog);
        getExternalStorage();
        mMedialAdapter = new ListViewAdapter();
        medialListView.setAdapter(mMedialAdapter);
        rightListView.setVisibility(View.GONE);
        right_listViewRelative.setVisibility(View.GONE);
        multipleChoice.setVisibility(View.VISIBLE);
    }

    public void getExternalStorage(){
        if (ContextCompat.checkSelfPermission(MedicalVideoActivity.this, Manifest.
                permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MedicalVideoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},  1);
        }
        getData();
    }

    public void getData(){
        File path = new File(mUrl);
        Log.i("fileName", mUrl);
        FileFilter ff = new FileFilter() {
            public boolean accept(File pathname) {
                // TODO Auto-generated method stub
                return !pathname.isHidden();//过滤隐藏文件
            }
        };
        File[] files=path.listFiles(ff);
        if(files!=null) {
            medialData = null;
            medialData = new String[files.length];
            for (int i = 0 ; i<files.length; i++){
                medialData[i] = String.valueOf(files[i].getName());
                Log.i("fileName", medialData[i]);
            }
        }
        mUrl = null;
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

//    /**
//     * 按两次退回键退出应用
//     */
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
//            long secondTime = System.currentTimeMillis();
//            if (secondTime - firstTime > 1500) {
//                show_Toast("再按一次退出程序");
//                firstTime = secondTime;
//                return true;
//            } else {
//                removeAllActivity();
//            }
//        }
//
//        return super.onKeyUp(keyCode, event);
//    }



    class ModeCallback implements AbsListView.MultiChoiceModeListener {

        View actionBarView;
        TextView selectedNum;
        int selectedCount;
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            return true;
        }

        //退出多选模式时调用
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub
            medialListView.clearChoices();
            videoPlay.setVisibility(View.GONE);
        }

        //进入多选模式调用，初始化ActionBar的菜单和布局
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            getMenuInflater().inflate(R.menu.multiple_mode_menu, menu);
            if(actionBarView == null) {
                actionBarView = LayoutInflater.from(MedicalVideoActivity.this).inflate(R.layout.actionbar_view, null);
                selectedNum = actionBarView.findViewById(R.id.selected_num);
            }
            videoPlay.setVisibility(View.VISIBLE);
            mode.setCustomView(actionBarView);
            return true;
        }

        //ActionBar上的菜单项被点击时调用
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // TODO Auto-generated method stub
            switch(item.getItemId()) {
                case R.id.select_all:
                    for(int i = 0; i < mMedialAdapter.getCount(); i++) {
                        medialListView.setItemChecked(i, true);
                    }
                    updateSelectedCount();
                    mMedialAdapter.notifyDataSetChanged();
                    break;
                case R.id.unselect_all:
                    medialListView.clearChoices();
                    updateSelectedCount();
                    mMedialAdapter.notifyDataSetChanged();
                    break;
            }
            return true;
        }

        //列表项的选中状态被改变时调用
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position,
                                              long id, boolean checked) {
            // TODO Auto-generated method stub
            updateSelectedCount();
            mode.invalidate();
            mMedialAdapter.notifyDataSetChanged();
            mList = new ArrayList(new HashSet(mList));
            if (!checked){
                mList.remove(medialData[position]);
            }else if (checked){
                mList.add(medialData[position]);
            }
        }

        public void updateSelectedCount() {
            selectedCount = medialListView.getCheckedItemCount();
            selectedNum.setText(selectedCount + "");

        }
    }

    class ListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return medialData.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return medialData[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(MedicalVideoActivity.this, R.layout.medial_item, null);
                viewHolder.text = (TextView)convertView.findViewById(R.id.medial_item_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.text.setText(medialData[position]);
            if(medialListView.isItemChecked(position)) {
                mList.add(medialData[position]);
                convertView.setBackgroundColor(Color.RED);
            } else{
                mList = new ArrayList(new HashSet(mList));
                mList.remove(medialData[position]);
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            return convertView;
        }

        class ViewHolder {
            TextView text;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MedicalVideoActivity.this, MedicalActivity.class));
        finish();
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
