package com.ddonging.wenba.download;

/**
 * Created by 4ndroidev on 16/10/6.
 */

// one-to-one association with DownloadTask
public interface DownloadListener {

    void onStateChanged(String key, int state);

    void onProgressChanged(String key, long finishedLength, long contentLength);

}
