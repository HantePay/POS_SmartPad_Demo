package com.hante.smartpadposclient;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.hante.smartpadposclient.dialog.CustomDialog;
import com.hante.smartpadposclient.dialog.OrderInfoDialog;
import com.hante.smartpadposclient.dialog.PairingCodeDialog;
import com.hante.smartpadposclient.dialog.PaymentTipDialog;
import com.hante.smartpadposclient.dialog.TipDialog;
import com.hante.smartpadposclient.net.BaseCallBack;
import com.hante.smartpadposclient.net.BaseResponse;
import com.hante.smartpadposclient.net.PosInfoResponse;
import com.hante.smartpadposclient.net.RetrofitFactory;
import com.hante.smartpadposclient.net.SimpleObserver;
import com.hante.smartpadposclient.utils.Constant;
import com.hante.smartpadposclient.utils.SpUtils;
import com.hante.tcp.bean.v2.POSTransaction;
import com.hante.tcp.callback.SocketCallback;
import com.hante.tcp.util.HantePOSAPI;
import com.hjq.toast.ToastUtils;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.math.BigDecimal;
import java.util.Random;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Smart Pad POS Demo
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG="SmartPadPOS";

    CommonTitleBar title_bar;

    private TextView ip_et;
    private TextView param_tv;
    private TextView result_tv;
    private TextView merchant_et;
    private TextView device_sn_et;

    private EditText credit_card_sign_in_et;

    private EditText token_verify_code_et;

    private EditText reconnection_time_et;

    private TextView reconnection_order_et;



    private TextView status_tv;

    /**
     * 金额
     */
    private EditText et_amount;

    private TextView et_amount_pro;

    private EditText et_tax_amount;
    private TextView et_tax_amount_pro;

    private EditText et_tip_amount;
    private TextView et_tip_amount_pro;

    private EditText et_remark;


    private EditText refund_et;
    private TextView refund_et_pro;

    private EditText capture_et;

    private EditText capture_tip_et;

    private EditText order_tr_et;

    private ImageView oder_sign_img;

    /**
     * pos 创建 服务连接设备
     * 商户后台->设备管理->设备列表->设置->创建终端密钥
     */
    private EditText device_id_et;
    private EditText device_key_et;
    CustomDialog customDialog;

    PaymentTipDialog paymentTipDialog;

    TipDialog tipDialog;

    TipDialog checkConfigDialog;

    OrderInfoDialog orderInfoDialog;

    private String deviceSN="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title_bar=findViewById(R.id.act_tcp_v2_title_bar);

        device_id_et=findViewById(R.id.device_id_et);
        device_key_et=findViewById(R.id.device_key_et);
        ip_et = findViewById(R.id.ip_et);
        param_tv = findViewById(R.id.param_tv);
        result_tv = findViewById(R.id.result_tv);
        merchant_et = findViewById(R.id.merchant_et);
        device_sn_et=findViewById(R.id.device_sn_et);
        credit_card_sign_in_et=findViewById(R.id.credit_card_sign_in_et);

        status_tv=findViewById(R.id.status_tv);
        reconnection_time_et=findViewById(R.id.reconnection_time_et);
        reconnection_order_et=findViewById(R.id.reconnection_order_et);
        et_amount=findViewById(R.id.et_amount);
        et_amount_pro=findViewById(R.id.et_amount_pro);
        et_tax_amount=findViewById(R.id.et_tax_amount);
        et_tax_amount_pro=findViewById(R.id.et_tax_amount_pro);


        et_tip_amount=findViewById(R.id.et_tip_amount);
        et_tip_amount_pro=findViewById(R.id.et_tip_amount_pro);

        et_remark=findViewById(R.id.et_remark);
        order_tr_et=findViewById(R.id.order_tr_et);

        refund_et=findViewById(R.id.refund_et);
        refund_et_pro=findViewById(R.id.refund_et_pro);

        capture_et=findViewById(R.id.capture_et);
        capture_tip_et=findViewById(R.id.capture_tip_et);
        token_verify_code_et=findViewById(R.id.token_verify_code_et);

        oder_sign_img=findViewById(R.id.oder_sign_img);




        et_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    try{
                        double amount=Double.parseDouble(s.toString().trim());
                        et_amount_pro.setText("$"+mul(amount,0.01,2));
                    }catch (RuntimeException e){
                        e.printStackTrace();
                    }
                }else{
                    et_amount_pro.setText("$0.00");
                }
            }
        });

        et_tax_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    try{
                        double amount=Double.parseDouble(s.toString().trim());
                        et_tax_amount_pro.setText("$"+mul(amount,0.01,2));
                    }catch (RuntimeException e){
                        e.printStackTrace();
                    }
                }else{
                    et_tax_amount_pro.setText("$0.00");
                }
            }
        });


        et_tip_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    try{
                        double amount=Double.parseDouble(s.toString().trim());
                        et_tip_amount_pro.setText("$"+mul(amount,0.01,2));
                    }catch (RuntimeException e){
                        e.printStackTrace();
                    }
                }else{
                    et_tip_amount_pro.setText("$0.00");
                }
            }
        });


        refund_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    try{
                        double amount=Double.parseDouble(s.toString().trim());
                        refund_et_pro.setText("$"+mul(amount,0.01,2));
                    }catch (RuntimeException e){
                        e.printStackTrace();
                    }
                }else{
                    refund_et_pro.setText("$0.00");
                }
            }
        });



        title_bar.setListener(new CommonTitleBar.OnTitleBarListener() {

            @Override
            public void onClicked(View v, int action, String extra) {
                switch (action) {
                    case CommonTitleBar.ACTION_LEFT_BUTTON:
                        finish();
                        break;
                }
            }
        });




        findViewById(R.id.connect_btn).setOnClickListener(this);
        findViewById(R.id.checkstand_btn).setOnClickListener(this);
        findViewById(R.id.qrcode_collection_btn).setOnClickListener(this);
        findViewById(R.id.qrcode_scan_btn).setOnClickListener(this);
        findViewById(R.id.checkout_counter_btn).setOnClickListener(this);
        findViewById(R.id.stop_connect_btn).setOnClickListener(this);

        findViewById(R.id.reconnection_time_btn).setOnClickListener(this);
        findViewById(R.id.checkout_cancel_btn).setOnClickListener(this);

//        findViewById(R.id.jump_to_old).setOnClickListener(this);


        findViewById(R.id.refresh_order_btn).setOnClickListener(this);

        findViewById(R.id.cancel_order_btn).setOnClickListener(this);

        findViewById(R.id.refund_btn).setOnClickListener(this);

        findViewById(R.id.auth_btn).setOnClickListener(this);

        findViewById(R.id.capture_btn).setOnClickListener(this);

        findViewById(R.id.void_btn).setOnClickListener(this);

        findViewById(R.id.query_order_btn).setOnClickListener(this);

        findViewById(R.id.checkout_ebt_btn).setOnClickListener(this);

//        findViewById(R.id.merchant_sign_in_btn).setOnClickListener(this);

        findViewById(R.id.merchant_create_token_btn).setOnClickListener(this);

        findViewById(R.id.merchant_search_token_btn).setOnClickListener(this);

        findViewById(R.id.device_search_btn).setOnClickListener(this);

        findViewById(R.id.credit_card_sign_in_btn).setOnClickListener(this);

        findViewById(R.id.query_oder_sign_btn).setOnClickListener(this);

        findViewById(R.id.merchant_check_btn).setOnClickListener(this);

        findViewById(R.id.checkout_close_countdown_btn).setOnClickListener(this);

        findViewById(R.id.query_order_list_btn).setOnClickListener(this);

        findViewById(R.id.merchant_reset_pair_btn).setOnClickListener(this);

        findViewById(R.id.card_num_payment_btn).setOnClickListener(this);


        customDialog=new CustomDialog(this);

        tipDialog=new TipDialog(this).builder().setPositiveButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        checkConfigDialog=new TipDialog(this).builder().setPositiveButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpCheck();
            }
        });


        String sn=SpUtils.getInstance().getString(Constant.CONFIG_DEVICE_SN,"");
        device_sn_et.setText(sn);
        refreshPosIp(SpUtils.getInstance().getString(Constant.CONFIG_DEVICE_SN,""));
        //设置页面
        findViewById(R.id.device_config_btn).setOnClickListener((View v)->{
            jumpCheck();
        });

        String token=SpUtils.getInstance().getString(Constant.DATE_DEVICE_TOKEN,"");
        if(!TextUtils.isEmpty(token)){
            try{
                JSONObject jsonObject=JSONObject.parseObject(token);
                String deviceId=jsonObject.getString("deviceId");
                String key=jsonObject.getString("token");
                if(!TextUtils.isEmpty(deviceId) && !TextUtils.isEmpty(key)){
                    device_id_et.setText(deviceId);
                    device_key_et.setText(key);
                }
            }catch(RuntimeException e){
                e.printStackTrace();
            }
        }

        if(TextUtils.isEmpty(sn) || TextUtils.isEmpty(token)){
            ToastUtils.show("Please check config");
            jumpCheck();

        }

        order_tr_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                findViewById(R.id.query_order_btn).setEnabled(s.length()>0);
                findViewById(R.id.refund_btn).setEnabled(s.length()>0);
                findViewById(R.id.void_btn).setEnabled(s.length()>0);
                findViewById(R.id.capture_btn).setEnabled(s.length()>0);
            }
        });

    }

    @Override
    public void onClick(View view) {
        int taxAmount=0;
        int tipAmount=0;
        switch (view.getId()) {
            case R.id.connect_btn://连接socket
                if (!HantePOSAPI.isConnected()) {
                    customDialog.show();
                    customDialog.setProgressText("Connect POS");
                    HantePOSAPI.connectPOSService(MainActivity.this, ip_et.getText().toString(), device_id_et.getText().toString(),
                            device_key_et.getText().toString(), new SocketCallback() {
                                @Override
                                public void connected() {
                                    setStatus("Successful connection");
                                    runOnUiThread(()->{
                                        //查询token
                                        HantePOSAPI.searchToken(merchant_et.getText().toString());
                                        refreshTransBtn(true);
                                    });
                                }

                                @Override
                                public void connectionFails(String s) {
                                    setStatus("Connection failure:"+s);
                                }

                                @Override
                                public void error(int code,String s) {
                                    setStatus("Exception:"+s);

                                }

                                @Override
                                public void heartbeat(String s) {
//                            setStatus("Successful connection");
                                    runOnUiThread(()->{
                                        result_tv.setText("Successful connection"+s);
                                    });
                                }

                                @Override
                                public void disConnected() {
                                    setStatus("Disconnect");
                                    runOnUiThread(()->{
                                        refreshTransBtn(false);
                                    });

                                }

                                @Override
                                public void reconnection() {
                                    setStatus("Wait for Reconnect");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this,"Wait for Reconnect",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void receiveMessage(String msg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            result_tv.setText(msg);
                                            try {
                                                POSTransaction orderBean = JSON.parseObject(msg, POSTransaction.class);
                                                //响应订单
                                                if("searchToken".equals(orderBean.getType())){
                                                    JSONObject jsonObject=JSONObject.parseObject(msg);
                                                    String resultCode=jsonObject.getString("resultCode");
                                                    String resultMsg=jsonObject.getString("resultMsg");
                                                    deviceSN=jsonObject.getString("deviceSN");
                                                    setStatus("Successful connection SN:"+deviceSN);
                                                    if("SUCCESS".equals(resultCode)){
                                                        JSONArray tokenList= jsonObject.getJSONArray("tokenList");
                                                        if(null!=tokenList && !tokenList.isEmpty()){
                                                            JSONObject token=tokenList.getJSONObject(0);
                                                            if(null!=token){
                                                                SpUtils.getInstance().save(Constant.DATE_DEVICE_TOKEN,JSONObject.toJSONString(token));
                                                                String deviceId=token.getString("deviceId");
                                                                String tok=token.getString("token");
                                                                if(!TextUtils.isEmpty(deviceId)){
                                                                    if(!deviceId.equals(device_id_et.getText().toString())){
                                                                        device_id_et.setText(deviceId);
                                                                        device_key_et.setText(tok);
                                                                        //刷新 device ID
                                                                        HantePOSAPI.refreshToken(deviceId,tok);
                                                                    }
                                                                }

                                                            }
                                                        }else {
                                                            new PairingCodeDialog(MainActivity.this).builder().setPositiveButton(new BaseCallBack<String>() {
                                                                @Override
                                                                public void callSuccess(String code) {
                                                                    HantePOSAPI.pairingDevice(code,merchant_et.getText().toString());
                                                                }

                                                                @Override
                                                                public void callFail(String code, String msg) {

                                                                }
                                                            }).show();
//                                                    if(checkConfigDialog.isShowing()){
//                                                        checkConfigDialog.dismiss();
//                                                    }
//                                                    ToastUtils.show("Please check token");
//                                                    jumpCheck();
//
                                                        }
                                                    }else{
                                                        if(null!=resultCode){
                                                            if(tipDialog.isShowing()){
                                                                tipDialog.dismiss();
                                                            }
                                                            tipDialog.show(resultCode+":"+resultMsg);
                                                        }
                                                    }
                                                }else if("devicePair".equals(orderBean.getType())){
                                                    try {
                                                        JSONObject token=JSONObject.parseObject(msg);
                                                        String resultCode=token.getString("resultCode");
                                                        String resultMsg=token.getString("resultMsg");
                                                        if("SUCCESS".equals(resultCode)){
                                                            JSONObject tok=token.getJSONObject("token");
                                                            if(null!=tok){

                                                                String deviceId=tok.getString("deviceId");
                                                                String key=tok.getString("token");
                                                                if(!TextUtils.isEmpty(deviceId)){
                                                                    if(!deviceId.equals(device_id_et.getText().toString())){
                                                                        device_id_et.setText(deviceId);
                                                                        device_key_et.setText(key);
                                                                        //关闭连接，重新连接
                                                                        HantePOSAPI.refreshToken(deviceId,key);
                                                                    }
                                                                }

                                                            }
                                                        }else {
                                                            ToastUtils.show(resultCode+":"+resultMsg);
                                                        }


                                                    }catch (RuntimeException e){
                                                        e.printStackTrace();
                                                    }

                                                }else if("orderSignature".equals(orderBean.getType())){
                                                    JSONObject jsonObject=JSONObject.parseObject(msg);
                                                    if(null!=jsonObject){
                                                        String resultCode=jsonObject.getString("resultCode");
                                                        if("SUCCESS".equals(resultCode)){
                                                            String orderNo=jsonObject.getString("orderNo");
                                                            if(!TextUtils.isEmpty(orderNo)){
                                                                String ip=ip_et.getText().toString();
                                                                String imgUrl="http://"+ip+":5000/pos?action=orderSignature&orderNo="+orderNo;
//                                                        Bitmap bitmap=ImageUtils.base64ToBitmap(base64Img);
//                                                        if(null!=bitmap){
//                                                            oder_sign_img.setImageBitmap(bitmap);
//                                                        }
                                                                Glide.with(MainActivity.this).load(imgUrl).into(oder_sign_img);
                                                            }
                                                        }
                                                    }

                                                }else if("transaction".equals(orderBean.getType())){
                                                    JSONObject jsonObject=JSONObject.parseObject(msg);
                                                    if(null!=jsonObject){
                                                        String resultCode=jsonObject.getString("resultCode");
                                                        String resultMsg=jsonObject.getString("resultMsg");

                                                        if("PENDING".equals(resultCode)){
                                                            if(null!=paymentTipDialog && paymentTipDialog.isShowing()){
                                                                paymentTipDialog.refreshMsg(resultMsg);
                                                            }
                                                        }else if("SUCCESS".equals(resultCode)){
                                                            paymentTipDialog.setPositiveButton(null);
                                                            String customerSignature= jsonObject.getString("customerSignature");
                                                            String paymentMethod= jsonObject.getString("paymentMethod");
//                                                    String cardType=jsonObject.getString("cardType");!TextUtils.isEmpty(cardType)?cardType:
                                                            paymentTipDialog.refreshSuccess(paymentMethod);
                                                            if("1".equalsIgnoreCase(customerSignature)){
                                                                paymentTipDialog.refreshMsg("Waiting for customer signature");
                                                            }
                                                        }else if("FAIL".equals(resultCode)){
                                                            if("Please Check the Token Whether to create".equals(resultMsg)){
                                                                paymentTipDialog.dismiss();
                                                                new PairingCodeDialog(MainActivity.this).builder().setPositiveButton(new BaseCallBack<String>() {
                                                                    @Override
                                                                    public void callSuccess(String code) {
                                                                        HantePOSAPI.pairingDevice(code,merchant_et.getText().toString());
                                                                    }

                                                                    @Override
                                                                    public void callFail(String code, String msg) {

                                                                    }
                                                                }).show();
                                                            }else{
                                                                paymentTipDialog.refreshFail(resultMsg);
                                                            }

                                                        }
                                                    }
                                                }else if("orderQuery".equals(orderBean.getType())){
                                                    JSONObject jsonObject=JSONObject.parseObject(msg);
                                                    if(null!=jsonObject){
                                                        String resultCode=jsonObject.getString("resultCode");
                                                        String returnCode=jsonObject.getString("returnCode");
                                                        String resultMsg=jsonObject.getString("resultMsg");
                                                        if("SUCCESS".equals(resultCode)){
                                                            double amount=0;
                                                            String totalAmount=jsonObject.getString("totalAmount");
                                                            String transactionId=jsonObject.getString("transactionId");
                                                            String paytime=jsonObject.getString("paytime");
                                                            String payStatus=jsonObject.getString("paystatus");
                                                            String paymentMethod=jsonObject.getString("paymentMethod");

                                                            if(!TextUtils.isEmpty(totalAmount)){
                                                                amount+=Double.parseDouble(totalAmount)*0.01;
                                                            }
                                                            if(null==orderInfoDialog){
                                                                orderInfoDialog=new OrderInfoDialog(MainActivity.this).builder().setPositiveButton(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {

                                                                    }
                                                                });
                                                            }
                                                            orderInfoDialog.dismiss();
                                                            orderInfoDialog.show(paymentMethod,
                                                                    "$"+amount,transactionId,paytime,payStatus);
                                                        }else{
                                                            if(tipDialog.isShowing()){
                                                                tipDialog.dismiss();
                                                            }
                                                            tipDialog.show(resultCode+":"+resultMsg);
                                                        }
                                                    }

                                                }else  if("refund".equals(orderBean.getType())){
                                                    JSONObject jsonObject=JSONObject.parseObject(msg);
                                                    if(null!=jsonObject){
                                                        String resultCode=jsonObject.getString("resultCode");
                                                        String resultMsg=jsonObject.getString("resultMsg");
                                                        paymentTipDialog.setPositiveButton(null);
                                                        if("SUCCESS".equals(resultCode)){
                                                            paymentTipDialog.refreshSuccess("");
                                                        }else if("FAIL".equals(resultCode)){
                                                            paymentTipDialog.refreshFail(resultMsg);
                                                        }
                                                    }
                                                }else  if("capture".equals(orderBean.getType())){
                                                    JSONObject jsonObject=JSONObject.parseObject(msg);
                                                    if(null!=jsonObject){
                                                        paymentTipDialog.setPositiveButton(null);
                                                        String resultCode=jsonObject.getString("resultCode");
                                                        String resultMsg=jsonObject.getString("resultMsg");
//                                                String paymentMethod=jsonObject.getString("paymentMethod");
                                                        if("SUCCESS".equals(resultCode)){
                                                            paymentTipDialog.refreshSuccess("");
                                                        }else if("FAIL".equals(resultCode)){
                                                            paymentTipDialog.refreshFail(resultMsg);
                                                        }
                                                    }
                                                }else  if("void".equals(orderBean.getType())){
                                                    JSONObject jsonObject=JSONObject.parseObject(msg);
                                                    if(null!=jsonObject){
                                                        paymentTipDialog.setPositiveButton(null);
                                                        String resultCode=jsonObject.getString("resultCode");
                                                        String resultMsg=jsonObject.getString("resultMsg");
                                                        if("SUCCESS".equals(resultCode)){
                                                            paymentTipDialog.refreshSuccess("");
                                                        }else if("FAIL".equals(resultCode)){
                                                            paymentTipDialog.refreshFail(resultMsg);
                                                        }
                                                    }
                                                }else{
                                                    JSONObject jsonObject=JSONObject.parseObject(msg);
                                                    String resultCode=jsonObject.getString("resultCode");
                                                    String resultMsg=jsonObject.getString("resultMsg");
                                                    if("SUCCESS".equals(resultCode)){

                                                    }else{
                                                        if(null!=resultCode){
                                                            if(tipDialog.isShowing()){
                                                                tipDialog.dismiss();
                                                            }
                                                            tipDialog.show(resultCode+":"+resultMsg);
                                                        }
                                                    }
                                                }
                                            }catch (RuntimeException e){
                                                e.printStackTrace();
                                            }

                                        }
                                    });

                                }
                            });
                }else {
                    ToastUtils.show("Connected");
                }

                break;
            case R.id.stop_connect_btn:
                HantePOSAPI.stopPOSConnect();
                break;
            case R.id.reconnection_time_btn:
                if(TextUtils.isEmpty(reconnection_time_et.getText().toString())){
                    Toast.makeText(this,"请输入断开重连时长(单位:秒)",Toast.LENGTH_SHORT).show();
                    return;
                }
                int time=Integer.parseInt(reconnection_time_et.getText().toString());
                HantePOSAPI.setReconnectionTime(time);
                break;
            case R.id.checkstand_btn://信用卡
            case R.id.auth_btn:
                if(TextUtils.isEmpty(et_amount.getText().toString())){
                    ToastUtils.show("Please enter the transaction amount");
                    return;
                }

                if(!TextUtils.isEmpty(et_tax_amount.getText().toString())){
                    taxAmount=Integer.parseInt(et_tax_amount.getText().toString());
                }

                if(!TextUtils.isEmpty(et_tip_amount.getText().toString())){
                    tipAmount=Integer.parseInt(et_tip_amount.getText().toString());
                }
                refreshPromptDialog("transaction");
                HantePOSAPI.sale(R.id.checkstand_btn==view.getId()?"SALE":"AUTH"
                        ,Integer.parseInt(et_amount.getText().toString()),taxAmount,tipAmount,"POS_PAYMENT",randomOrderNo(),"测试"
                        ,device_id_et.getText().toString(),merchant_et.getText().toString());
                break;
            case R.id.qrcode_collection_btn://二维码收款
                if(TextUtils.isEmpty(et_amount.getText().toString())){
                    ToastUtils.show("Please enter the transaction amount");
                    return;
                }

                if(!TextUtils.isEmpty(et_tax_amount.getText().toString())){
                    taxAmount=Integer.parseInt(et_tax_amount.getText().toString());
                }
                if(!TextUtils.isEmpty(et_tip_amount.getText().toString())){
                    tipAmount=Integer.parseInt(et_tip_amount.getText().toString());
                }
                refreshPromptDialog("transaction");
                HantePOSAPI.sale("SALE"
                        ,Integer.parseInt(et_amount.getText().toString()),taxAmount,tipAmount,"QR_CODE_PAYMENT",randomOrderNo(),"测试"
                        ,device_id_et.getText().toString(),merchant_et.getText().toString());

                break;
            case R.id.qrcode_scan_btn://扫码收款

                if(TextUtils.isEmpty(et_amount.getText().toString())){
                    ToastUtils.show("Please enter the transaction amount");
                    return;
                }

                if(!TextUtils.isEmpty(et_tax_amount.getText().toString())){
                    taxAmount=Integer.parseInt(et_tax_amount.getText().toString());
                }
                if(!TextUtils.isEmpty(et_tip_amount.getText().toString())){
                    tipAmount=Integer.parseInt(et_tip_amount.getText().toString());
                }
                refreshPromptDialog("transaction");
                HantePOSAPI.sale("SALE"
                        ,Integer.parseInt(et_amount.getText().toString()),taxAmount,tipAmount,"SCAN_CODE_PAYMENT",randomOrderNo(),"测试"
                        ,device_id_et.getText().toString(),merchant_et.getText().toString());


                break;
            case R.id.checkout_counter_btn://收银台
                if(TextUtils.isEmpty(et_amount.getText().toString())){
                    ToastUtils.show("Please enter the transaction amount");
                    return;
                }

                if(!TextUtils.isEmpty(et_tax_amount.getText().toString())){
                    taxAmount=Integer.parseInt(et_tax_amount.getText().toString());
                }
                if(!TextUtils.isEmpty(et_tip_amount.getText().toString())){
                    tipAmount=Integer.parseInt(et_tip_amount.getText().toString());
                }
                refreshPromptDialog("transaction");
                HantePOSAPI.sale("SALE"
                        ,Integer.parseInt(et_amount.getText().toString()),taxAmount,tipAmount,"HANTE_CASHIER",randomOrderNo(),"测试"
                        ,device_id_et.getText().toString(),merchant_et.getText().toString());
                break;
            case R.id.checkout_cancel_btn://取消
                HantePOSAPI.cancelTransaction(reconnection_order_et.getText().toString(),device_id_et.getText().toString(),merchant_et.getText().toString());
                break;
            case R.id.refresh_order_btn:

                break;
            case R.id.cancel_order_btn://cancelorder

                break;

            case R.id.refund_btn://退款
                if(TextUtils.isEmpty(refund_et.getText().toString())){
                    ToastUtils.show("Please enter the refund amount");
                    return;
                }

                if(TextUtils.isEmpty(order_tr_et.getText().toString())){
                    ToastUtils.show("Please enter the transactionId");
                    return;
                }

                refreshPromptDialog("refund");
                HantePOSAPI.refund(Integer.parseInt(refund_et.getText().toString()),order_tr_et.getText().toString()
                        ,device_id_et.getText().toString(),merchant_et.getText().toString());
                break;
            case R.id.capture_btn://capture
                if(TextUtils.isEmpty(order_tr_et.getText().toString())){
                    ToastUtils.show("Please enter the transactionId");
                    return;
                }

                if(TextUtils.isEmpty(capture_et.getText().toString())){
                    ToastUtils.show("Please enter the amount");
                    return;
                }
                refreshPromptDialog("capture");

                HantePOSAPI.capture(order_tr_et.getText().toString(),Integer.parseInt(capture_et.getText().toString()),Integer.parseInt(capture_tip_et.getText().toString()),
                        device_id_et.getText().toString(),merchant_et.getText().toString());
                break;

            case R.id.void_btn:
                if(TextUtils.isEmpty(order_tr_et.getText().toString())){
                    ToastUtils.show("Please enter the transactionId");
                    return;
                }
                refreshPromptDialog("void");
                HantePOSAPI.Void(order_tr_et.getText().toString(),device_id_et.getText().toString(),merchant_et.getText().toString());
                break;
            case R.id.query_order_btn:
                if(TextUtils.isEmpty(order_tr_et.getText().toString())){
                    ToastUtils.show("Please enter the transactionId");
                    return;
                }
                HantePOSAPI.orderQuery(order_tr_et.getText().toString(),device_id_et.getText().toString(),merchant_et.getText().toString());
                break;
            case R.id.checkout_ebt_btn:
                if(TextUtils.isEmpty(et_amount.getText().toString())){
                    ToastUtils.show("Please enter the transaction amount");
                    return;
                }

                if(!TextUtils.isEmpty(et_tax_amount.getText().toString())){
                    taxAmount=Integer.parseInt(et_tax_amount.getText().toString());
                }
                if(!TextUtils.isEmpty(et_tip_amount.getText().toString())){
                    tipAmount=Integer.parseInt(et_tip_amount.getText().toString());
                }

                HantePOSAPI.sale("EBT"
                        ,Integer.parseInt(et_amount.getText().toString()),taxAmount,tipAmount,"POS_PAYMENT",randomOrderNo(),"测试"
                        ,device_id_et.getText().toString(),merchant_et.getText().toString());
                break;
//            case R.id.merchant_sign_in_btn:
//                posSendMessage.setType("merchantSignIn");
//                posSendMessage.setMerchantNo(merchant_et.getText().toString());
//                posSendMessage.setPassword(merchant_pass_et.getText().toString());
//                sendMsg(posSendMessage);
//                break;
            case R.id.merchant_create_token_btn:
                if(!TextUtils.isEmpty(token_verify_code_et.getText().toString())){
                    HantePOSAPI.pairingDevice(token_verify_code_et.getText().toString(),merchant_et.getText().toString());
                }else {
                    ToastUtils.show("请输入 verify code");
                }
                break;
            case R.id.merchant_search_token_btn:
                HantePOSAPI.searchToken(merchant_et.getText().toString());
                break;
            case R.id.device_search_btn:
                if(!TextUtils.isEmpty(device_sn_et.getText().toString())){
                    refreshPosIp(device_sn_et.getText().toString());
                }else{
                    ToastUtils.show("请输入 Device SN");
                }
                break;
            case R.id.credit_card_sign_in_btn:
                HantePOSAPI.creditCardSignIn(device_sn_et.getText().toString(),merchant_et.getText().toString(),credit_card_sign_in_et.getText().toString());
                break;
            case R.id.query_oder_sign_btn:
                HantePOSAPI.orderSignature(randomOrderNo(),device_id_et.getText().toString(),merchant_et.getText().toString());
                break;
            case R.id.merchant_check_btn:
//                posSendMessage.setType("checkPOS");
//                sendMsg(posSendMessage);
                break;
            case R.id.checkout_close_countdown_btn:
                HantePOSAPI.config("transactionCountdown",0,device_id_et.getText().toString(),merchant_et.getText().toString());
                break;
//            case R.id.query_order_list_btn:
//                startActivityForResult(new Intent(MainActivity.this, TransactionRecordActivity.class),102);
//                break;
            case R.id.merchant_reset_pair_btn:
                HantePOSAPI.reSetPairDevice(merchant_et.getText().toString());
                break;
            case R.id.card_num_payment_btn:

                if(TextUtils.isEmpty(et_amount.getText().toString())){
                    ToastUtils.show("Please enter the transaction amount");
                    return;
                }

                if(!TextUtils.isEmpty(et_tax_amount.getText().toString())){
                    taxAmount=Integer.parseInt(et_tax_amount.getText().toString());
                }
                if(!TextUtils.isEmpty(et_tip_amount.getText().toString())){
                    tipAmount=Integer.parseInt(et_tip_amount.getText().toString());
                }

                HantePOSAPI.sale("SALE"
                        ,Integer.parseInt(et_amount.getText().toString()),taxAmount,tipAmount,"POS_KEY_IN",randomOrderNo(),"测试"
                        ,device_id_et.getText().toString(),merchant_et.getText().toString());
                break;
        }
    }

    private void refreshTransBtn(boolean b) {
        findViewById(R.id.checkstand_btn).setEnabled(b);
        findViewById(R.id.auth_btn).setEnabled(b);

        findViewById(R.id.qrcode_collection_btn).setEnabled(b);
        findViewById(R.id.qrcode_scan_btn).setEnabled(b);
        findViewById(R.id.checkout_cancel_btn).setEnabled(b);
        findViewById(R.id.query_order_list_btn).setEnabled(b);
        findViewById(R.id.query_oder_sign_btn).setEnabled(b);
        findViewById(R.id.checkout_close_countdown_btn).setEnabled(b);
        findViewById(R.id.card_num_payment_btn).setEnabled(b);
        findViewById(R.id.stop_connect_btn).setEnabled(b);
        findViewById(R.id.connect_btn).setEnabled(!b);

    }

    private void refreshPromptDialog(String type) {
        if(HantePOSAPI.isConnected()){

            if("transaction".equals(type)){
                if(null==paymentTipDialog){
                    paymentTipDialog=new PaymentTipDialog(MainActivity.this).builder();
                }

                double amount=0;
                if(!TextUtils.isEmpty(et_amount.getText().toString())){
                    amount+=Double.parseDouble(et_amount.getText().toString())*0.01;
                }

                if(!TextUtils.isEmpty(et_tax_amount.getText().toString())){
                    amount+=Double.parseDouble(et_tax_amount.getText().toString())*0.01;
                }

                if(!TextUtils.isEmpty(et_tip_amount.getText().toString())){
                    amount+=Double.parseDouble(et_tip_amount.getText().toString())*0.01;
                }
                paymentTipDialog.dismiss();
                paymentTipDialog.setPositiveButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HantePOSAPI.cancelTransaction(reconnection_order_et.getText().toString()
                                ,device_id_et.getText().toString(),merchant_et.getText().toString());
                    }
                }).show("$"+amount,"Wait for payment");

            }else if("refund".equals(type)){
                if(null==paymentTipDialog){
                    paymentTipDialog=new PaymentTipDialog(MainActivity.this).builder();
                }

                double amount=0;
                if(!TextUtils.isEmpty(refund_et.getText().toString())){
                    amount+=Double.parseDouble(refund_et.getText().toString())*0.01;
                }
                paymentTipDialog.setCanClose(false).show("$"+amount,"Wait for Refund");
            }else if("void".equals(type)){
                if(null==paymentTipDialog){
                    paymentTipDialog=new PaymentTipDialog(MainActivity.this).builder();
                }
                paymentTipDialog.setCanClose(false).show("","Wait for Void");
            }else if("capture".equals(type)){
                if(null==paymentTipDialog){
                    paymentTipDialog=new PaymentTipDialog(MainActivity.this).builder();
                }

                double amount=0;
                if(!TextUtils.isEmpty(capture_et.getText().toString())){
                    amount+=Double.parseDouble(capture_et.getText().toString())*0.01;
                }

                if(!TextUtils.isEmpty(capture_tip_et.getText().toString())){
                    amount+=Double.parseDouble(capture_tip_et.getText().toString())*0.01;
                }
                paymentTipDialog.setCanClose(false).show("$"+amount,"Wait for capture");
            }

            param_tv.setText(type);
            result_tv.setText("Response:");
            // HantePOSAPI.sentMessageV2(msg);
        }else {
            Toast.makeText(this,"Please connect to POS first",Toast.LENGTH_SHORT).show();
        }
    }




    private void setStatus(String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(customDialog.isShowing()){
                    customDialog.dismiss();
                }
                status_tv.setText(msg);
            }
        });
    }

    private String randomOrderNo(){
        StringBuilder orderSB=new StringBuilder();
        orderSB.append(System.currentTimeMillis());
        for(int i=0;i<3;i++){
            orderSB.append(new Random().nextInt(10));
        }
        reconnection_order_et.setText(orderSB.toString());
        return orderSB.toString() ;
    }


    private void refreshPosIp(String sn){
        if(!TextUtils.isEmpty(sn)){
            try{
                if(!customDialog.isShowing()){
                    customDialog.show();
                }
                RetrofitFactory.getInstance().queryPosIP(sn)
                        .subscribe(new SimpleObserver<BaseResponse<PosInfoResponse>>() {

                            @Override
                            public void onSubscribe(Disposable d) {
                                super.onSubscribe(d);
                            }


                            @Override
                            public void onNext(@NonNull BaseResponse<PosInfoResponse> response) {
                                customDialog.dismiss();
                                //请求成功
                                PosInfoResponse posInfo=response.getData();
                                if(null!=posInfo){
                                    String url=posInfo.getConnectUrl();
                                    if(!TextUtils.isEmpty(url)){
                                        String[] arr= url.split(":");
                                        if(arr.length==2){
                                            ip_et.setText(arr[0]);
                                        }
                                    }
                                    String merchantNo=posInfo.getUserNo();
                                    SpUtils.getInstance().save(Constant.DATE_USER_NO,merchantNo);
                                    merchant_et.setText(merchantNo);

                                    findViewById(R.id.connect_btn).setEnabled(true);


                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                e.printStackTrace();
                                customDialog.dismiss();
                            }
                        });
            }catch (RuntimeException e){
                e.printStackTrace();
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101){
            String token= SpUtils.getInstance().getString(Constant.DATE_DEVICE_TOKEN,"");
            if(!TextUtils.isEmpty(token)){
                try {
                    JSONObject jsonObject=JSONObject.parseObject(token);
                    String deviceId=jsonObject.getString("deviceId");
                    String key=jsonObject.getString("token");
                    if(!TextUtils.isEmpty(deviceId) && !TextUtils.isEmpty(key)){
                        device_id_et.setText(deviceId);
                        device_key_et.setText(key);
                    }
                }catch (RuntimeException e){
                    e.printStackTrace();
                }
            }

            String sn=SpUtils.getInstance().getString(Constant.CONFIG_DEVICE_SN,"");
            device_sn_et.setText(sn);

            refreshPosIp(sn);
        }else if(102==requestCode){
            if(null!=data){
                String transactionId=data.getStringExtra("transactionId");
                if(!TextUtils.isEmpty(transactionId)){
                    order_tr_et.setText(transactionId);
                }
            }
        }
    }

    @Override
    public void finish() {
        HantePOSAPI.stopPOSConnect();
        super.finish();
    }

    private void jumpCheck(){
        startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class),101);
    }

    /**
     * 获取系统剪贴板内容
     */
    public String getClipContent() {
        ClipboardManager manager = (ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
            // 获取剪贴板的剪贴数据集
            ClipData clipData = manager.getPrimaryClip();
            // 从数据集中获取（粘贴）第一条文本数据
            if (clipData != null && clipData.getItemCount() > 0) {
                return clipData.getItemAt(0).getText().toString();
            }
        }
        return "";
    }

    public double mul(double d1, double d2,int scale) { // 进行乘法运算
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        double d = b1.multiply(b2).doubleValue();
        return round(d,scale);
    }

    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}