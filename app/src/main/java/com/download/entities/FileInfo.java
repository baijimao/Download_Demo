package com.download.entities;

import java.io.Serializable;

/**
 * the information of file
 * @author: baijimao
 * @date: 2019/3/24
 * Description:
 */
public class FileInfo implements Serializable {
    private int id;
    private String url;
    private String fileName;
    private int length;
    private int finished;

    public FileInfo() {
        super();
    }

    public FileInfo(int id, String url, String fileName, int length, int finished) {
        super();
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.length = length;
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", length=" + length +
                ", finished=" + finished +
                '}';
    }
}
