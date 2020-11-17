package com.uav.autodebit.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uav.autodebit.DMRC.DMRCApi;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.ConfirmationGetObjet;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ErrorMsg;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.BaseVO;
import com.uav.autodebit.vo.CityVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.SBICustomerCardVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SbiCreditCardApplyActivity extends Base_Activity{

    Spinner spAge,spIncome,spProfession;
    EditText pincode,city;
    TextView name,mobileNo;
    Button apply;
    CityVO cityVO;
    CheckBox checkBox;
    String[] ageRange,professions,incomeRange;
    LinearLayout main_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sbi_credit_cad_apply);
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();

        spAge = findViewById(R.id.spAge);
        spIncome =findViewById(R.id.income);
        spProfession = findViewById(R.id.profession);
        apply = findViewById(R.id.applyForCard);

        name = findViewById(R.id.username);
        mobileNo = findViewById(R.id.mobNo);
        pincode = findViewById(R.id.pin);
        city = findViewById(R.id.city);
        checkBox = findViewById(R.id.consent);
        main_container=findViewById(R.id.main_container);

        cityVO  =new CityVO();

        ((ImageView)findViewById(R.id.back_activity_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pincode.setInputType(InputType.TYPE_CLASS_NUMBER);
        pincode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //set the row background to a different color
                    if (pincode.length() < 6) {
                        pincode.setError(Utility.getErrorSpannableString(SbiCreditCardApplyActivity.this, ErrorMsg.Pincode_6_characters_Error_Message));
                        city.setText("");
                    }
                }

            }
        });

        pincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (pincode.length() == 6) {
                    DMRCApi.pinCodebycity(SbiCreditCardApplyActivity.this,pincode.getText().toString().trim(),new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
                        cityVO = (CityVO) success;
                        city.setText(cityVO.getCityName());
                        city.setError(null);
                        InputMethodManager imm = (InputMethodManager)SbiCreditCardApplyActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(pincode.getWindowToken(), 0);
                    },(VolleyResponse.OnError)(error)->{
                        city.setText("");
                        pincode.setError(error);
                    }));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        CustomerVO customerVO = new Gson().fromJson(Session.getSessionByKey(SbiCreditCardApplyActivity.this,Session.CACHE_CUSTOMER), CustomerVO.class);
        if(customerVO!=null){
            name.setText(customerVO.getName());
            mobileNo.setText(customerVO.getMobileNumber());
            pincode.setText(customerVO.getPincode());
            city.setText(customerVO.getCity().getCityName());
        }

         ageRange =this.getResources().getStringArray(R.array.agerange_arrays);
         professions =this.getResources().getStringArray(R.array.profession_arrays);
         incomeRange =this.getResources().getStringArray(R.array.incomerange_arrays);

        spAge.setAdapter(new ArrayAdapter<String>(SbiCreditCardApplyActivity.this, R.layout.spinner_item,ageRange) );
        spProfession.setAdapter(new ArrayAdapter<String>(SbiCreditCardApplyActivity.this, R.layout.spinner_item,professions) );
        spIncome.setAdapter(new ArrayAdapter<String>(SbiCreditCardApplyActivity.this, R.layout.spinner_item,incomeRange));


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().isEmpty()){
                    name.setError("Name cannot be left blank.");
                    return;
                }
                if(name.getText().toString().length()<3){
                    name.setError("Name should be more than 2 letters.");
                    return;

                }
                if(mobileNo.getText().toString().isEmpty()){
                    mobileNo.setError("Mobile number cannot be left blank.");
                    return;
                }
                if(mobileNo.getText().toString().length()!=10){
                    mobileNo.setError("Mobile number should be of 10 digits.");
                    return;
                }
                if(pincode.getText().toString().isEmpty()){
                    pincode.setError("pincode cannot be left blank.");
                    return;
                }
                if(pincode.getText().toString().length()!=6){
                    pincode.setError("Pincode should be of 6 digit.");
                    return;
                }
                if(city.getText().toString().isEmpty()){
                    city.setError("City cannot be left blank.");
                    return;
                }
                if(spAge.getSelectedItemPosition()==0){
                    ((TextView)findViewById(R.id.ageError)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.ageError)).setText("* Kindly,select an age group.");
                    return;
                }
                if(spProfession.getSelectedItemPosition()==0){
                    ((TextView)findViewById(R.id.professionError)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.professionError)).setText("* Kindly,select your profession.");
                    return;
                }
                if(spIncome.getSelectedItemPosition()==0){
                    ((TextView)findViewById(R.id.incomeError)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.incomeError)).setText("* Kindly,select an income group.");
                    return;
                }
                if(!checkBox.isChecked()) {
                    ((TextView)findViewById(R.id.consentError)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.consentError)).setText("* Kindly,Accept term & condition.");
                    return;
                }

                applyForCard();

            }
        });


        spAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position>0){
                        ((TextView)findViewById(R.id.ageError)).setVisibility(View.GONE);

                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spIncome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)
                {
                    ((TextView)findViewById(R.id.incomeError)).setVisibility(View.GONE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spProfession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    ((TextView)findViewById(R.id.professionError)).setVisibility(View.GONE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void applyForCard() {
        HashMap<String, Object> params = new HashMap<String, Object>();

        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("saveSBICustomerCards");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        //send customerId
        SBICustomerCardVO sbiCustomerCardVO = new SBICustomerCardVO();
        CustomerVO customerVO = new Gson().fromJson(Session.getSessionByKey(SbiCreditCardApplyActivity.this,Session.CACHE_CUSTOMER), CustomerVO.class);
        sbiCustomerCardVO.setCustomer(customerVO);
        if(cityVO==null ||cityVO.getCityName().equals(""))  {
            sbiCustomerCardVO.setCity(customerVO.getCity());
        }
        else{
            sbiCustomerCardVO.setCity(cityVO);

        }
        sbiCustomerCardVO.setAgeGroup(ageRange[spAge.getSelectedItemPosition()]);
        sbiCustomerCardVO.setProfession(professions[spProfession.getSelectedItemPosition()]);
        sbiCustomerCardVO.setGrossAnnualIncome(incomeRange[spIncome.getSelectedItemPosition()]);
        sbiCustomerCardVO.setCurrentPincode(pincode.getText().toString().trim());

        String json = new Gson().toJson(sbiCustomerCardVO);

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
                BaseVO baseVO = gson.fromJson(jresponse.toString(), BaseVO.class);

                if(baseVO.getStatusCode().equals("400")){
                    //VolleyUtils.furnishErrorMsg(  "Fail" ,response, MainActivity.this);

                    ArrayList error = (ArrayList) baseVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(SbiCreditCardApplyActivity.this,baseVO.getDialogTitle(),sb.toString(),false);
                }else {
                    if(main_container.getChildCount()>0) main_container.removeAllViews();
                    if(baseVO.getImage()!=null){
                        getWebviewWithImage(SbiCreditCardApplyActivity.this,baseVO.getImage(),main_container,new ConfirmationGetObjet((ConfirmationGetObjet.OnOk)(ok)->{

                        }));
                    }else {
                        finish();
                    }
                }
            }
        });
    }

    public void getWebviewWithImage(Context context , String loadUrl , LinearLayout linearLayout , ConfirmationGetObjet confirmationGetObjet){
        WebView webview = new WebView(SbiCreditCardApplyActivity.this);
        webview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        ProgressDialog progressBar = ProgressDialog.show(context, null, " Please wait...", false, false);;
        linearLayout.addView(webview);

        WebSettings ws = webview.getSettings();
        ws.setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(!((Activity)context).isFinishing() &&  progressBar!=null && !progressBar.isShowing()){
                    try {
                        progressBar.show();
                    }catch (Exception e){
                    }
                }
                if(newProgress==100){
                    Utility.dismissDialog(context, progressBar);
                }
            }
        });

        webview.setVerticalScrollBarEnabled(false);
        webview.setHorizontalScrollBarEnabled(false);
        webview.getSettings().setBuiltInZoomControls(false);

        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setInitialScale(1);
        webview.getSettings().setUseWideViewPort(true);

        webview.loadUrl(loadUrl); //receiptUrl
        webview.addJavascriptInterface(new Object() {
            @JavascriptInterface // For API 17+
            public void performClick(String message) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (message.equalsIgnoreCase("ok")) {
                            confirmationGetObjet.onOk("ok");
                        } else if (message.equalsIgnoreCase("cancel")) {
                            confirmationGetObjet.onCancel("cancel");
                        }
                    }
                });
            }
        }, "ok");
    }


    public void onClickBackButton(View view) {
        finish();
    }


}