package com.hante.smartpadposclient.net;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    public String TAG = "RetrofitFactory";
    private static final RetrofitFactory retrofitFactory = new RetrofitFactory();
    //TODO包名
    public static final String CACHE_NAME = "com.hante.hantepay";
    public static final int DEFAULT_CONNECT_TIMEOUT = 30;
    public static final int DEFAULT_WRITE_TIMEOUT = 60;
    public static final int DEFAULT_READ_TIMEOUT = 60;

    private Retrofit retrofit;
    private HttpApi httpApi;
    private ConnectionPool connectionPool = new ConnectionPool(10, 5, TimeUnit.MINUTES);
    /**
     * 请求失败重连次数
     */
    private int RETRY_COUNT = 0;
    private OkHttpClient.Builder okHttpBuilder;

    public void clearConnection() {
        try{
            if (connectionPool.connectionCount() > 0) {
                connectionPool.evictAll();
            }
        }catch (Exception e){}
    }
    //构造方法私有
    private RetrofitFactory() {
        //手动创建一个OkHttpClient并设置超时时间
        okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.connectionPool(connectionPool);//设置连接池

        /**
         * 设置头信息
         */
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder requestBuilder = originalRequest.newBuilder()
                        .addHeader("Content-Type", "application/json; charset=utf-8");
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };

        //需要时候添加
        okHttpBuilder.addInterceptor(headerInterceptor);


        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("网络请求",message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //设置 Debug Log 模式
        okHttpBuilder.addInterceptor(loggingInterceptor);

        /**
         * 设置超时和重新连接
         */
        okHttpBuilder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        okHttpBuilder.readTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS);
        okHttpBuilder.writeTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS);
        //错误重连
        okHttpBuilder.retryOnConnectionFailure(true);

        GsonBuilder builder = new GsonBuilder();

        // Register an adapter to manage the date types as long values
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        retrofit = new Retrofit.Builder()
                .client(okHttpBuilder.build())
//                .addConverterFactory(GsonConverterFactory.create())//json转换成JavaBean
                //解析返回结果
                .addConverterFactory(GsonConverterFactory.create(builder.setLenient() .create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://gateway.hantepay.com/")
                .build();
        httpApi = retrofit.create(HttpApi.class);
    }


    //获取单例
    public static RetrofitFactory getInstance() {
        return retrofitFactory;
    }

    /**
     * 获取retrofit
     *
     * @return
     */
    public Retrofit getRetrofit() {
        return retrofit;
    }


    /**
     * 获取httpService
     *
     * @return
     */
    public HttpApi getHttpApi() {
        return httpApi;
    }

    /**
     * 设置httpService
     *
     * @return
     */
    public void setHttpApi(HttpApi httpApi) {
        this.httpApi = httpApi;
    }

    /**
     * 设置订阅 和 所在的线程环境
     */
    public <T> void toSubscribe(Observable<T> o, DisposableObserver<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(RETRY_COUNT)//请求失败重连次数
                .subscribe(s);

    }




    public Observable<BaseResponse<PosInfoResponse>> queryPosIP(String sn) {
        return httpApi.queryPosIP(sn,"short") //调用登录方法
                .subscribeOn(Schedulers.io())//IO线程异步请求
                .observeOn(AndroidSchedulers.mainThread());//结果响应在主线程
    }



}
