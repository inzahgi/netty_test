package com.inzahgi.file.module;

public class ConstantStatus {
    public interface REQUEST_HEAD_TYPE{
        int requestInfo = 0;
        int requestBlock = 2;
        int requestEnd = 4;
    }

    public interface RESPON_HEAD_TYPE{
        int responeInfo = 1;
        int responseBlock =3;
        int responseEnd = 5;
    }


}
