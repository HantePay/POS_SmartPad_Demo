package com.hante.smartpadposclient.net;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HttpApi {



    /**
     * 查询 设备 POS 服务 IP 端口号
     * @return
     */
    @GET("http://test.hantepay.cn/route/v2.0.0/machine/info")
    Observable<BaseResponse<PosInfoResponse>> queryPosIP(@Query("machineCode") String sn,@Query("type") String type);


}
