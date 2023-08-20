package com.ddonging.wenba;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<String> list;  //列表数据
    private List<Bitmap> icon;
    private OnItemClickListener mOnItemClickListener;
    private OnRemoveClickListener mOnRemoveClickListener;
    public MyAdapter() {
        // TODO Auto-generated constructor stub

    }
    public MyAdapter(List<String> list,List<Bitmap> icon) {
        // TODO Auto-generated constructor stub
        this.list = list;
        this.icon=icon;
    }
    /**
     *
     * 实例化 列表项布局中的控件，在此为一个简单的textview
     */
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView list_item;
        ImageView add,webicon;
        public MyViewHolder(View view) {
            super(view);
            list_item = view.findViewById(R.id.list_item);
            add= view.findViewById(R.id.dialog_add);
            webicon= view.findViewById(R.id.webicon);
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
        holder.list_item.setText(list.get(position));
        if(icon.get(position)==null)
        {
            holder.webicon.setImageResource(R.drawable.ic_webicon);
        }else
        {
            holder.webicon.setImageBitmap(icon.get(position));
        }

        String text = holder.list_item.getText().toString();
        //将列表数据list数组中的position位置的字符串 填充给 这个列表项上的textview
        //实现列表item单击事件，主要使用java interface来实现回调MainActivity中的onItemClick函数

        if(mOnItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position,text);
                }
            });

        }
        if(mOnRemoveClickListener!=null){
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mOnRemoveClickListener.onItemClick(v,position);

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
        MyViewHolder holder = new MyViewHolder(View.inflate(viewGroup.getContext(), R.layout.menu, null));
        return holder;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position,String text);
    }
    public interface OnRemoveClickListener{
        void onItemClick(View view, int position);
    }
    public  void addData(int position,String name){
        list.add(position,name);
        //提示刷新--会影响效率
       // notifyDataSetChanged(po);
        notifyItemInserted(position);
    }
    public void setData(int num,String data){
        list.set(num,data);

    }
    public void removeData(int position){
        list.remove(position);
        notifyItemRemoved(position);
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }
    public void setOnRemoveClickListener(OnRemoveClickListener listener){
        this.mOnRemoveClickListener = listener;
    }
}