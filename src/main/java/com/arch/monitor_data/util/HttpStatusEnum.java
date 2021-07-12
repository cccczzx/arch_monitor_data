package com.arch.monitor_data.util;

public enum HttpStatusEnum {

    SUCCESS(200),FAIL(-1);
    private Integer code=1;
    HttpStatusEnum(int code){
        this.code=code;
    }
    public int getCode(){
        return code;
    }

}
