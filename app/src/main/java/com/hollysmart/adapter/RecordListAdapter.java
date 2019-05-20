package com.hollysmart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hollysmart.beans.SoundInfo;
import com.hollysmart.park.R;

import java.io.File;
import java.util.List;

/**
 * Created by Lenovo on 2019/3/15.
 */

public class RecordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<SoundInfo> data;


    private boolean longClickState = false;

    private MyOnItemClickListener myOnItemClickListener;

    public RecordListAdapter(Context context, List<SoundInfo> data){
        this.context = context;
        this.data = data;

    }



    public void setMyOnItemClickListener(MyOnItemClickListener myOnItemClickListener) {
        this.myOnItemClickListener = myOnItemClickListener;
    }

    @Override
    public RecordListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recordview,parent,false);
        return new RecordListAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final RecordListAdapter.ViewHolder holder1= (RecordListAdapter.ViewHolder)holder;
        holder1.tv_name.setText(data.get(position).getFilename());

        holder1.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myOnItemClickListener != null) {
                    myOnItemClickListener.myClick(position);
                    Log.e("这里是点击每一行item的响应事件",""+position);

                }
            }
        });

        holder1.tv_bianji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder1.tv_bianji.setVisibility(View.GONE);
                holder1.ll_bianji.setVisibility(View.VISIBLE);

            }
        });
        holder1.ll_shanchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SoundInfo soundInfo = data.get(position);

                File file = new File(soundInfo.getFilePath());

                if (file.exists()) {
                    file.delete();
                }
                data.remove(position);
                notifyDataSetChanged();


            }
        });



        holder1.rl_all.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder1.iv_imageView.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
                longClickState = true;
                return false;
            }
        });



        if (longClickState) {
            holder1.iv_imageView.setVisibility(View.VISIBLE);
        } else {
            holder1.iv_imageView.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_name;
        private TextView tv_bianji;
        private ImageView iv_imageView;


        LinearLayout ll_shanchu;
        LinearLayout ll_bianji;

        RelativeLayout rl_all;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_bianji = itemView.findViewById(R.id.tv_bianji);
            iv_imageView = itemView.findViewById(R.id.iv_imageView);


            rl_all = (RelativeLayout) itemView.findViewById(R.id.rl_all);
            ll_bianji = (LinearLayout) itemView.findViewById(R.id.ll_bianji);
            ll_shanchu = (LinearLayout) itemView.findViewById(R.id.ll_shanchu);

        }
    }



    public interface MyOnItemClickListener{
        void myClick(int positon);
    }


}
