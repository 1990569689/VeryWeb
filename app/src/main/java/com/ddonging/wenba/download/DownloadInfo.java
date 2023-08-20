package com.ddonging.wenba.download;

/**
 * Created by 4ndroidev on 16/10/7.
 */

// one-to-one association with DownloadJob
public class DownloadInfo implements Comparable<DownloadInfo> {

    int state;
    long finishedLength;

    public long createTime;
    public long finishTime;
    public long contentLength;
    public long id;
    public String key;
    public String url;
    public String name;
    public String path;
    public String source;
    public String extras;

    DownloadInfo() {
        this.createTime = System.currentTimeMillis();
    }

    DownloadInfo(long id, String key, String url, String name, String path, String source, String extras) {
        this.id = id;
        this.key = key;
        this.url = url;
        this.name = name;
        this.path = path;
        this.source = source;
        this.extras = extras;
        this.createTime = System.currentTimeMillis();
    }

    public boolean isFinished() {
        return DownloadState.STATE_FINISHED == state;
    }

    @Override
    public int compareTo(DownloadInfo another) {
        long diff = finishTime - another.finishTime;
        return diff == 0 ? 0 : diff > 0 ? -1 : 1;
    }
}
