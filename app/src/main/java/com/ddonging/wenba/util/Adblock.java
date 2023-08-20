package com.ddonging.wenba.util;

import java.util.ArrayList;
import java.util.List;

public class Adblock{
    static String one;
    static List<String> list=new ArrayList<>();
    public static  boolean Adblock(String url,List<String> list)
    {

        if(url.startsWith("http://"))
        {
            one=url.substring(url.lastIndexOf("http://"));
        }else
        {
            one=url.substring(url.lastIndexOf("https://"));
        }
        for(int i=0;i<list.size();i++)
        {
            //HTML元素隐藏
            if(list.get(i).startsWith("!"))
            {
                //title
                System.out.println(list.get(i).substring(list.get(i).indexOf("!Title:")+7));
            }
            else
            {
                if(list.get(i).startsWith("##"))
                {
                    System.out.println(list.get(i).substring(list.get(i).indexOf("##")+2));
                }
                //
                else if(list.get(i).startsWith("@@"))
                {
                    System.out.println(list.get(i).substring(list.get(i).indexOf("##")+2));
                }
                else if(list.get(i).startsWith("@@"))
                {
                    System.out.println(list.get(i).substring(list.get(i).indexOf("##")+2));
                }
                else if(list.get(i).endsWith("*")&&!list.get(i).endsWith(".*.")&&!list.get(i).contains("*=")||list.get(i).contains("*")||list.get(i).startsWith("*"))
                {
                    list.get(i).replaceAll("*", ".*");
                }
                else if(list.get(i).contains("$domain="))
                {
                    String[] str=list.get(i).substring(list.get(i).indexOf("$domain=")+8).split("\\|");
                    System.out.println(str[0]);
                }
                else if(!list.get(i).startsWith("##")&&list.get(i).contains("##"))
                {
                    //尾部
                    System.out.println(list.get(i).substring(list.get(i).indexOf("##")+2));
                    //头部
                    System.out.println(list.get(i).substring(0,list.get(i).lastIndexOf("##")));
                    if(list.get(i).substring(0,list.get(i).lastIndexOf("##")).contains(","))
                    {
                        String[] str =list.get(i).substring(0,list.get(i).lastIndexOf("##")).split("\\,");
                        System.out.println(str[0]);
                    }else
                    {
                        list.get(i).substring(0,list.get(i).lastIndexOf("##"));
                        //~exampl.com
                    }
                }
                else if(list.get(i).contains("^"))
                {
                    //尾部
                    list.get(i).replace("^", "");
                    list.get(i).replace("||", "");
                    System.out.println(list.get(i).substring(list.get(i).indexOf("##")+2));
                    //头部
                }
                else if(list.get(i).contains("||")||list.get(i).contains("|"))
                {
                    //尾部
                    list.get(i).replace("|", "");
                    list.get(i).replace("||", "");

                    System.out.println(list.get(i).substring(list.get(i).indexOf("##")+2));
                    //头部
                }
                else if(list.get(i).contains("#@#"))
                {
                    //尾部
                    list.get(i).replace("^", "");
                    list.get(i).replace("||", "");
                    System.out.println(list.get(i).substring(list.get(i).indexOf("##")+2));
                    //头部
                }

            }

        }
        return false;
    }
}
