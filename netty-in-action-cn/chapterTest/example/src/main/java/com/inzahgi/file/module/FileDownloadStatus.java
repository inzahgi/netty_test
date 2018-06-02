package com.inzahgi.file.module;

import java.util.Map;
import java.util.TreeMap;

public class FileDownloadStatus {
    private String fileName;
    private String filePath;
    private long fileLength;
    private long maxFileBlockLength;
    private String md5;
    private int fileBlockTotal;
    private String[] blockMd5;
    private Map<Integer, FileDownloadEntity> map;
    private int startIndex = -1;
    private int endIndex = -1;

    public FileDownloadStatus(String fileName, String filePath, long fileLength, long maxFileBlockLength, String md5) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileLength = fileLength;
        this.maxFileBlockLength = maxFileBlockLength;
        this.md5 = md5;
        this.fileBlockTotal = (int)(fileLength/maxFileBlockLength) +
                (fileLength%maxFileBlockLength == 0 ? 0 : 1);
        this.blockMd5 = new String[fileBlockTotal];
        map = new TreeMap<>();

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getFileBlockTotal() {
        return fileBlockTotal;
    }

    public void setFileBlockTotal(int fileBlockTotal) {
        this.fileBlockTotal = fileBlockTotal;
    }

    public long getMaxFileBlockLength() {
        return maxFileBlockLength;
    }

    public void setMaxFileBlockLength(long maxFileBlockLength) {
        this.maxFileBlockLength = maxFileBlockLength;
    }

    public String[] getBlockMd5() {
        return blockMd5;
    }

    public void setBlockMd5(String[] blockMd5) {
        this.blockMd5 = blockMd5;
    }


    public Map<Integer, FileDownloadEntity> getMap() {
        return map;
    }

    public void setMap(Map<Integer, FileDownloadEntity> map) {
        this.map = map;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }
}
