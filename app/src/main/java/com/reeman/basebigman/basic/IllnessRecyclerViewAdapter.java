package com.reeman.basebigman.basic;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reeman.basebigman.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/17.
 */

public class IllnessRecyclerViewAdapter extends RecyclerView.Adapter<IllnessRecyclerViewAdapter.RecyclerViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private String[] medialData;
    private ItemListener mListener;
    private final static String IllNESSURL = Environment.getExternalStorageDirectory().getPath()+"/video/疾病宣教";

    public interface ItemListener{
        void onclickRoom(int position, String[] data);
    }

    public IllnessRecyclerViewAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        getData();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(mLayoutInflater.inflate(R.layout.common_illness_item, parent, false));
    }

    public void setItemListener(ItemListener listener){
        mListener = listener;
    }


    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        holder.videoName.setText(medialData[position]);
        if (mListener != null){
            holder.videoName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onclickRoom(position, medialData);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return medialData.length;
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.illness_video_name)
        TextView videoName;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void getData(){
        File path = new File(IllNESSURL);
        if (path != null){
            File[] files=path.listFiles();
            if(files!=null) {
                medialData = new String[files.length];
                for (int i = 0 ; i<files.length; i++){
                    medialData[i] = String.valueOf(files[i].getName()).split("\\.")[0];
                }
            }
        }
    }
}
