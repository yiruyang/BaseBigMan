package com.reeman.basebigman;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_left)
    RelativeLayout mainLeft;
    @BindView(R.id.main_right)
    RelativeLayout mainRight;
    @BindView(R.id.main_bottom)
    RelativeLayout mainBottm;
    @BindView(R.id.layout_main)
    RelativeLayout layoutMain;
    //    @BindView(R.id.main_above)
//    RelativeLayout mainAbove;
//    @BindView(R.id.main_password)
//    EditText mainPassword;
//    @BindView(R.id.main_masu)
//    TextView mainMasu;
//    @BindView(R.id.main_above_top)
//    TextView mainAboveTop;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    EditText editText;

    private View view;
    private String password;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏标题栏
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initDialog();
        initView();
    }

    private void initDialog() {

        editText = new EditText(this);
        builder = new AlertDialog.Builder(this)
                .setTitle("医护人员登录")
                .setView(editText)
                .setPositiveButton("登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        password = editText.getText().toString().trim();
                        if (TextUtils.isEmpty(password)) {
                            Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                        } else if (password.equals("123")) {
//                    mainAbove.setVisibility(View.GONE);
//                    mainBottm.setVisibility(View.VISIBLE);
//                            passwd.setText("");//跳转之前先清空，否则导航栏返回密码依然会在
                            startActivity(new Intent(MainActivity.this, MedicalActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        dialog = builder.create();
    }

    //    init dialog
    /*private void initDialog() {
        dialog = new Dialog(this,R.style.CustomDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_login_main, null);
        // set view listener
        final EditText passwd = view.findViewById(R.id.main_password);
        TextView main_masu = view.findViewById(R.id.main_masu);

        main_masu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = passwd.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                } else if (password.equals("123")) {
//                    mainAbove.setVisibility(View.GONE);
//                    mainBottm.setVisibility(View.VISIBLE);
                    passwd.setText("");//跳转之前先清空，否则导航栏返回密码依然会在
                    startActivity(new Intent(MainActivity.this, MedicalActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        WindowManager dialogWM = this.getWindowManager();
    }*/

    private void initView() {

        mainLeft.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(28);
//                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor();
            }
        });


        mainRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PatientCommonUse.class));

            }
        });

//        mainAbove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mainAbove.setVisibility(View.GONE);
//                mainBottm.setVisibility(View.VISIBLE);
//                //隐藏键盘
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                // 获取软键盘的显示状态
//                boolean isOpen=imm.isActive();
//                if (isOpen){
//                    imm.hideSoftInputFromWindow(mainBottm.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                }
//            }
//        });
    }

//    private void showCustomizeDialog(){
//        /* @setView 装入自定义View ==> R.layout.dialog_customize
//         * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
//         * dialog_customize.xml可自定义更复杂的View
//         */
//
//        AlertDialog.Builder customizeDialog =
//                new AlertDialog.Builder(MainActivity.this);
//        final View dialogView = LayoutInflater.
//                from(MainActivity.this).inflate(R.layout.dialog_customize,null);
//        customizeDialog.setTitle("进行以下操作");
//        customizeDialog.setView(dialogView);
//        customizeDialog.setPositiveButton("确认修改", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                EditText dialog_inputname = dialogView.findViewById(R.id.dialog_name);
//                patient_name.setText(dialog_inputname.getText());
//
//            }
//        });
//        customizeDialog.show();
//    }

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
