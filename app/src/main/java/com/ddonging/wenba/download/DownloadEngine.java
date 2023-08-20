package com.ddonging.wenba.download;

import static com.ddonging.wenba.download.DownloadState.STATE_PAUSED;
import static com.ddonging.wenba.download.DownloadState.STATE_RUNNING;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by 4ndroidev on 16/10/6.
 */
class DownloadEngine {

    private static final String TAG = "DownloadEngine";
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int KEEP_ALIVE = 10;


    /**
     * observes job lifecycle: onJobCreated, onJobStarted, onJobCompleted
     */
    private final List<DownloadJobListener> downloadJobListeners;


    /**
     * record all jobs those are not completed
     */
    private final Map<String, DownloadJob> jobs;

    /**
     * record all download info
     */
    private final Map<String, DownloadInfo> infos;

    /**
     * record all active info in order for notification
     */
    private final List<DownloadInfo> activeInfos;

    /**
     * show download notifications
     */
    private DownloadNotifier notifier;

    /**
     * update notification
     */
    private final Runnable updateNotificationRunnable = new Runnable() {
        @Override
        public void run() {
            notifier.notify(activeInfos);
        }
    };

    /**
     * for some server, the url of resource if temporary
     * maybe need setting interceptor to update the url
     */
    List<DownloadManager.Interceptor> interceptors;

    /**
     * download ThreadPoolExecutor
     */
    ThreadPoolExecutor executor;

    /**
     * provider for inserting, deleting, querying or updating the download info with the database
     */
    DownloadProvider provider;
    Handler handler;

    DownloadEngine(int maxTask) {
        jobs = new HashMap<>();
        infos = new HashMap<>();
        activeInfos = new ArrayList<>();
        interceptors = new ArrayList<>();
        downloadJobListeners = new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());
        if (maxTask > CORE_POOL_SIZE) maxTask = CORE_POOL_SIZE;
        executor = new ThreadPoolExecutor(maxTask, maxTask, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        executor.allowCoreThreadTimeOut(true);
    }

    /**
     * load download info from the database
     */
    void initialize(Context context) {
        provider = new DownloadProvider(context.getApplicationContext());
        executor.submit(new Runnable() {
            @Override
            public void run() {
                List<DownloadInfo> list = provider.query();
                for (DownloadInfo info : list) {
                    if (STATE_RUNNING == info.state) // App had been force stopped
                        info.state = STATE_PAUSED;
                    infos.put(info.key, info);
                    if (info.isFinished()) continue;
                    jobs.put(info.key, new DownloadJob(DownloadEngine.this, info));
                }
            }
        });
    }

    /**
     * clear and clear
     */
    void destroy() {
        executor.shutdown();
        interceptors.clear();
        downloadJobListeners.clear();
    }

    /**
     * @return all tasks those are not completed
     */
    List<DownloadTask> getAllTasks() {
        List<DownloadTask> tasks = new ArrayList<>();
        for (DownloadJob job : jobs.values()) {
            tasks.add(new DownloadTask(this, job.info, null));
        }
        Collections.sort(tasks);
        return tasks;
    }

    /**
     * @return all download info in order
     */
    List<DownloadInfo> getAllInfo() {
        List<DownloadInfo> result = new ArrayList<>(infos.values());
        Collections.sort(result);
        return result;
    }
    List<DownloadInfo> getDataInfo() {
        List<DownloadInfo> result = provider.query();
        return result;
    }

    boolean isActive() {
        return activeInfos.size() > 0;
    }

    /**
     * @param interceptor which implements method updateDownloadInfo(DownloadInfo downloadInfo)
     */
    void addInterceptor(DownloadManager.Interceptor interceptor) {
        if (interceptor == null || interceptors.contains(interceptor)) return;
        interceptors.add(interceptor);
    }

    /**
     * add downloadJobListener to observe the job lifecycle
     *
     * @param downloadJobListener which implements onJobCreated, onJobStarted, onJobCompleted
     */
    void addDownloadJobListener(DownloadJobListener downloadJobListener) {
        if (downloadJobListener == null || downloadJobListeners.contains(downloadJobListener))
            return;
        downloadJobListeners.add(downloadJobListener);
    }

    /**
     * delete downloadJobListener that observing the job lifecycle
     *
     * @param downloadJobListener which implements onJobCreated, onJobStarted, onJobCompleted
     */
    void removeDownloadJobListener(DownloadJobListener downloadJobListener) {
        if (downloadJobListener == null || !downloadJobListeners.contains(downloadJobListener))
            return;
        downloadJobListeners.remove(downloadJobListener);
    }

    /**
     * set download notifier
     */
    void setDownloadNotifier(DownloadNotifier notifier) {
        this.notifier = notifier;
        if (notifier != null) {
            notifier.notify(activeInfos);
        }
    }

    /**
     * prepare for the task, while creating a task, should callback the download info to the listener
     */
    void prepare(DownloadTask task) {
        String key = task.key;
        if (!infos.containsKey(key)) {  // do not contain this info, means that it will create a download job
            if (task.listener == null) return;
            task.listener.onStateChanged(key, DownloadState.STATE_PREPARED);
            return;
        }
        DownloadInfo info = infos.get(key);
        task.size = info.contentLength;
        task.createTime = info.createTime;
        if (!jobs.containsKey(key)) {  // uncompleted jobs do not contain this job, means the job had completed
            if (task.listener == null) return;
            task.listener.onStateChanged(key, info.state); // info.state == DownloadState.STATE_FINISHED
        } else {
            jobs.get(key).addListener(task.listener);
        }
    }

    /**
     * if downloadJobs contains the relative job, and the job is not running, enqueue it
     * otherwise create the job and enqueue it
     */
    void enqueue(DownloadTask task) {
        String key = task.key;
        if (jobs.containsKey(key)) {                   // has existed uncompleted job
            DownloadJob job = jobs.get(key);
            if (job.isRunning()) return;
            job.enqueue();
            activeInfos.add(job.info);
        } else {
            if (infos.containsKey(key)) return;         // means the job had completed
            DownloadInfo info = task.generateInfo();
            DownloadJob job = new DownloadJob(this, info);
            infos.put(key, info);
            jobs.put(key, job);
            onJobCreated(info);
            job.addListener(task.listener);
            job.enqueue();
            activeInfos.add(info);
        }
        updateNotification();
    }

    /**
     * delete the downloadJob and delete the relative info
     */
    void delete(DownloadTask task) {
        String key = task.key;
        if (!jobs.containsKey(key)) return;
        DownloadInfo info = infos.get(key);
        DownloadJob job = jobs.remove(key);
        job.delete();
        delete(info);
        if (!activeInfos.contains(info)) return;
        activeInfos.remove(info);
        updateNotification();
    }

    /**
     * pause the downloadJob
     */
    void pause(DownloadTask task) {
        String key = task.key;
        if (!jobs.containsKey(key)) return;
        jobs.get(key).pause();
    }

    /**
     * resume the downloadJob if it has not been running
     */
    void resume(DownloadTask task) {
        String key = task.key;
        if (!jobs.containsKey(key)) return;
        DownloadJob job = jobs.get(key);
        if (job.isRunning()) return;
        job.resume();
        activeInfos.add(job.info);
        updateNotification();
    }

    /**
     * delete download info, delete file
     */
    void delete(final DownloadInfo info) {
        if (info == null || !infos.containsValue(info)) return;
        infos.remove(info.key);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                provider.delete(info);
                File file = new File(info.path);
                if (file.exists() && !file.delete()) {
                    Log.w(TAG, "can not delete file: " + file.getPath());
                }
            }
        });
    }

    /**
     * add download listener
     */
    void addListener(DownloadTask task) {
        String key = task.key;
        if (!infos.containsKey(key)) {
            if (task.listener == null) return;
            task.listener.onStateChanged(key, DownloadState.STATE_PREPARED);
        } else {
            if (!jobs.containsKey(key)) {
                if (task.listener == null) return;
                task.listener.onStateChanged(key, DownloadState.STATE_FINISHED);
            } else {
                jobs.get(key).addListener(task.listener);
            }
        }
    }

    /**
     * delete download listener
     */
    void removeListener(DownloadTask task) {
        String key = task.key;
        if (!jobs.containsKey(key)) return;
        jobs.get(key).removeListener(task.listener);
    }

    /**
     * notify the downloadJob has been create
     */
    private void onJobCreated(DownloadInfo info) {
        for (DownloadJobListener downloadJobListener : downloadJobListeners) {
            downloadJobListener.onCreated(info);
        }
    }

    /**
     * notify the downloadJob has been started
     */
    void onJobStarted(DownloadInfo info) {
        updateNotification();
        for (DownloadJobListener downloadJobListener : downloadJobListeners) {
            downloadJobListener.onStarted(info);
        }
    }

    /**
     * notify the downloadJob has been completed
     */
    void onJobCompleted(boolean finished, DownloadInfo info) {
        String key = info.key;
        activeInfos.remove(info);
        updateNotification();
        if (finished) {
            jobs.remove(key);
        }
        for (DownloadJobListener downloadJobListener : downloadJobListeners) {
            downloadJobListener.onCompleted(finished, info);
        }
    }

    /**
     * update the notification avoid too fast
     */
    private void updateNotification() {
        if (notifier == null) return;
        handler.removeCallbacks(updateNotificationRunnable);
        handler.postDelayed(updateNotificationRunnable, 100);
    }

}
