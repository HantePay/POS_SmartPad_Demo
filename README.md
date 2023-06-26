"# POS_SmartPad_Demo"
Sunmi SmartPad 接入Hante 收款
支持：信用卡,微信,支付宝

原理:Sunmi SmartPad 作为 服务端 , Demo作为 客户端,使用Socket 交互

使用步骤：<br/>
   第一步：导入: tcp-release.aar <br/>
   第二步：获取 SmartPad 服务端 IP + 端口号<br/>
   第三步: 使用 tcp-release.aar 建立连接 <br/>
    HanteSDKUtils.connectTcpService(Context context, String ip, String deviceId, String key, SocketCallback callback) <br/>

     HanteSDKUtils.connectTcpService(Context, ip, deviceId,key, new SocketCallback() {
         @Override
         public void connected() {//POS连接成功}

         @Override
         public void connectionFails(String s) {//POS连接失败}

         @Override
         public void error(int code,String s) {//POS异常}

         @Override
         public void heartbeat(String s) {//心跳包 }

         @Override
         public void disConnected() {//断开连接}

         @Override
         public void reconnection() { //断开重新连接}

         @Override
         public void receiveMessage(String msg) {//收到服务端响应信息}
     });

     
     发送消息消息:
     HanteSDKUtils.sentMessageV2(TcpMessageBase msg);

     
