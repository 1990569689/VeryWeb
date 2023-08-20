package com.ddonging.wenba.download;


import java.util.List;

public interface DownloadNotifier {

    void notify(List<DownloadInfo> infos);

}
