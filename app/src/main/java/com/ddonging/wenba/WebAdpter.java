package com.ddonging.wenba;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.ddonging.wenba.run.WebViewGm;

import java.util.List;

public  class WebAdpter extends PagerAdapter {
    public List<WebViewGm> mListView;
    private final boolean canSwipe = true;
    private int mChildCount = 0;
    public WebAdpter(List<WebViewGm> listView){
        this.mListView = listView;
    }

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }
    @Override
    public int getItemPosition(Object object)   {
        if ( mChildCount > 0) {
            mChildCount --;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int  postion, @NonNull Object object) {
        container.removeView(IndexActivity.itemlistView[IndexActivity.countlist.get(IndexActivity.count)].get(postion));

    }

/*
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(ListViews.get(position));

    }

 */

    //获得ViewPager中有多少个View
    @Override
    public int getCount(){
        return mListView.size();
    }
    /*
    1. 将给定位置的view添加到ViewGroup中，创建并显示出来
    2. 返回一个代表新增页面的Object/key，通常都是直接返回View本身
    */
    @Override
    public Object instantiateItem(ViewGroup container, int position){
        ViewGroup viewGroup = (ViewGroup) mListView.get(position).getParent();
        if (viewGroup != null) {
            viewGroup.removeView( mListView.get(position));
        }
        container.addView(mListView.get(position), 0);
        return mListView.get(position);
    }
    //判断instantiateItem(ViewGroup, int)函数返回回来的key，与一个页面视图是否是对应的
    @Override
    public boolean isViewFromObject(View view, Object object){
        return view == object;
    }





}
