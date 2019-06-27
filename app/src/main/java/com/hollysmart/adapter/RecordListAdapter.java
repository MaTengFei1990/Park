package com.hollysmart.adapter;
import android.content.Context;
import android.net.Uri;
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
import com.hollysmart.utils.Utils;
import com.hollysmart.value.Values;

import java.io.File;
import java.util.List;

import hollysmart.com.audiolib.AudioPlayManager;
import hollysmart.com.audiolib.IAudioPlayListener;

/**
 * Created by Lenovo on 2019/3/15.
 */

public class RecordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<SoundInfo> data;
    private List<SoundInfo> deletedata;


    private boolean longClickState = false;

    private MyOnItemClickListener myOnItemClickListener;

    public RecordListAdapter(Context context, List<SoundInfo> data,List<SoundInfo> deletedata){
        this.context = context;
        this.data = data;
        this.deletedata = deletedata;

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

//        holder1.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (myOnItemClickListener != null) {
//                    myOnItemClickListener.myClick(position);
//                    Log.e("这里是点击每一行item的响应事件",""+position);
//
//                }
//            }
//        });

//        holder1.tv_bianji.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                holder1.tv_bianji.setVisibility(View.GONE);
//                holder1.ll_bianji.setVisibility(View.VISIBLE);
//
//            }
//        });
        holder1.iv_delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SoundInfo soundInfo = data.get(position);

                deletedata.add(soundInfo);
                data.remove(position);
                File deletFile = new File(soundInfo.getFilePath());
                deletFile.delete();
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
        holder1.iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AudioPlayManager.getInstance().stopPlay();
                holder1.iv_play.setImageResource(R.mipmap.daolanplay);
                SoundInfo item = data.get(position);

                if (!Utils.isEmpty(item.getAudioUrl())) {
                    AudioPlayManager.getInstance().startPlayNetData(context, Values.SERVICE_URL_ADMIN_FORM+ item.getAudioUrl(), new IAudioPlayListener() {
                        @Override
                        public void onStart(Uri var1) {
                            holder1.iv_play.setImageResource(R.mipmap.daolanplaying);
                        }

                        @Override
                        public void onStop(Uri var1) {
                            holder1.iv_play.setImageResource(R.mipmap.daolanplay);
                        }

                        @Override
                        public void onComplete(Uri var1) {
                            holder1.iv_play.setImageResource(R.mipmap.daolanplay);
                        }
                    });


                } else if (!Utils.isEmpty(item.getFilePath())) {

                    Uri audioUri = Uri.fromFile(new File(item.getFilePath()));

                    Log.e("LQR", audioUri.toString());
                    AudioPlayManager.getInstance().startPlay(context, audioUri, new IAudioPlayListener() {
                        @Override
                        public void onStart(Uri var1) {
                            holder1.iv_play.setImageResource(R.mipmap.daolanplaying);

                        }

                        @Override
                        public void onStop(Uri var1) {
                            holder1.iv_play.setImageResource(R.mipmap.daolanplay);
                        }

                        @Override
                        public void onComplete(Uri var1) {
                            holder1.iv_play.setImageResource(R.mipmap.daolanplay);
                        }
                    });



                }



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
        //        private TextView tv_bianji;
        private ImageView iv_imageView;
        private ImageView iv_play;
        private ImageView iv_delet;



        RelativeLayout rl_all;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
//            tv_bianji = itemView.findViewById(R.id.tv_bianji);
            iv_imageView = itemView.findViewById(R.id.iv_imageView);


            rl_all = (RelativeLayout) itemView.findViewById(R.id.rl_all);
            iv_play = (ImageView) itemView.findViewById(R.id.iv_play);
            iv_delet = (ImageView) itemView.findViewById(R.id.iv_delet);

        }
    }



    public interface MyOnItemClickListener{
        void myClick(int positon);
    }


}
