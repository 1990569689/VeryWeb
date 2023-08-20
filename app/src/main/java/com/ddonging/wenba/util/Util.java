package com.ddonging.wenba.util;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

public class Util {
   /**
    * @param url
    * @return
    */
   public static boolean isUrl(String url) {
      return url.startsWith("http://") || url.startsWith("javascript://") || url.startsWith("history://") || url.startsWith("folder://") || url.startsWith("content://") || url.startsWith("https://") || url.startsWith("about://") || url.startsWith("file://");
   }

   public static void downloadByADM(Context context, String url, String mimeType) {
      if (hasApp(context, "com.dv.adm.pay")) {
         try {
            Intent startApp = new Intent(Intent.ACTION_VIEW);
            Intent intent = startApp.setComponent(new ComponentName("com.dv.adm.pay", "com.dv.adm.pay.AEditor"));
            startApp.setDataAndType(Uri.parse(url), mimeType);
            context.startActivity(startApp);
         } catch (Exception e) {
         }
      } else {
         ToastUtil.Toast(context, "没有安装ADM,取消下载");
      }
   }

   public static boolean hasApp(Context context, String packgename) {
      PackageInfo packageInfo;
      try {
         packageInfo = context.getPackageManager().getPackageInfo(packgename, 0);
      } catch (Exception e) {
         packageInfo = null;
         e.printStackTrace();
      }
      return packageInfo != null;
   }
   /*
   清除浏览器缓存
    */
   public static void clearWebViewCache(Context context) {
      File file = context.getApplicationContext().getCacheDir().getAbsoluteFile();
      deleteFile(file);
   }

   /*
   s删除文件
    */
   public static void deleteFile(File file) {
      if (file.exists()) {
         if (file.isFile()) {
            file.delete();
         } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
               deleteFile(files[i]);
            }
         }
         file.delete();
      } else {

      }
   }


   /**
    * 将图片转成byte数组
    *
    * @param bitmap 图片
    * @return 图片的字节数组
    */
   public static byte[] bitmap2Byte(Bitmap bitmap) {
      if (null == bitmap) throw new NullPointerException();
      // if (null == bitmap) return null;
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      //把bitmap100%高质量压缩 到 output对象里
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
      return outputStream.toByteArray();
   }

   /**
    * 将图片转成byte数组
    *
    * @param imageByte 图片
    * @return Base64 String
    */
   public static String byte2Base64(byte[] imageByte) {
      if(null == imageByte) return null;
      return Base64.encodeToString(imageByte, Base64.DEFAULT);
   }

   /**
    * Base64转Bitmap
    *
    * @param base64 base64数据流
    * @return Bitmap 图片
    */
   public static Bitmap base642Bitmap(String base64String) {
      if (null == base64String) throw new NullPointerException();
      byte[] decode = Base64.decode(base64String.split(",")[1], Base64.DEFAULT);
      Bitmap mBitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
      return mBitmap;
   }
   // 测试使用(上面Base64数据流格式部分粘贴过来的)实际以服务器或自己生成的为准
   String base64= "data:image/png;base64,iVBORw0KGgoAA";
   // 设置到view上
  //  imageView.setImageBitmap(base642Bitmap(base64));


   public static HashMap<String, HashMap<String, String>> importBookmarks(Context context, String content) {
      //    System.out.println(content);
      HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
      HashMap<String, String> book = new HashMap<String, String>();
      Document document = Jsoup.parse(String.valueOf(content));
      Element element = document.select("body > dl").first();
      String bookmarkpath = context.getExternalFilesDir("bookmark").getAbsolutePath();
      Bookmark.set(element, bookmarkpath, context);
      return null;
   }


   public static Bitmap drawableToBitamp(Drawable drawable)
   {
      //声明将要创建的bitmap
      Bitmap bitmap = null;
      //获取图片宽度
      int width = drawable.getIntrinsicWidth();
      //获取图片高度
      int height = drawable.getIntrinsicHeight();
      //图片位深，PixelFormat.OPAQUE代表没有透明度，RGB_565就是没有透明度的位深，否则就用ARGB_8888。详细见下面图片编码知识。
      Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
      //创建一个空的Bitmap
      bitmap = Bitmap.createBitmap(width,height,config);
      //在bitmap上创建一个画布
      Canvas canvas = new Canvas(bitmap);
      //设置画布的范围
      drawable.setBounds(0, 0, width, height);
      //将drawable绘制在canvas上
      drawable.draw(canvas);
      return bitmap;
   }


}
