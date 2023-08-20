package com.ddonging.wenba.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4ndroidev on 16/10/6.
 */

// one-to-one association with DownloadInfo
class DownloadJob implements Runnable {

    private static final int MIN_READ_STEP = 8 * 1024;
    private static final int MIN_STORE_STEP = MIN_READ_STEP * 16;

    private boolean isPaused;
    private boolean isDeleted;
    private DownloadEngine engine;
    private final List<DownloadListener> listeners;

    DownloadInfo info;

    private final Runnable changeState = new Runnable() {
        @Override
        public void run() {
            synchronized (DownloadJob.class) {
                for (DownloadListener listener : listeners) {
                    listener.onStateChanged(info.key, DownloadJob.this.info.state);
                }
            }
            switch (info.state) {
                case DownloadState.STATE_RUNNING:
                    engine.onJobStarted(info);
                    break;
                case DownloadState.STATE_FINISHED:
                    engine.onJobCompleted(true, info);
                    delete();
                    clear();
                    break;
                case DownloadState.STATE_FAILED:
                case DownloadState.STATE_PAUSED:
                    engine.onJobCompleted(false, info);
                    break;
            }
        }
    };

    private final Runnable changeProgress = new Runnable() {
        @Override
        public void run() {
            synchronized (DownloadJob.class) {
                for (DownloadListener listener : listeners) {
                    listener.onProgressChanged(info.key, DownloadJob.this.info.finishedLength, DownloadJob.this.info.contentLength);
                }
            }
        }
    };

    DownloadJob(DownloadEngine engine, DownloadInfo info) {
        this.engine = engine;
        this.info = info;
        this.listeners = new ArrayList<>();
    }

    void addListener(DownloadListener listener) {
        synchronized (DownloadJob.class) {
            if (listener == null || listeners.contains(listener)) return;
            listener.onStateChanged(info.key, info.state);
            listeners.add(listener);
        }
    }

    void removeListener(DownloadListener listener) {
        synchronized (DownloadJob.class) {
            if (listener == null || !listeners.contains(listener)) return;
            listeners.remove(listener);
        }
    }

    boolean isRunning() {
        return DownloadState.STATE_RUNNING == info.state;
    }
    void enqueue() {
        resume();
    }

    void pause() {
        isPaused = true;
        if (info.state != DownloadState.STATE_WAITING) return;
        onStateChanged(DownloadState.STATE_PAUSED, false);
    }

    void delete() {
        isDeleted = true;
    }

    void resume() {
        if (isRunning()) return;
        onStateChanged(DownloadState.STATE_WAITING, false);
        isPaused = false;
        engine.executor.submit(this);
    }

    private void clear() {
        listeners.clear();
        engine = null;
        info = null;
    }

    private void onStateChanged(int state, boolean updateDb) {
        info.state = state;
        if (updateDb) engine.provider.update(info);
        engine.handler.removeCallbacks(changeState);
        engine.handler.post(changeState);
    }

    private void onProgressChanged(long finishedLength, long contentLength) {
        info.finishedLength = finishedLength;
        info.contentLength = contentLength;
        engine.handler.removeCallbacks(changeProgress);
        engine.handler.post(changeProgress);
    }

    private boolean prepare() {
        if (isDeleted) {
            clear();
            return false;
        } else if (isPaused) {
            onStateChanged(DownloadState.STATE_PAUSED, false);
            if (!engine.provider.exists(info)) {
                engine.provider.insert(info);
            } else {
                engine.provider.update(info);
            }
            return false;
        } else {
            onStateChanged(DownloadState.STATE_RUNNING, false);
            onProgressChanged(info.finishedLength, info.contentLength);
            if (engine.interceptors != null) {
                for (DownloadManager.Interceptor interceptor : engine.interceptors) {
                    interceptor.updateDownloadInfo(info);
                }
            }
            if (!engine.provider.exists(info)) {
                engine.provider.insert(info);
            }
            return true;
        }
    }

    @Override
    public void run() {
        if (!prepare()) return;
        long finishedLength = info.finishedLength;
        long contentLength = info.contentLength;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        try {
            connection = (HttpURLConnection) new URL(info.url).openConnection();
            connection.setAllowUserInteraction(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");
            if (finishedLength != 0 && contentLength > 0) {
                connection.setRequestProperty("Range", "bytes=" + finishedLength + "-" + contentLength);
            } else {
                contentLength = connection.getContentLength();
                if (contentLength == -1) {
                    contentLength = Long.parseLong(connection.getHeaderField("Content-Length"));
                }
            }
            int responseCode = connection.getResponseCode();
            if (contentLength > 0 && (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_PARTIAL)) {
                inputStream = connection.getInputStream();
                File file = new File(info.path);
                randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(finishedLength);
                byte[] buffer = new byte[MIN_READ_STEP];
                int len;
                int buck = 0;
                long bytesRead = finishedLength;
                while (!this.isDeleted && !this.isPaused && (len = inputStream.read(buffer)) != -1) {
                    randomAccessFile.write(buffer, 0, len);
                    bytesRead += len;
                    finishedLength = bytesRead;
                    onProgressChanged(finishedLength, contentLength);
                    buck += len;
                    if (buck >= MIN_STORE_STEP) {
                        buck = 0;
                        randomAccessFile.getFD().sync();
                        engine.provider.update(info);
                    }
                }
                connection.disconnect();
                if (isDeleted) {
                    clear();
                } else if (this.isPaused) {
                    onStateChanged(DownloadState.STATE_PAUSED, true);
                } else {
                    info.finishTime = System.currentTimeMillis();
                    onStateChanged(DownloadState.STATE_FINISHED, true);
                }
            } else {
                onStateChanged(DownloadState.STATE_FAILED, true);
            }
        } catch (final Exception e) {
            onStateChanged(DownloadState.STATE_FAILED, true);
        } finally {
            try {
                if (randomAccessFile != null)
                    randomAccessFile.close();
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null)
                connection.disconnect();
        }
    }
}
