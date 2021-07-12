package com.arch.monitor_data.util;

public class HttpResponse<T> {
    private Integer code;
    private T data;
    private String msg;

    private HttpResponse(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static <T>HttpResponse<T>SUCCESS(T data, String msg){
        return new HttpResponse<>(HttpStatusEnum.SUCCESS.getCode(),data,msg);
    }

    public static <T> HttpResponse<T>FAIL(String msg){
        return new HttpResponse<>(HttpStatusEnum.FAIL.getCode(),null,msg);
    }

    public static <T> HttpResponse<T>FAIL(int code,String msg){
        return new HttpResponse<>(code,null,msg);
    }

    public static <T> HttpResponse<T>SUCCESS(String msg){
        return new HttpResponse<>(HttpStatusEnum.SUCCESS.getCode(),null,msg);
    }

    public static <T> HttpResponse<T>SUCCESS(T data){
        return new HttpResponse<>(HttpStatusEnum.SUCCESS.getCode(),data,null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
