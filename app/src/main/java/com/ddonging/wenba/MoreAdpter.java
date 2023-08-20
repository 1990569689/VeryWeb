package com.ddonging.wenba;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MoreAdpter extends RecyclerView.Adapter<MoreAdpter.MyViewHolder> {
    private ArrayList<HashMap<String,Object>> list;  //列表数据
    private OnItemClickListener mOnItemClickListener,ItemClickListener;
    private OnItemLongClickListener   mOnItemLongClickListener;
    private  String type;

    public MoreAdpter() {
        // TODO Auto-generated constructor stub

    }
    public MoreAdpter(ArrayList<HashMap<String,Object>> list,String type) {
        // TODO Auto-generated constructor stub
        this.list = list;
        this.type = type;
    }

    /**
     *
     * 实例化 列表项布局中的控件，在此为一个简单的textview
     */
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView script_name;
        TextView user;
        public MyViewHolder(View view) {
            super(view);
            script_name = view.findViewById(R.id.script_name);
            user= view.findViewById(R.id.user);
        }
    }
    /**
     * 要显示的列表项数
     * @return 列表项总数
     */
    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return list.size();
    }
    /**
     * 用数据填充列表项上的 textview文本
     * @param holder：  当前列表项 布局的 控件实例
     * @param position: 列表项的索引
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.script_name.setText((String)list.get(position).get("title"));

        String name =(String)list.get(position).get("title");
        holder.script_name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mOnItemLongClickListener.onItemClick(view, position,name);
                return false;
            }
        });

        if(Objects.equals(type, "homepage"))
        {

        String home=(String)list.get(position).get("Homepage");

        if(Objects.equals(name, home))
        {
            holder.user.setText("●");
        }
        else
        {
            holder.user.setText("○");
        }
        }
        else if(Objects.equals(type, "useragent"))
        {
            String home=(String)list.get(position).get("UserAgent");

            if(Objects.equals(name, home))
            {
                holder.user.setText("●");
            }
            else
            {
                holder.user.setText("○");
            }
        }
        else if(Objects.equals(type, "engines"))
        {
            String home=(String)list.get(position).get("Engines");

            if(Objects.equals(name, home))
            {
                holder.user.setText("●");
            }
            else
            {
                holder.user.setText("○");
            }
        }
        //将列表数据list数组中的position位置的字符串 填充给 这个列表项上的textview
        //实现列表item单击事件，主要使用java interface来实现回调MainActivity中的onItemClick函数

        if(mOnItemClickListener!=null){

            holder.user.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position,name);
                }
            });

        }



    }
    /**
     * 为列表项实例化布局，将在onBindViewHolder里为布局控件赋值
     * @param viewGroup
     * @param arg1
     * @return
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
        MyViewHolder holder = new MyViewHolder(View.inflate(viewGroup.getContext(), R.layout.script_item, null));
        return holder;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position,String name);

    }
    public interface OnItemLongClickListener{
        void onItemClick(View view, int position,String name);

    }


    public void setOnLongClickListener(OnItemLongClickListener listener){
        this.mOnItemLongClickListener = listener;
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }



}