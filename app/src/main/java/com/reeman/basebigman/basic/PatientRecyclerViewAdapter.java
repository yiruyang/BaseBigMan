package com.reeman.basebigman.basic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reeman.basebigman.R;


/**
 * Created by Administrator on 2018/4/23.
 */

public class PatientRecyclerViewAdapter extends RecyclerView.Adapter<PatientRecyclerViewAdapter.PatientRecyclerViewHolder> {

    private final LayoutInflater mLayouInflater;
    private final Context mContext;

    private String[] mVideoName;

    public PatientRecyclerViewAdapter(Context context) {
        mContext = context;
        mLayouInflater = LayoutInflater.from(context);
    }

    @Override
    public PatientRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PatientRecyclerViewHolder(mLayouInflater.inflate(R.layout.medical_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PatientRecyclerViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 0;
    }

    public static class PatientRecyclerViewHolder extends RecyclerView.ViewHolder{

        public PatientRecyclerViewHolder(View itemView) {
            super(itemView);
        }
    }

}
