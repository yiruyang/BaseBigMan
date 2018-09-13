package com.reeman.basebigman.basic;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.reeman.basebigman.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    public static final String PREFS_NAME = "TUIMAN-PATIENT";
    public static final String CURRENT_PLACE = "CURRENT-PLACE";
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private String[] mRoomId;
    private String[] mPatientName;
    private ItemListener mListener;
    private SharedPreferences mSettings;
    private SharedPreferences mPlace;
    private boolean isCanUsed = false;

    public interface ItemListener{
        void onclickRoom(boolean isCanUsed);
    }

    public RecyclerViewAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mSettings = mContext.getSharedPreferences(PREFS_NAME, 0);
        mPlace = mContext.getSharedPreferences(CURRENT_PLACE, 0);
        mRoomId = getRoomId(54);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(mLayoutInflater.inflate(R.layout.medical_item, parent, false));
    }

    public void setItemListener(ItemListener listener){
        mListener = listener;
    }


    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        holder.roomId.setText(mRoomId[position]);
        if (mListener != null){
            holder.roomId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPatientName[position] != null){
                        isCanUsed = true;
                    }else {
                        isCanUsed = false;
                    }
                    mListener.onclickRoom(isCanUsed);
                    if (mRoomId[position] != null && mPatientName[position] != null ){
                        setPlacePreferences(mRoomId[position]);
                    }
                }
            });
        }
        /**
         * 防止滑动之后数据错乱
         * 第一步移除监听
         */
        if (holder.patientName.getTag() instanceof TextWatcher) {
            holder.patientName.removeTextChangedListener((TextWatcher) holder.patientName.getTag());
        }
        /**
         * 第二步设置值
         */
        holder.patientName.setText(mPatientName[position]);
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String data = holder.patientName.getText().toString();
                mPatientName[position] = data;
                setPreferences(mRoomId[position], data);
                Log.d("afterTextChanged", data);
            }
        };
        /**
         * 第三步添加监听
         */
        holder.patientName.addTextChangedListener(watcher);
        holder.patientName.setTag(watcher);
    }

    @Override
    public int getItemCount() {
        return mRoomId.length;
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.room_id)
        TextView roomId;
        @BindView(R.id.patient_name)
        EditText patientName;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public String[] getRoomId(int roomNumber){
        String[] sa = new String[roomNumber];
        mPatientName = new String[roomNumber];
        int x = 0;
        String y = null;
        for (int i = 0; i<roomNumber; i++){
            if (i <9){
                y = "0"+(i+1);
            }else {
                y = String.valueOf(i+1);
            }
            sa[i] = "5-"+y;
        }
        for (int i = 0; i<sa.length; i++){
            mPatientName[i] = mSettings.getString(sa[i], null);
        }
        return sa;
    }

    private void setPreferences(String roomId, String name){
        mSettings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(roomId, name);
        editor.commit();
    }

    private void setPlacePreferences(String placeName){
        mPlace = mContext.getSharedPreferences(CURRENT_PLACE, 0);
        SharedPreferences.Editor editor = mPlace.edit();
        editor.putString("placeName", placeName);
        editor.commit();
    }
}
