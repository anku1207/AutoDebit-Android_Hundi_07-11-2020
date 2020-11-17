package com.uav.autodebit.usersservices.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uav.autodebit.Activity.Base_Activity;
import com.uav.autodebit.Activity.BillPayRequest;
import com.uav.autodebit.Activity.Enach_Mandate;
import com.uav.autodebit.Activity.SI_First_Data;
import com.uav.autodebit.Activity.UPI_Mandate;
import com.uav.autodebit.CustomDialog.BeforeRecharge;
import com.uav.autodebit.Interface.AlertSelectDialogClick;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.MandateAndRechargeInterface;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.usersservices.repo.MandateRevokeServiceWiseRepository;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.AuthServiceProviderVO;
import com.uav.autodebit.vo.CustomerServiceOperatorVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.OxigenTransactionVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import static android.view.View.GONE;

public class MandateDetailActivity extends Base_Activity {

    Button revokeMandate,changeMandate;
    TextView amount,createdAt,updatedAt,paymentCycle,tvService,tvHMAndateType,tvMandateType,tvHMandateTypeNumber,tvMandateTypeNumber;
    String dialogMessage;
    LinearLayout linearLayout;
    MandateRevokeServiceWiseRepository mandateRevokeServiceWiseRepository ;
    ScrollView scrollView;
    CustomerServiceOperatorVO mandateSwapingTypeResp;

    int csoId,serviceId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mandate_detail);
        if(getSupportActionBar()!=null) getSupportActionBar().hide();

        scrollView = findViewById(R.id.scrollview);
        scrollView.setVisibility(GONE);

        revokeMandate = findViewById(R.id.revokeMandate);
        changeMandate = findViewById(R.id.changeMandate);
        amount = findViewById(R.id.tvamount);
        createdAt = findViewById(R.id.tvCreatedAT);
        updatedAt = findViewById(R.id.tvlastUpdateDate);
        paymentCycle = findViewById(R.id.tvpaymnetScheduleDays);
        tvService = findViewById(R.id.tvService);
        linearLayout =(LinearLayout)findViewById(R.id.dynamicList);
        tvHMAndateType=  findViewById(R.id.tvHMAndateType);
        tvMandateType= findViewById(R.id.tvMandateType);
        tvHMandateTypeNumber=  findViewById(R.id.tvHMandateTypeNumber);
        tvMandateTypeNumber= findViewById(R.id.tvMandateTypeNumber);






        mandateRevokeServiceWiseRepository = new MandateRevokeServiceWiseRepository();
        csoId=getIntent().getIntExtra("csoid",0);
        serviceId=getIntent().getIntExtra("serviceTypeId",0);

        mandateRevokeServiceWiseRepository.getMandateDetails(MandateDetailActivity.this,csoId,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
            CustomerServiceOperatorVO customerVO =(CustomerServiceOperatorVO) s;
            if(customerVO!=null){
                try {
                    JSONObject respjsonObject  =new JSONObject(new Gson().toJson(customerVO));
                    JSONObject anonymous   = new JSONObject(customerVO.getAnonymousString());
                    JSONObject button = new JSONObject(customerVO.getAnonymousString1());
                    revokeMandate.setText(button.getString("btn1"));
                    changeMandate.setText(button.getString("btn2"));

                    if(anonymous.has("operatorIcon")&& !anonymous.getString("operatorIcon").isEmpty())
                        Picasso.with(MandateDetailActivity.this).load(anonymous.getString("operatorIcon")).into((ImageView) findViewById(R.id.ivOperator));

                    tvService.setText(new StringBuilder().append(anonymous.getString("operatorName")).append("\n \n").append(anonymous.getString("serviceName")).toString());


                    JSONArray jsonArray =anonymous.getJSONArray("mandateInputNumber");
                    if(linearLayout.getChildCount()>0)linearLayout.removeAllViews();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject =jsonArray.getJSONObject(i);
                        createDetailsList(linearLayout,jsonObject.getString("key"),jsonObject.getString("value"));

                    }

                    tvHMAndateType.setText(anonymous.getString("mandateType"));
                    tvMandateType.setText(anonymous.getString("mandateTypeName"));
                    tvHMandateTypeNumber.setText("Account/ CreditCard Number");
                    tvMandateTypeNumber.setText(anonymous.getString("mandateNumber"));

                    amount.setText(String.valueOf(customerVO.getAmount()));
                    createdAt.setText(Utility.getConvertedIntoDateFroms( customerVO.getCreatedAt()));//convertinto DATE
                    updatedAt.setText(Utility.getConvertedIntoDateFroms(customerVO.getLastUpdateDate()));//convertinto DATE
                    paymentCycle.setText(String.valueOf(customerVO.getPaymnetScheduleDays()));
                    dialogMessage = customerVO.getDialogMessage();
                    scrollView.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    ExceptionsNotification.ExceptionHandling(MandateDetailActivity.this , Utility.getStackTrace(e));
                }
            }
        }));

        revokeMandate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Utility.showDoubleButtonDialogConfirmation(new DialogInterface() {
                        @Override
                        public void confirm(Dialog dialog) {
                            Utility.dismissDialog(MandateDetailActivity.this,dialog);
                        }
                        @Override
                        public void modify(Dialog dialog) {
                            Utility.dismissDialog(MandateDetailActivity.this,dialog);
                            mandateRevokeServiceWiseRepository.getIsmandateRevoked(getIntent().getIntExtra("csoid",0),MandateDetailActivity.this,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
                                CustomerVO customerVO  = (CustomerVO) s;
                                CustomerServiceOperatorVO customerServiceOperatorVO = new CustomerServiceOperatorVO();
                                customerServiceOperatorVO.setAnonymousString(customerVO.getAnonymousString());
                                customerServiceOperatorVO.setDialogTitle(customerVO.getDialogTitle());
                                Intent intent = new Intent();
                                intent.putExtra("objectResult",(Serializable)customerServiceOperatorVO);
                                setResult(RESULT_OK,intent);
                                finish();

                            }));
                        }
                    }, MandateDetailActivity.this,dialogMessage,"", "Yes","No");

                }
            });

        changeMandate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new  MandateRevokeServiceWiseRepository().getMandateSwapingType(MandateDetailActivity.this,getIntent().getIntExtra("csoid",0),serviceId,
                        Integer.parseInt(Session.getCustomerId(MandateDetailActivity.this)),new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
                            try {
                                mandateSwapingTypeResp =(CustomerServiceOperatorVO)s;
                                JSONArray paymentTypeArray=new JSONArray(mandateSwapingTypeResp.getPaymentTypeObject());
                                if(paymentTypeArray.length()>1){
                                    Utility.showSelectPaymentTypeDialog(MandateDetailActivity.this, "Payment Type", mandateSwapingTypeResp.getPaymentTypeObject(), new AlertSelectDialogClick((position) -> {
                                        int selectPosition = Integer.parseInt(position);
                                        selectPaymentTypeWiseShowDialog(MandateDetailActivity.this,selectPosition,mandateSwapingTypeResp);
                                    }));
                                }else {
                                    selectPaymentTypeWiseShowDialog(MandateDetailActivity.this,paymentTypeArray.getJSONObject(0).getInt("id"),mandateSwapingTypeResp);
                                }
                            }catch (Exception e){
                                ExceptionsNotification.ExceptionHandling(MandateDetailActivity.this , Utility.getStackTrace(e));

                            }

                }));
            }
        });
    }


    public void selectPaymentTypeWiseShowDialog(Context context , Integer paymentTypeid , CustomerServiceOperatorVO customerServiceOperatorVO){
        if (paymentTypeid.equals(ApplicationConstant.BankMandatePayment)){
            // 07/05/2020
            BillPayRequest.showBankMandateOrSiMandateInfo(MandateDetailActivity.this,customerServiceOperatorVO.getBankMandateHtml(),new ConfirmationDialogInterface((ok)->{
                customerServiceOperatorVO.setAnonymousInteger(AuthServiceProviderVO.ENACHIDFC);
                setBankMandateOrRecharge(MandateDetailActivity.this,customerServiceOperatorVO);
            }));


        } else if(paymentTypeid.equals(ApplicationConstant.SIMandatePayment)) {
            // recharge on SI mandate
            BillPayRequest.showBankMandateOrSiMandateInfo(MandateDetailActivity.this,customerServiceOperatorVO.getSiMandateHtml(),new ConfirmationDialogInterface((ok)->{
                customerServiceOperatorVO.setAnonymousInteger(AuthServiceProviderVO.AUTOPE_PG);
                setBankMandateOrRecharge(MandateDetailActivity.this,customerServiceOperatorVO);
            }));
        }else if(paymentTypeid.equals(ApplicationConstant.UPIMandatePayment)) {
            // recharge on SI mandate
            BillPayRequest.showBankMandateOrSiMandateInfo(MandateDetailActivity.this,customerServiceOperatorVO.getUpiMandateHtml(),new ConfirmationDialogInterface((ok)->{
                customerServiceOperatorVO.setAnonymousInteger(AuthServiceProviderVO.AUTOPE_PG_UPI);
                setBankMandateOrRecharge(MandateDetailActivity.this,customerServiceOperatorVO);
            }));
        }
    }



    public  void setBankMandateOrRecharge(Context context , CustomerServiceOperatorVO customerServiceOperatorVO){
        OxigenTransactionVO oxigenTransactionVO = new OxigenTransactionVO();
        oxigenTransactionVO.setServiceId(serviceId);

        AuthServiceProviderVO authServiceProviderVO = new AuthServiceProviderVO();
        authServiceProviderVO.setProviderId(customerServiceOperatorVO.getAnonymousInteger());
        oxigenTransactionVO.setProvider(authServiceProviderVO);
        oxigenTransactionVO.setAnonymousInteger(customerServiceOperatorVO.getcSOId());

        BeforeRecharge.beforeRechargeAddMandate(context,oxigenTransactionVO,new MandateAndRechargeInterface((recharge)->{
            saveMandateSwaping(MandateDetailActivity.this,Integer.parseInt((String) recharge),customerServiceOperatorVO.getcSOId(),customerServiceOperatorVO.getAnonymousInteger(),false);
        }, (mandate)->{
            if(oxigenTransactionVO.getProvider().getProviderId()== AuthServiceProviderVO.AUTOPE_PG){
                startSIActivity(context,customerServiceOperatorVO,ApplicationConstant.PG_MANDATE);
            }else if(oxigenTransactionVO.getProvider().getProviderId()== AuthServiceProviderVO.ENACHIDFC){
                startBankMandateActivity(context,customerServiceOperatorVO);
            }else if(oxigenTransactionVO.getProvider().getProviderId()== AuthServiceProviderVO.AUTOPE_PG_UPI){
                startUPIActivity(context,customerServiceOperatorVO,ApplicationConstant.PG_MANDATE);
            }
        }));
    }

    public  void startBankMandateActivity(Context context , CustomerServiceOperatorVO  customerServiceOperatorVO){
        try {
            Intent intent = new Intent(context, Enach_Mandate.class);
            intent.putExtra("forresutl",true);
            intent.putExtra("selectservice",new ArrayList<Integer>(Arrays.asList(customerServiceOperatorVO.getServiceId())));
            intent.putExtra("id",customerServiceOperatorVO.getAnonymousInteger1());
            ((Activity) context).startActivityForResult(intent,ApplicationConstant.REQ_ENACH_MANDATE);
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(MandateDetailActivity.this , Utility.getStackTrace(e));
        }

    }

    public  void startSIActivity(Context context ,  CustomerServiceOperatorVO  customerServiceOperatorVO , String paymentType){
        try {
            Intent intent = new Intent(context, SI_First_Data.class);
            intent.putExtra("id",customerServiceOperatorVO.getAnonymousInteger1());
            intent.putExtra("amount",customerServiceOperatorVO.getAnonymousAmount());
            intent.putExtra("serviceId",customerServiceOperatorVO.getServiceId()+"");
            intent.putExtra("paymentType",paymentType);
            ((Activity) context).startActivityForResult(intent,ApplicationConstant.REQ_SI_MANDATE);
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(MandateDetailActivity.this , Utility.getStackTrace(e));
        }

    }

    public  void startUPIActivity(Context context ,  CustomerServiceOperatorVO  customerServiceOperatorVO , String paymentType){
        try {
            Intent intent = new Intent(context, UPI_Mandate.class);
            intent.putExtra("id",customerServiceOperatorVO.getAnonymousInteger1());
            intent.putExtra("amount",customerServiceOperatorVO.getAnonymousAmount());
            intent.putExtra("serviceId",customerServiceOperatorVO.getServiceId()+"");
            intent.putExtra("paymentType",paymentType);
            ((Activity) context).startActivityForResult(intent,ApplicationConstant.REQ_UPI_FOR_MANDATE);
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(MandateDetailActivity.this , Utility.getStackTrace(e));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){

            if(requestCode==ApplicationConstant.REQ_SI_MANDATE) {
                int SIMandateId = data.getIntExtra("mandateId", 0);
                Integer actionId = mandateSwapingTypeResp.getcSOId();
                if (SIMandateId != 0 && actionId != null) {
                    saveMandateSwaping(MandateDetailActivity.this,SIMandateId,actionId,AuthServiceProviderVO.AUTOPE_PG,true);
                } else {
                    Utility.showSingleButtonDialog(MandateDetailActivity.this, "Error !", Content_Message.error_message, false);
                }
            }else if(requestCode== ApplicationConstant.REQ_ENACH_MANDATE){
                boolean enachMandateStatus=data.getBooleanExtra("mandate_status",false);
                String mandateId=data.getStringExtra("bankMandateId");
                Integer actionId=mandateSwapingTypeResp.getcSOId();
                if(enachMandateStatus && actionId!=null && mandateId!=null){
                    saveMandateSwaping(MandateDetailActivity.this,Integer.parseInt(mandateId),actionId,AuthServiceProviderVO.ENACHIDFC,true);
                }else{
                    Utility.showSingleButtonDialog(MandateDetailActivity.this,"Alert",data.getStringExtra("msg"),false);
                }
            }else if(requestCode == ApplicationConstant.REQ_UPI_FOR_MANDATE){
                int SIMandateId = data.getIntExtra("mandateId", 0);
                Integer actionId = mandateSwapingTypeResp.getcSOId();
                if (SIMandateId != 0 && actionId != null) {
                    saveMandateSwaping(MandateDetailActivity.this,SIMandateId,actionId,AuthServiceProviderVO.AUTOPE_PG_UPI,true);
                } else {
                    Utility.showSingleButtonDialog(MandateDetailActivity.this, "Error !", Content_Message.error_message, false);
                }
            }
        }
    }



    public void saveMandateSwaping(Context context , int mandateId , int csoId ,int providerId ,boolean mandatetype){
       mandateRevokeServiceWiseRepository.saveMandateSwaping(context,mandateId,csoId,providerId,Integer.parseInt(Session.getCustomerId(context)),serviceId,mandatetype,new VolleyResponse((VolleyResponse.OnSuccess)(success)->{

           CustomerServiceOperatorVO customerServiceOperatorVO = (CustomerServiceOperatorVO) success;
           Intent intent = new Intent();
           intent.putExtra("objectResult",(Serializable)customerServiceOperatorVO);
           setResult(RESULT_OK,intent);
           finish();

       }));




    }



    private void createDetailsList(LinearLayout ll, String key, String v) {

        Typeface typeface = ResourcesCompat.getFont(MandateDetailActivity.this, R.font.poppinssemibold);

        LinearLayout et = new LinearLayout(new ContextThemeWrapper(MandateDetailActivity.this,R.style.confirmation_dialog_layout));

        TextView text = new TextView(new ContextThemeWrapper(MandateDetailActivity.this, R.style.confirmation_dialog_filed));
        text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
       // text.setTextColor(MandateDetailActivity.this.getResources().getColor(R.color.defaultTextColor));
        text.setPadding(8,8,8,8);
        text.setText(key);
        text.setMaxLines(1);
        text.setTextSize(14);
        text.setEllipsize(TextUtils.TruncateAt.END);
        text.setTypeface(typeface);

        TextView value = new TextView(new ContextThemeWrapper(MandateDetailActivity.this, R.style.confirmation_dialog_value_image));
        value.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
        //value.setTextColor(context.getResources().getColor(R.color.defaultTextColor));
        value.setPadding(8,8,8,8);
        value.setTextSize(12);
        value.setText(v);

        // et.addView(iv);

        et.addView(text);
        et.addView(value);
        ll.addView(et);

    }



    public void onClickBackButton(View view) {
        finish();
    }

}