package com.ddonging.wenba.download;

import android.content.Context;

import java.util.List;

/**
 * Created by 4ndroidev on 16/10/6.
 */
public class DownloadManager {

    public final static String INTENT_ACTION_DOWNLOAD = "com.ddonging.wenba";

    private static class DownloadManagerHolder {
        private static final DownloadManager instance = new DownloadManager();
    }

    private DownloadEngine engine;

    private DownloadManager() {
    }

    public void initialize(Context context, int masTask) {
        engine = new DownloadEngine(masTask);
        engine.initialize(context);
    }
    public   List<DownloadTask> get() {
        List<DownloadTask> list=engine.getAllTasks();
        return list;
    }
    public static DownloadManager getInstance() {
        return DownloadManagerHolder.instance;
    }

    public void destroy() {
        engine.destroy();
        engine = null;
    }

    public DownloadTask.Builder newTask(long id, String url, String name) {

        return new DownloadTask.Builder(engine).id(id).url(url).name(name);
    }

    public DownloadTask createTask(DownloadInfo info, DownloadListener listener) {
        return new DownloadTask(engine, info, listener);
    }

    public void addInterceptor(Interceptor interceptor) {

        engine.addInterceptor(interceptor);
    }

    public void addDownloadJobListener(DownloadJobListener downloadJobListener) {

        engine.addDownloadJobListener(downloadJobListener);
    }

    public void removeDownloadJobListener(DownloadJobListener downloadJobListener) {

        engine.removeDownloadJobListener(downloadJobListener);
    }

    public void setDownloadNotifier(DownloadNotifier downloadNotifier) {

        engine.setDownloadNotifier(downloadNotifier);
    }

    public List<DownloadTask> getAllTasks() {

        return engine.getAllTasks();
    }

    public List<DownloadInfo> getAllInfo() {
        return engine.getAllInfo();
    }
    public List<DownloadInfo> getDataInfo() {
        return engine.getDataInfo();
    }

    public void delete(DownloadInfo info) {

        engine.delete(info);
    }

    public boolean isActive() {

        return engine.isActive();
    }

    public interface Interceptor {
        void updateDownloadInfo(DownloadInfo info);
    }

}
