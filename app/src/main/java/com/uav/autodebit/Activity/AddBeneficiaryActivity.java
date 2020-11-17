package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uav.autodebit.BO.BeneficiaryBO;
import com.uav.autodebit.CustomDialog.MyDialog;
import com.uav.autodebit.Interface.ConfirmationGetObjet;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.BaseVO;
import com.uav.autodebit.vo.BeneAccVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.StatusVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.uav.autodebit.vo.StatusVO.*;

public class AddBeneficiaryActivity extends Base_Activity {

    EditText fullName,accountNumber,confirmAC,beneficiaryPhone,ifscCode;
    Button add;

    LinearLayout addcardlistlayout,layoutmainBanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beneficiary);

        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();

        fullName = findViewById(R.id.fullName);
        accountNumber = findViewById(R.id.accountNumber);
        confirmAC = findViewById(R.id.confirmAC);
        beneficiaryPhone = findViewById(R.id.beneficiaryPhone);
        ifscCode = findViewById(R.id.ifscCode);
        add =findViewById(R.id.addBeneficiary);
        addcardlistlayout=findViewById(R.id.addcardlistlayout);

        // add existing beneficiary on banner
        addBeneficiaryBanner();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(accountNumber.getText().toString().isEmpty()){
                    accountNumber.setError("Account Number cannot be left blank");
                    return;
                }
                if(accountNumber.length()<7 || accountNumber.length()>18) {
                    accountNumber.setError("Account Number  should be of 7-18 digits");
                    return;
                }
                if(confirmAC.getText().toString().isEmpty()){
                    confirmAC.setError("Confirm Account Number cannot be left blank");
                    return;
                }
                String cac = confirmAC.getText().toString();
                if(!(accountNumber.getText().toString()).equalsIgnoreCase(cac) ){
                    confirmAC.setError("Your Account Number and Confirm Account Number should be same");
                    return;
                }
                if(ifscCode.getText().toString().isEmpty()){
                    ifscCode.setError("IFSC Code cannot be left blank");
                    return;
                }
                /*if(ifscCode.length()!=11) {
                    ifscCode.setError("IFSC Code should be of 11 digits");
                    return;
                }*/
                if(fullName.getText().toString().isEmpty()){
                    fullName.setError("Full Name cannot be left blank");
                    return;
                }
                if(fullName.length()<3) {
                    fullName.setError("Full Name should be more than 2 letters");
                    return;
                }
                if(beneficiaryPhone.getText().toString().isEmpty()) {
                    beneficiaryPhone.setError("Mobile number cannot be left blank");
                    return;
                }
                if (!beneficiaryPhone.getText().toString().trim().equals("") &&  Utility.validatePattern(beneficiaryPhone.getText().toString().trim(), ApplicationConstant.MOBILENO_VALIDATION)!=null){
                    beneficiaryPhone.setError(Utility.validatePattern(beneficiaryPhone.getText().toString().trim(), ApplicationConstant.MOBILENO_VALIDATION));
                    return;
                }
                addBeneficiary();
            }
        });
    }


    private void addBeneficiaryBanner(){

        CardView cardView =Utility.getCardViewStyle(this);
        ImageView imageView =Utility.getImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Picasso.with(this)
                .load("http://autope.in/images/apk/1577709175082.jpeg")
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //do smth when picture is loaded successfully
                    }
                    @Override
                    public void onError() {
                    }
                });
        cardView.addView(imageView);
        addcardlistlayout.addView(cardView);
    }


    private void addBeneficiary() {

        HashMap<String, Object> params = new HashMap<String, Object>();

        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("penyDropOnAccount");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);

        BeneAccVO beneAccVO =new BeneAccVO();
        beneAccVO.setBeneNameRespose(fullName.getText().toString().trim());
        beneAccVO.setBeneficialAccountNum(confirmAC.getText().toString().trim());
        beneAccVO.setBeneficialPhone(beneficiaryPhone.getText().toString().trim());
        beneAccVO.setBeneficialIFSCcode(ifscCode.getText().toString().trim());
        beneAccVO.setAnonymousInteger(Integer.parseInt(Session.getCustomerId(AddBeneficiaryActivity.this)));
        String json = new Gson().toJson(beneAccVO);

        Log.w("request",json);

        params.put("volley", json);
        connectionVO.setParams(params);

        VolleyUtils.makeJsonObjectRequest(this,connectionVO, new VolleyResponseListener() {

            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(Object response) throws JSONException {
                JSONObject jresponse = (JSONObject) response;
                Gson gson = new Gson();
                BeneAccVO accVO = gson.fromJson(jresponse.toString(), BeneAccVO.class);

                if(accVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) accVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.alertDialog(AddBeneficiaryActivity.this,accVO.getDialogTitle(),sb.toString(),"Ok");
                }else {
                       Utility.confirmationDialogTextType(new DialogInterface() {
                           @Override
                           public void confirm(Dialog dialog) {
                               dialog.dismiss();
                               sendConfirmationMessageToServer(AddBeneficiaryActivity.this,accVO,new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
                                   BeneAccVO accVO = (BeneAccVO) success;
                                   MyDialog.sendPaymentDialog(AddBeneficiaryActivity.this,true,accVO,new ConfirmationGetObjet((ConfirmationGetObjet.OnOk)(ok)->{
                                       HashMap<String,Object> objectHashMap = (HashMap<String, Object>) ok;
                                       ((Dialog)objectHashMap.get("dialog")).dismiss();
                                       BeneAccVO beneAccVO1 = (BeneAccVO) objectHashMap.get("data");
                                       beneAccVO1.setAnonymousInteger(Integer.parseInt(Session.getCustomerId(AddBeneficiaryActivity.this)));
                                       p2pInitiateAmount(AddBeneficiaryActivity.this,beneAccVO1,new VolleyResponse((VolleyResponse.OnSuccess)(p2pInitiateAmount_SuccessResp)->{
                                           JSONObject jsonObject = (JSONObject) p2pInitiateAmount_SuccessResp;
                                           try {
                                               Intent intent = new Intent(AddBeneficiaryActivity.this,AutopePayment.class);
                                               intent.putExtra(AutopePayment.EXTRAS_URL,jsonObject.getString("url"));
                                               intent.putExtra(AutopePayment.EXTRAS_FAIL_URL,jsonObject.getString("fail"));
                                               intent.putExtra(AutopePayment.EXTRAS_SUCCESS_URL,jsonObject.getString("success"));
                                               intent.putExtra(AutopePayment.EXTRAS_TITLE,"Payment");
                                               startActivity(intent);
                                           }catch (Exception e){
                                               ExceptionsNotification.ExceptionHandling(AddBeneficiaryActivity.this, Utility.getStackTrace(e));
                                           }
                                       }));
                                   }));
                               }));
                           }
                           @Override
                           public void modify(Dialog dialog) {
                               dialog.dismiss();
                               finish();
                           }
                       },AddBeneficiaryActivity.this,new JSONArray(accVO.getAnonymousString()),null,accVO.getDialogTitle(),new String[]{"Yes","No"});
                }
            }
        });
    }


    private void p2pInitiateAmount(Context context, BeneAccVO beneAccVO , VolleyResponse volleyResponse) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = BeneficiaryBO.p2pInitiateAmount();
        params.put("volley", new Gson().toJson(beneAccVO));
        connectionVO.setParams(params);

        Log.w("p2p_Request",new Gson().toJson(beneAccVO));

        VolleyUtils.makeJsonObjectRequest(this,connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object response) throws JSONException {
                JSONObject jsonObject = (JSONObject) response;

                if(jsonObject.getString("statusCode").equals("400")){
                    ArrayList error = (ArrayList) jsonObject.getJSONArray("errorMsgs").get(0);
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.alertDialog(AddBeneficiaryActivity.this,jsonObject.getString("dialogTitle"),sb.toString(),"Ok");
                }else {
                    volleyResponse.onSuccess(jsonObject);
                }
            }
        });
    }

    private void sendConfirmationMessageToServer(Context context, BeneAccVO beneAccVO , VolleyResponse volleyResponse) {
        HashMap<String, Object> params = new HashMap<String, Object>();

        ConnectionVO connectionVO = BeneficiaryBO.confirmationAfterPenyDrop();

        BeneAccVO accVO =new BeneAccVO();
        accVO.setBeneficialAccountId(beneAccVO.getBeneficialAccountId());
        accVO.setBeneNameRespose(beneAccVO.getBeneNameRespose());
        accVO.setAnonymousInteger(Integer.parseInt(Session.getCustomerId(context)));

        params.put("volley", new Gson().toJson(accVO));
        connectionVO.setParams(params);

        VolleyUtils.makeJsonObjectRequest(this,connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object response) throws JSONException {
                JSONObject jsonObject = (JSONObject) response;
                Gson gson = new Gson();
                BeneAccVO accVO = gson.fromJson(jsonObject.toString(), BeneAccVO.class);
                if(accVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) accVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.alertDialog(AddBeneficiaryActivity.this,accVO.getDialogTitle(),sb.toString(),"Ok");
                }else {
                  volleyResponse.onSuccess(accVO);
                }
            }
        });
    }

    public void onClickBackButton(View view) {
        finish();
    }
}