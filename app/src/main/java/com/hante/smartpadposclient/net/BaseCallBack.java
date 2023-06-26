package com.hante.smartpadposclient.net;

/**
 * 回调
 * @param <T>
 */
public interface BaseCallBack<T> {

    void callSuccess(T t);

    void callFail(String code,String msg);
}
