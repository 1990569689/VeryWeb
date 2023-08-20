package com.ddonging.wenba.download;

/**
 * Created by 4ndroidev on 16/10/18.
 */
public interface DownloadJobListener {
    void onCreated(DownloadInfo info);
    void onStarted(DownloadInfo info);
    void onCompleted(boolean finished, DownloadInfo info);
}
