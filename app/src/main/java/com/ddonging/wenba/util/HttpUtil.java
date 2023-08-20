package com.ddonging.wenba.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;


public class HttpUtil {
    public static int result;


    public static String getFileNameFromUrl(String resulturl) {
        String urlString = null;
        String result = null;
        try {
            URL url = new URL(resulturl);
            urlString = url.getFile();
         result= URLDecoder.decode(urlString.substring(urlString.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0],"UTF-8");
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
       
        return result;
    }

    public static String getFileName(String url) {
                String fileName = null;
                HttpURLConnection connection = null;
                int code = 0;
                try {
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8 * 1000);
                    connection.setReadTimeout(8 * 1000);
                    connection.connect();
                    code = connection.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK)
                    {
                        fileName = connection.getHeaderField("Content-Disposition");
                        // 通过Content-Disposition获取文件名，这点跟服务器有关，需要灵活变通
                        if (fileName == null || fileName.length() < 1)
                        {
                            // 通过截取URL来获取文件名URL
                            URL downloadUrl = connection.getURL();
                            // 获得实际下载文件的URL
                            fileName = downloadUrl.getFile();
                            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                        } else {
                            fileName = URLDecoder.decode(fileName.substring(fileName.indexOf("filename=") + 9), "UTF-8");
                            // 有些文件名会被包含在""里面，所以要去掉，不然无法读取文件后缀
                            fileName = fileName.replaceAll("\"", "");

                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

        return fileName;
    }

    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {

            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段

            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

}
