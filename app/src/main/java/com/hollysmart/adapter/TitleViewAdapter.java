package com.hollysmart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hollysmart.beans.StateBean;
import com.hollysmart.listener.TextClickListener;
import com.hollysmart.park.R;

import java.util.List;

/**
 * Created by Lenovo on 2019/3/15.
 */

public class TitleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<StateBean> data;

    public TextClickListener textClickListener;


    public TitleViewAdapter(Context context, List<StateBean> data){
        this.context = context;
        this.data = data;

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_title,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ViewHolder holder1= (ViewHolder)holder;
        final StateBean stateBean = data.get(position);
        holder1.mTvTitle.setText(stateBean.getTitle());

        if (stateBean.isSelect()) {
            holder1.view_all.setVisibility(View.VISIBLE);

        } else {
            holder1.view_all.setVisibility(View.GONE);
        }



        holder1.ll_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (stateBean.isSelect()) {
                    return;
                } else {
                    stateBean.setSelect(true);
                    notifyDataSetChanged();

                }

                if (textClickListener != null) {

                    textClickListener.onClick(data.get(position).getState());
                }

            }
        });


    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mTvTitle;
        LinearLayout ll_all;
        View view_all;

        public ViewHolder(View view) {
            super(view);
            mTvTitle =  view.findViewById(R.id.tv_all);
            ll_all =  view.findViewById(R.id.ll_all);
            view_all =  view.findViewById(R.id.view_all);
        }
    }


    public TextClickListener getTextClickListener() {
        return textClickListener;
    }

    public void setTextClickListener(TextClickListener textClickListener) {
        this.textClickListener = textClickListener;
    }

}
