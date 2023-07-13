"# POS_SmartPad_Demo"
Sunmi SmartPad <br/>
支持快捷键:*#00# 检查环境, *#01# 打开wiif <br/>
Hante POS 收款支持：信用卡,微信,支付宝 <br/>

原理:Sunmi SmartPad 作为 POS 服务端 , Demo作为 客户端,使用Socket 交互<br/>
注意：使用前请先，观看使用视频（目录:app/SmartPad_POS.mp4）<br/>
使用步骤：<br/>
   第一步：导入: HantePOSAPI.aar(目录:app/libs/HantePOSAPI.aar) <br/>
   第二步: 使用 HantePOSAPI <br/>
    <b>2.1 连接POS服务</b>  
   /** <br/>
     * 连接POS服务<br/>
     * @param ip ip 服务IP地址<br/>
     * @param deviceId pos (配对数据 ，未配对传空)<br/>
     * @param token pos (配对数据，未配对传空)<br/>
     */
    public static void connectPOSService(Context context, String ip, String deviceId, String token, SocketCallback callback)
    <br/>
    举例:  <br/>  
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


    2.2 设备配对(未配对需要进行)
     /**
     * 设备配对
     * @param pairingCode 配对码
     * @param merchantNo 商户号 
     * 配对成功获取 token 和 deviceId
     */
    public static void pairingDevice(String pairingCode,String merchantNo)

    
    2.3配对成功初始化token: <br/>
    /** <br/>
     * 初始化 token<br/>
     * @param deviceId 设备id<br/>
     * @param token<br/>
     */<br/>
    public static void refreshToken(String deviceId,String token)
    举例:<br/>
    HantePOSAPI.refreshToken("ht98234","s2j409sdfjlhg1rt");

   <b>2.4 重置配对（忘记deviceId 和 token 可以进行重置配对）</b> <br/>
    /**<br/>
     * 解除设备配对<br/>
     * @param merchantNo 商户号<br/>
     */<br/>
    public static void reSetPairDevice(String merchantNo)

    举例
       HantePOSAPI.reSetPairDevice("1101301");
    
   <b> 2.5发起交易:</b> <br/>
    /**<br/>
     * 收款<br/>
     * @param transType  交易类型，  SALE 直接收款  AUTH 预授权(刷卡支付生效)<br/>
     * @param amount 交易金额 （单位:分）<br/>
     * @param taxAmount 税费<br/>
     * @param tipAmount 消费<br/>
     * @param paymentScenario 收款方式  SCAN_CODE_PAYMENT : 付款码支付  QR_CODE_PAYMENT : 二维码支付  POS_PAYMENT : 刷卡支付 HANTE_CASHIER：汉特收银台<br/>
     * @param orderNo 订单号<br/>
     * @param remark 备注<br/>
     * @param deviceId 设备ID （设备配对获取）<br/>
     * @param merchantNo 商户号<br/>
     * 收款成功响应 transactionId(Hante 交易流水号)
     */
   
    public static void sale(String transType,int amount,int taxAmount,int tipAmount,String paymentScenario,String orderNo,String remark,String deviceId,String merchantNo)
    
  举例：信用卡收款
  
  HantePOSAPI.sale("SALE",1,0,0,"POS_PAYMENT","1624646168464174","测试","ht98234","1101301");
    
   

   <b> 2.6发起退款：</b> <br/>
     /**<br/>
     * 退款<br/>
     * @param amount 退款金额 （单位:分）<br/>
     * @param transactionId Hante交易流水号<br/>
     * @param deviceId 设备ID （设备配对获取）<br/>
     * @param merchantNo 商户号<br/>
     */
    public static void refund(int amount,String transactionId,String deviceId,String merchantNo)

    举例： 
      HantePOSAPI.refund(1,"2023061246465489456","ht98234","1101301");
    
    
