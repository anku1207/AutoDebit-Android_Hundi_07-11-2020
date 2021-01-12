package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uav.autodebit.BO.MetroBO;
import com.uav.autodebit.BO.OxigenPlanBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.AutoPeTransactionVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Dmrc_Card_Topup_BillPay extends AppCompatActivity implements View.OnClickListener {
    LinearLayout fetchbilllayout;
    Button bill_pay;
    AutoPeTransactionVO autoPeTransactionVOResp;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmrc__card__topup__bill_pay);
        getSupportActionBar().hide();
        fetchbilllayout=findViewById(R.id.fetchbilllayout);
        bill_pay=findViewById(R.id.bill_pay);
        title=findViewById(R.id.title);
        bill_pay.setOnClickListener(this);

        try {
            JSONObject jsonObject =new JSONObject(getIntent().getStringExtra(ApplicationConstant.NOTIFICATION_ACTION));

            if(jsonObject.has("value") &&   jsonObject.isNull("value")){
                Utility.showSingleButtonDialog(this,"Error !", Content_Message.error_message,true);
            }else{
                getFetchTopUpDetails(jsonObject.getInt("value") ,new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
                    try {
                        autoPeTransactionVOResp = (AutoPeTransactionVO) success;
                        title.setText(autoPeTransactionVOResp.getDialogTitle());

                        JSONArray jsonArray=new JSONArray(autoPeTransactionVOResp.getAnonymousString());
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject billJson=jsonArray.getJSONObject(i);
                            LinearLayout et1 = new LinearLayout(new ContextThemeWrapper(this,R.style.confirmation_dialog_layout));
                            et1.setPadding(Utility.getPixelsFromDPs(this,10),Utility.getPixelsFromDPs(this,10),Utility.getPixelsFromDPs(this,10),Utility.getPixelsFromDPs(this,10));

                            TextView text = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_filed));
                            text.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
                            text.setText(billJson.getString("key"));
                            text.setMaxLines(1);
                            text.setEllipsize(TextUtils.TruncateAt.END);
                            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


                            TextView value = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_value));
                            value.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                            value.setText(billJson.getString("value"));
                            value.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                            et1.addView(text);
                            et1.addView(value);
                            fetchbilllayout.addView(et1);
                        }
                        bill_pay.setVisibility(View.VISIBLE);
                    }catch (Exception e){
                        ExceptionsNotification.ExceptionHandling(Dmrc_Card_Topup_BillPay.this , Utility.getStackTrace(e));
                    }
                }));
            }
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(Dmrc_Card_Topup_BillPay.this , Utility.getStackTrace(e));
        }
    }


    public static void startDirectPaymentActivity(Context context , AutoPeTransactionVO autoPeTransactionVO ){
        try {
            Intent intent = new Intent(context,DirectPaymentActivity.class);
            intent.putExtra(DirectPaymentActivity.EXTRAS_ID,autoPeTransactionVO.getAptxnId());
            intent.putExtra(DirectPaymentActivity.EXTRAS_ENCRYPTED_VALUE,autoPeTransactionVO.getEncryptedValue());
            intent.putExtra(DirectPaymentActivity.EXTRAS_DIRECT_PAYMENT,1);
            intent.putExtra(DirectPaymentActivity.EXTRAS_TITLE,ApplicationConstant.DIRECT_PAYMENT_TITLE);
            ((Activity) context).startActivityForResult(intent,ApplicationConstant.REQ_DIRECT_PAYMENT_RESULT);
        }catch (Exception e ){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }

    }
    private  void getFetchTopUpDetails(int id,VolleyResponse volleyResponse)throws Exception{
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MetroBO.getMyDmrcTopUpDetails();

        AutoPeTransactionVO autoPeTransactionVORequest = new AutoPeTransactionVO();
        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(Dmrc_Card_Topup_BillPay.this)));
        autoPeTransactionVORequest.setAptxnId(id);
        autoPeTransactionVORequest.setCustomer(customerVO);
        Gson gson=new Gson();
        String json = gson.toJson(autoPeTransactionVORequest);
        params.put("volley", json);
        connectionVO.setParams(params);


        VolleyUtils.makeJsonObjectRequest(this, connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Log.w("respele",response.toString());
                AutoPeTransactionVO oxigenTransactionVO = gson.fromJson(response.toString(), AutoPeTransactionVO.class);
                if(oxigenTransactionVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) oxigenTransactionVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(Dmrc_Card_Topup_BillPay.this,"Error !",sb.toString(),true);
                    bill_pay.setVisibility(View.GONE);
                }else {
                    volleyResponse.onSuccess(oxigenTransactionVO);
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.bill_pay){
            startDirectPaymentActivity(Dmrc_Card_Topup_BillPay.this,autoPeTransactionVOResp);
        }

    }
}