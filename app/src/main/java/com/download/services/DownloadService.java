package com.download.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.download.entities.FileInfo;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends Service {
    public static final String DOWNLOAD_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/downloads/";
    public static final String ACTION_START = "action_start";
    public static final String ACTION_STOP = "action_stop";
    public static final String ACTION_UPDATE = "action_update";
    public static final int MSG_INIT = 0;
    private DownloadTask mTask = null;


    public DownloadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("test", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("test", "onStartCommand");
        // get the parameter from the activity
        if (ACTION_START.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.i("test", "Start: " + fileInfo);

            //start the initialization thread
            new InitThread(fileInfo).start();
        } else if (ACTION_STOP.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.i("test", "Stop: " + fileInfo);
            if (mTask != null) {
                mTask.isPause = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT:
                    FileInfo fileInfo = (FileInfo) msg.obj;
                    Log.i("test", fileInfo.toString());
                    //start the download task
                    mTask = new DownloadTask(DownloadService.this, fileInfo);
                    mTask.download();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /**
     * initialize thread
     */

    class InitThread extends Thread {
        private FileInfo mFileInfo = null;

        public InitThread(FileInfo fileInfo) {
            this.mFileInfo = fileInfo;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try {
                // connect network file
                URL url = new URL(mFileInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int length = -1;
                if (conn.getResponseCode() == 200) {
                    // get file length
                    length = conn.getContentLength();
                    Log.i("test", "length: " + length);
                }
                if (length <= 0) {
                    return;
                }

                File dir = new File(DOWNLOAD_PATH);
                if (!dir.exists()) {
                    boolean result = dir.mkdirs();
                    Log.i("test", "result: " + result);
                }
                // create the file locally
                File file = new File(dir, mFileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                // set file length
                raf.setLength(length);
                mFileInfo.setLength(length);
                mHandler.obtainMessage(MSG_INIT, mFileInfo).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                try {
                    if (raf != null) {
                        raf.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
