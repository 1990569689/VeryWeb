package com.ddonging.wenba;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyRecyclerView extends androidx.recyclerview.widget.RecyclerView{
    public MyRecyclerView(@NonNull Context context) {
        super(context);
    }

    public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        IndexActivity.xDown = (int) event.getRawX();
        IndexActivity.yDown = (int) event.getRawY();
        return super.onInterceptTouchEvent(event);
    }
}
