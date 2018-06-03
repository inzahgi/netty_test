package com.inzahgi.file.module;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class FileDownloadEntity {

    private int headType;

    private String fileName;

    private String filePath;

    private long fileLength;

    private long maxFileBlockLength;

    private int fileBlockTotal;

    private int fileBlockCurNo;

    private long blockStartPos;

    private long blockEndPos;

    private String md5;

    private byte[] fileBlock;

    private String endInfo = "send finish!!";

    public enum HEAD_TYPE{
        FIND_FILE(0), FILE_INFO(1), FILE_BLOCK(2), FILE_END(3);
        private int type;
        private HEAD_TYPE(int type){
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }



    public int getHeadType() {
        return headType;
    }

    public void setHeadType(int headType) {
        this.headType = headType;
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

    public long getMaxFileBlockLength() {
        return maxFileBlockLength;
    }

    public void setMaxFileBlockLength(long maxFileBlockLength) {
        this.maxFileBlockLength = maxFileBlockLength;
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

    public int getFileBlockCurNo() {
        return fileBlockCurNo;
    }

    public void setFileBlockCurNo(int fileBlockCurNo) {
        this.fileBlockCurNo = fileBlockCurNo;
    }

    public long getBlockStartPos() {
        return blockStartPos;
    }

    public void setBlockStartPos(long blockStartPos) {
        this.blockStartPos = blockStartPos;
    }

    public long getBlockEndPos() {
        return blockEndPos;
    }

    public void setBlockEndPos(long blockEndPos) {
        this.blockEndPos = blockEndPos;
    }

    public byte[] getFileBlock() {
        return fileBlock;
    }

    public void setFileBlock(byte[] fileBlock) {
        this.fileBlock = fileBlock;
    }

    public String getEndInfo() {
        return endInfo;
    }

    public void setEndInfo(String endInfo) {
        this.endInfo = endInfo;
    }
}
