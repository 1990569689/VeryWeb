package com.ddonging.wenba;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ddonging.wenba.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

public class MyHistoryAdapter extends RecyclerView.Adapter<MyHistoryAdapter.MyViewHolder> {
    ArrayList<HashMap<String, Object>> list= new ArrayList<HashMap<String, Object>>();/*在数组中存放数据*/
    private OnItemClickListener mOnItemClickListener,ItemClickListener;
    public MyHistoryAdapter() {
        // TODO Auto-generated constructor stub

    }
    public MyHistoryAdapter( ArrayList<HashMap<String, Object>> listItem ) {
        // TODO Auto-generated constructor stub
        this.list = listItem;

    }
    /**
     *
     * 实例化 列表项布局中的控件，在此为一个简单的textview
     */
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView list_title,list_url,list_date;
        ImageView webicon;
        public MyViewHolder(View view) {
            super(view);
            list_title = view.findViewById(R.id.list_title);
            list_url= view.findViewById(R.id.list_url);
            list_date= view.findViewById(R.id.list_date);
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
        holder.list_title.setText((String)list.get(position).get("title"));
        holder.list_url.setText((String)list.get(position).get("url"));
        holder.list_date.setText((String)list.get(position).get("date"));
        holder.webicon.setImageBitmap(Util.base642Bitmap((String)list.get(position).get("image")));
        //将列表数据list数组中的position位置的字符串 填充给 这个列表项上的textview
        //实现列表item单击事件，主要使用java interface来实现回调MainActivity中的onItemClick函数

        if(mOnItemClickListener!=null){

            holder.list_title.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
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
        MyViewHolder holder = new MyViewHolder(View.inflate(viewGroup.getContext(), R.layout.list_item, null));
        return holder;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);

    }

    public void setItemClickListener(OnItemClickListener listener){
        this.ItemClickListener = listener;
    }
}