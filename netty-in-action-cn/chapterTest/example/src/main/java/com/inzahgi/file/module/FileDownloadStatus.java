package com.inzahgi.file.module;

public class FileDownloadStatus {
    private String fileName;
    private String filePath;
    private long fileLength;
    private long maxFileBlockLength;
    private String md5;
    private int fileBlockTotal;
    private String[] blockMd5;

    public FileDownloadStatus(String fileName, String filePath, long fileLength, long maxFileBlockLength, String md5) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileLength = fileLength;
        this.maxFileBlockLength = maxFileBlockLength;
        this.md5 = md5;
        this.fileBlockTotal = (int)(fileLength/maxFileBlockLength) +
                (fileLength%maxFileBlockLength == 0 ? 0 : 1);
        this.blockMd5 = new String[fileBlockTotal];

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
}
