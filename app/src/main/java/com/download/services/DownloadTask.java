package com.download.services;

import android.content.Context;
import android.content.Intent;

import com.download.db.ThreadDAO;
import com.download.db.ThreadDAOImpl;
import com.download.entities.FileInfo;
import com.download.entities.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * download task class
 * @author: baijimao
 * @date: 2019/3/26
 * Description:
 */
public class DownloadTask {
    private Context mContext = null;
    private FileInfo mFileInfo = null;
    private ThreadDAO mDao = null;
    private int mFinished = 0;
    public boolean isPause = false;

    public DownloadTask(Context mContext, FileInfo mFileInfo) {
        this.mContext = mContext;
        this.mFileInfo = mFileInfo;
        mDao = new ThreadDAOImpl(mContext);
    }

    public void download() {
        // reads thread information for the database
        List<ThreadInfo> threadInfos = mDao.getThreads(mFileInfo.getUrl());
        ThreadInfo threadInfo = null;
        if (threadInfos.size() == 0) {
            // initializes threadInfo object
            threadInfo = new ThreadInfo(0, mFileInfo.getUrl(), 0, mFileInfo.getLength(), 0);
        } else {
            threadInfo = threadInfos.get(0);
        }
        // create child thread downloads
        new DownloadThread(threadInfo).start();
    }

    /**
     * download thread
     */
    class DownloadThread extends Thread {
        private ThreadInfo mThreadInfo = null;

        public DownloadThread(ThreadInfo threadInfo) {
            this.mThreadInfo = threadInfo;
        }

        @Override
        public void run() {
            //insert threadInfo into the database
            if (!mDao.isExists(mThreadInfo.getUrl(), mThreadInfo.getId())) {
                mDao.insertThread(mThreadInfo);
            }
            //set the download location
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream input = null;
            try {
                URL url = new URL(mThreadInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
                conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getEnd());
                //set the file write location
                File file = new File(DownloadService.DOWNLOAD_PATH, mFileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);
                Intent intent = new Intent(DownloadService.ACTION_UPDATE);
                mFinished += mThreadInfo.getFinished();
                // start download
                if (conn.getResponseCode() == 206) {
                    // read data
                    input = conn.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = input.read(buffer)) != -1) {
                        // write file
                        raf.write(buffer, 0, len);
                        // broadcast the download progress to the activity
                        mFinished += len;
                        if (System.currentTimeMillis() - time > 500) {
                            intent.putExtra("finished", mFinished * 100 / mFileInfo.getLength());
                            mContext.sendBroadcast(intent);
                        }

                        // save the download progress when the download stops
                        if (isPause) {
                            mDao.updateThread(mThreadInfo.getUrl(), mThreadInfo.getId(), mFinished);
                            return;
                        }
                    }
                    // delete thread info
                    mDao.deleteThread(mThreadInfo.getUrl(), mThreadInfo.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                try {
                    if (input != null) {
                        input.close();
                    }
                    if (raf != null) {
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
