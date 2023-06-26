"# POS_SmartPad_Demo"
Sunmi SmartPad 接入Hante 收款
支持：信用卡,微信,支付宝

原理:Sunmi SmartPad 作为 服务端 , Demo作为 客户端,使用Socket 交互

使用步骤：
   第一步：导入: tcp-release.aar
   第二步：获取 SmartPad 服务端 IP + 端口号
   第三步: 使用 tcp-release.aar
    HanteSDKUtils.connectTcpService(Context context, String ip, String deviceId, String key, SocketCallback callback) 建立连接