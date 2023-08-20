package com.ddonging.wenba.download;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by 4ndroidev on 16/10/6.
 */

/*
 * many-to-one association with DownloadInfo
 * many-to-one association with DownloadJob
 * one-to-one association with DownloadListener
 */

public class DownloadTask implements Comparable<DownloadTask> {

    private static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "Download";

    private final DownloadEngine engine;

    public String key;
    public long size;
    public long createTime;
    public long id;
    public String url;
    public String name;
    public String path;
    public String source;
    public String extras;
    DownloadListener listener;

    private DownloadTask(DownloadEngine engine, long id, String url, String name, String source, String extras, DownloadListener listener) {
        this.engine = engine;
        this.id = id;
        this.url = url;
        this.name = name;
        this.path = DOWNLOAD_PATH + File.separator + name;
        this.source = source;
        this.key = generateKey();
        this.extras = extras;
        this.listener = listener;
        this.engine.prepare(this);
    }

    DownloadTask(DownloadEngine engine, DownloadInfo info, DownloadListener listener) {
        this.engine = engine;
        this.id = info.id;
        this.url = info.url;
        this.name = info.name;
        this.path = info.path;
        this.source = info.source;
        this.key = info.key;
        this.extras = info.extras;
        this.createTime = info.createTime;
        this.listener = listener;
        this.engine.prepare(this);
    }

    private String generateKey() {
        StringBuilder sb = new StringBuilder();
        sb.append(id);
        int index = name.lastIndexOf(".");
        if (index >= 0) {
            sb.append(name.substring(index));
        }
        return sb.toString();
    }

    DownloadInfo generateInfo() {
        return new DownloadInfo(id, key, url, name, path, source, extras);
    }

    public void start() {
        engine.enqueue(this);
    }

    public void pause() {
        engine.pause(this);
    }

    public void resume() {
        engine.resume(this);
    }

    public void delete() {
        engine.delete(this);
        this.listener = null;
    }

    public void resumeListener() {
        engine.addListener(this);
    }

    public void pauseListener() {
        engine.removeListener(this);
    }

    public void clear() {
        setListener(null);
    }

    public void setListener(DownloadListener listener) {
        if (this.listener == listener) return;
        engine.removeListener(this);
        this.listener = listener;
        if (listener != null) {
            engine.addListener(this);
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadTask task = (DownloadTask) o;
        return key != null ? key.equals(task.key) : task.key == null;
    }

    @Override
    public int compareTo(DownloadTask other) {
        long diff = createTime - other.createTime;
        return diff == 0 ? 0 : diff > 0 ? 1 : -1;
    }

    public static class Builder {
        private final DownloadEngine engine;
        private long id;
        private String url;
        private String name;
        private String source;
        private String extras;
        private DownloadListener listener;

        Builder(DownloadEngine engine) {
            this.engine = engine;
        }

        Builder id(long id) {
            this.id = id;
            return this;
        }

        Builder url(String url) {
            this.url = url;
            return this;
        }

        Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder extras(String extras) {
            this.extras = extras;
            return this;
        }

        public Builder listener(DownloadListener listener) {
            this.listener = listener;
            return this;
        }

        public DownloadTask create() {
            if (TextUtils.isEmpty(url) || TextUtils.isEmpty("name")) {
                throw new IllegalArgumentException("url or name can't be empty!");
            }
            return new DownloadTask(engine, id, url, name, source, extras, listener);
        }

    }

}
