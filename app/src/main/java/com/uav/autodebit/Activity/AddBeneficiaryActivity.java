package com.uav.autodebit.Activity;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.uav.autodebit.BO.BeneficiaryBO;
import com.uav.autodebit.CustomDialog.MyDialog;
import com.uav.autodebit.Interface.ConfirmationGetObjet;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.BeneficiaryPagerAdapter;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.BankAccountTypeVO;
import com.uav.autodebit.vo.BaseVO;
import com.uav.autodebit.vo.BeneAccVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerServiceOperatorVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddBeneficiaryActivity extends Base_Activity {

    EditText fullName,accountNumber,confirmAC,beneficiaryPhone,ifscCode;
    Button add;
    LinearLayout addcardlistlayout,layoutmainBanner;

    EditText [] editTexts;
    ViewPager viewPager;
    TabLayout tabLayout;
    ScrollView scrollView;
    Spinner account_type;

    List<BankAccountTypeVO> bankAccountTypeVOList;
    int accountTypeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beneficiary);

        if(getSupportActionBar()!=null) getSupportActionBar().hide();

        fullName = findViewById(R.id.fullName);
        accountNumber = findViewById(R.id.accountNumber);
        confirmAC = findViewById(R.id.confirmAC);
        beneficiaryPhone = findViewById(R.id.beneficiaryPhone);
        ifscCode = findViewById(R.id.ifscCode);
        add =findViewById(R.id.addBeneficiary);
        addcardlistlayout=findViewById(R.id.addcardlistlayout);
        tabLayout =findViewById(R.id.indicator);
        scrollView=findViewById(R.id.scrollView);
        layoutmainBanner=findViewById(R.id.layoutmainBanner);
        account_type=findViewById(R.id.account_type);

        bankAccountTypeVOList=new ArrayList<>();

        ifscCode.setFilters(new InputFilter[] {new InputFilter.AllCaps()});


        editTexts= new EditText[]{fullName, accountNumber, confirmAC, beneficiaryPhone, ifscCode};

        // add existing beneficiary on banner
        addBeneficiaryBanner();

        account_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    BankAccountTypeVO bankAccountTypeVO =bankAccountTypeVOList.get(i);
                    accountTypeId=bankAccountTypeVO.getAccountTypeId();
                }catch (Exception e){
                    ExceptionsNotification.ExceptionHandling(AddBeneficiaryActivity.this , Utility.getStackTrace(e));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllError(editTexts);
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
                if(ifscCode.length()!=11) {
                    ifscCode.setError("IFSC Code should be of 11 digits");
                    return;
                }
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
                if(accountTypeId==0){
                    Toast.makeText(AddBeneficiaryActivity.this, "Please select account Type", Toast.LENGTH_SHORT).show();
                    return;
                }
                addBeneficiary();
            }
        });
    }

    private void removeAllError(EditText [] editTexts){
        for(EditText ele:editTexts){
          ele.setError(null);
        }
    }


    private void addBeneficiaryBanner(){
        addcardlistlayout.removeAllViewsInLayout();
        getCustomerBeneficiaryList(AddBeneficiaryActivity.this,new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
            CustomerVO customerVO = (CustomerVO) success;
            try {

                Type collectionType = new TypeToken<List<BankAccountTypeVO>>() {}.getType();
                bankAccountTypeVOList =new Gson().fromJson(customerVO.getAnonymousString1(), collectionType);

                ArrayList<String> accountList=new ArrayList<>();
                for(int i=0;i< bankAccountTypeVOList.size();i++){
                    BankAccountTypeVO bankAccountTypeVO=(bankAccountTypeVOList.get(i));
                    accountList.add(bankAccountTypeVO.getAccountName());
                }

                ArrayAdapter adapter = new ArrayAdapter<String>(AddBeneficiaryActivity.this,R.layout.spinner_item,accountList);
                adapter.setDropDownViewResource(R.layout.spinner_item);
                account_type.setAdapter(adapter);

                ArrayList<BeneAccVO> beneAccVOS= (ArrayList<BeneAccVO>) new Gson().fromJson(customerVO.getAnonymousString(), new TypeToken<ArrayList<BeneAccVO>>() { }.getType());
                if(beneAccVOS!=null && beneAccVOS.size()>0){
                    showAddCardBtn();
                    getdata(beneAccVOS);
                }else{
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
                    scrollviewAnimationAndVisibility();
                }
            }catch (Exception e){
                e.printStackTrace();
                ExceptionsNotification.ExceptionHandling(AddBeneficiaryActivity.this, Utility.getStackTrace(e));
            }
        }));
    }


    @SuppressLint("ResourceType")
    public void showAddCardBtn(){
        TextView textView = Utility.getTextView(AddBeneficiaryActivity.this,"Add Beneficiary");
        textView.setPaintFlags(textView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        textView.setTextColor(getApplication().getResources().getColorStateList(R.drawable.text_change_color_blue_color_black));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        textView.setTag("addBeneficiaryBtn");

        Typeface typeface = ResourcesCompat.getFont(this, R.font.poppinssemibold);
        textView.setTypeface(typeface ,Typeface.BOLD);

        int childCount = layoutmainBanner.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = layoutmainBanner.getChildAt(i);
            if((v.getTag()!=null) && (v.getTag().equals("addBeneficiaryBtn"))){
                Utility.removeEle(v);
            }
        }
        layoutmainBanner.addView(textView);

        scrollView.setAnimation(null);
        scrollView.setVisibility(View.GONE);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.removeEle(textView);
                scrollviewAnimationAndVisibility();
            }
        });
    }

    private void scrollviewAnimationAndVisibility(){
        scrollView.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                1000,
                0);
        animate.setDuration(1000);
        animate.setFillAfter(true);
        scrollView.startAnimation(animate);

    }



    public void getdata(ArrayList<BeneAccVO> beneAccVOS){

        BeneficiaryPagerAdapter beneficiaryPagerAdapter =new BeneficiaryPagerAdapter(beneAccVOS,AddBeneficiaryActivity.this);

        viewPager=Utility.getViewPager(AddBeneficiaryActivity.this);
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager.setAdapter(beneficiaryPagerAdapter);
        viewPager.setPadding(0,0,0,0);
        tabLayout.setupWithViewPager(viewPager, false);
        Utility.disable_Tab(tabLayout);
        addcardlistlayout.addView(viewPager);

        View current = getCurrentFocus();
        if (current != null) current.clearFocus();


    }

    public void getCustomerBeneficiaryList(Context context , VolleyResponse volleyResponse){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = BeneficiaryBO.getCustomerBeneficiaryList();

        CustomerVO customerVO = new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));
        params.put("volley", new Gson().toJson(customerVO));
        connectionVO.setParams(params);
        Log.w("p2p_Request",new Gson().toJson(customerVO));

        VolleyUtils.makeJsonObjectRequest(this,connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object response) throws JSONException {
                JSONObject jsonObject = (JSONObject) response;
                CustomerVO customerVOResp = new Gson().fromJson(jsonObject.toString(),CustomerVO.class);

                if(customerVOResp.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerVOResp.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.alertDialog(AddBeneficiaryActivity.this,customerVOResp.getDialogTitle(),sb.toString(),"Ok");
                }else {
                    volleyResponse.onSuccess(customerVOResp);
                }
            }
        });
    }


    private void addBeneficiary() {

        HashMap<String, Object> params = new HashMap<String, Object>();

        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("penyDropOnAccount");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);

        BankAccountTypeVO bankAccountTypeVO = new BankAccountTypeVO();
        bankAccountTypeVO.setAccountTypeId(accountTypeId);

        BeneAccVO beneAccVO =new BeneAccVO();
        beneAccVO.setBeneNameRespose(fullName.getText().toString().trim());
        beneAccVO.setBeneficialAccountNum(confirmAC.getText().toString().trim());
        beneAccVO.setBeneficialPhone(beneficiaryPhone.getText().toString().trim());
        beneAccVO.setBeneficialIFSCcode(ifscCode.getText().toString().trim());
        beneAccVO.setAnonymousInteger(Integer.parseInt(Session.getCustomerId(AddBeneficiaryActivity.this)));
        beneAccVO.setBankAccountTypeVO(bankAccountTypeVO);
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
                                   openPaymentDialog(accVO,null,true,true);
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

    public void openPaymentDialog(BeneAccVO accVO , View view ,boolean backBtnDismiss,boolean refreshPage){
        if(refreshPage)addBeneficiaryBanner();
        MyDialog.sendPaymentDialog(AddBeneficiaryActivity.this,backBtnDismiss,view,accVO,new ConfirmationGetObjet((ConfirmationGetObjet.OnOk)(ok)->{
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
                    intent.putExtra(AutopePayment.EXTRAS_TXN_ID,jsonObject.getInt("p2pTxnId")+"");
                    intent.putExtra(AutopePayment.EXTRAS_TITLE,"Payment");
                    intent.putExtra(AutopePayment.EXTRAS_REFRESH_PAGE,refreshPage);
                    intent.putExtra(AutopePayment.EXTRAS_SERVICE_TYPE_ID,ApplicationConstant.MoneyTransfer);

                    startActivityForResult(intent,ApplicationConstant.REQ_AUTOPE_PAYMENT_RESULT);
                }catch (Exception e){
                    ExceptionsNotification.ExceptionHandling(AddBeneficiaryActivity.this, Utility.getStackTrace(e));
                }
            }));
        }));

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==ApplicationConstant.REQ_AUTOPE_PAYMENT_RESULT){
                if(data!=null){
                    if(data.getBooleanExtra(AutopePayment.EXTRAS_REFRESH_PAGE,false))addBeneficiaryBanner();
                    BaseVO baseVO  =new Gson().fromJson(data.getStringExtra("data"),BaseVO.class);
                    if(!baseVO.getStatusCode().equals("200")){
                        MyDialog.txnDialogAutope(AddBeneficiaryActivity.this,baseVO.getDialogTitle(),baseVO.getErrorMsgs().get(0),false,Utility.getTransactionWishStyleForTxnDialog(AddBeneficiaryActivity.this,false),new ConfirmationGetObjet((ConfirmationGetObjet.OnOk)(ok)->{
                            ((Dialog)ok).dismiss();
                        }));
                    }else {
                        MyDialog.txnDialogAutope(AddBeneficiaryActivity.this,baseVO.getDialogTitle(),baseVO.getDialogMessage(),false,Utility.getTransactionWishStyleForTxnDialog(AddBeneficiaryActivity.this,true),new ConfirmationGetObjet((ConfirmationGetObjet.OnOk)(ok)->{
                            ((Dialog)ok).dismiss();
                        }));
                    }

                }
            }
        }
    }

    private void p2pInitiateAmount(Context context, BeneAccVO beneAccVO , VolleyResponse volleyResponse) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = BeneficiaryBO.p2pInitiateAmount();
        params.put("volley", new Gson().toJson(beneAccVO));
        Log.w("p2pInitiateAmount",new Gson().toJson(beneAccVO));
        connectionVO.setParams(params);

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