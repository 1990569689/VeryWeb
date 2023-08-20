package com.ddonging.wenba.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * 尺寸转换工具类
 * Created by Brioal on 2016/7/21.
 */

public class SizeUtil {
    //dp转px
    public static int dp2px(Context ctx, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                ctx.getResources().getDisplayMetrics());
    }

    //px转dp
    public static int px2dp(Context ctx, int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px,
                ctx.getResources().getDisplayMetrics());
    }

    //sp转px
    public static int sp2px(Context ctx, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                ctx.getResources().getDisplayMetrics());
    }

    //px转sp
    public static int px2sp(Context ctx, int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px,
                ctx.getResources().getDisplayMetrics());
    }
}