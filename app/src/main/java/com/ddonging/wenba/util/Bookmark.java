package com.ddonging.wenba.util;

import android.content.Context;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Bookmark {
        public static void set(Element element,String path,Context context) {
            Elements es=element.children();
            for (int i=0;i<es.size();i++) {
                Element menuName =es.get(i).select("h3").first();
                Element menuUrl = es.get(i).select("dl").first();

                if(menuName!=null||menuUrl!=null)
                {
                     String dirName= menuName.text();
                     String  dirpath=path+"/"+dirName;
                   //  const dirPath=bookmark.getDirPathByName(dirName,parentDirPath);
                   //  const url=bookmark.getDirUrlByPath(dirPath);

                    String  bookmarkpath=context.getExternalFilesDir("bookmark").getAbsolutePath();
                    File Folder = new File(path,dirName);
                    if (!Folder.exists()) {
                        Folder.mkdir();
                    }
                    set(menuUrl,dirpath,context);

                }
                else
                {
                    Element e = es.get(i).select("a").first();
                    if(e==null) continue;
                    String Name = e.getElementsByAttribute("href").text();//菜单名称
                    String Url = e.attr("href");//菜单路径
                    String  bookmarkpath=context.getExternalFilesDir("bookmark").getAbsolutePath();
                    File Folder = new File(path,Name);
                    if (!Folder.exists()) {
                        Folder.mkdir();
                    }
                    createFile(path+"/"+Name,Url);
                }
                //importbook(element);
            }

        }

    public static void createFile(String path, String content)
    {

        File mFile = new File(path);
        //判断文件是否存在，存在就删除
        if (mFile.exists()) {
            mFile.delete();
        }
        try {
            //创建文件
            mFile.createNewFile();
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(path, true),"gbk");
            osw.write(content);
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}