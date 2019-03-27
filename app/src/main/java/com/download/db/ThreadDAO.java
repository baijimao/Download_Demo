package com.download.db;

import com.download.entities.ThreadInfo;

import java.util.List;

/**
 * @author: baijimao
 * @date: 2019/3/25
 * Description:
 */
public interface ThreadDAO {

    /**
     * insert threadInfo
     * @param threadInfo
     */
    public void insertThread(ThreadInfo threadInfo);

    /**
     * delete thread
     * @param url
     * @param thread_id
     */
    public void deleteThread(String url, int thread_id);

    /**
     * update download progress
     * @param url
     * @param thread_id
     * @param finished
     */
    public void updateThread(String url, int thread_id, int finished);

    /**
     * query threadInfo of the file
     * @param url
     * @return
     */
    public List<ThreadInfo> getThreads(String url);

    /**
     * determines whether the thread exists
     * @param url
     * @param thread_id
     * @return
     */
    public boolean isExists(String url, int thread_id);
}
