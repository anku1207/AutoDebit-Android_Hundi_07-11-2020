package com.uav.autodebit.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uav.autodebit.BO.OxigenPlanBO;
import com.uav.autodebit.CustomDialog.BeforeRecharge;
import com.uav.autodebit.CustomDialog.MyDialog;
import com.uav.autodebit.Interface.AlertSelectDialogClick;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.ConfirmationGetObjet;
import com.uav.autodebit.Interface.MandateAndRechargeInterface;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.constant.ErrorMsg;
import com.uav.autodebit.constant.GlobalApplication;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.override.DrawableClickListener;
import com.uav.autodebit.override.UAVEditText;
import com.uav.autodebit.permission.PermissionHandler;
import com.uav.autodebit.permission.PermissionUtils;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.CustomTextWatcherLengthAction;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.AuthServiceProviderVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.OxigenPlanVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Mobile_Prepaid_Recharge_Service extends Base_Activity implements View.OnClickListener, PermissionUtils.PermissionResultCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    UAVEditText mobilenumber;
    EditText operator, amount;
    ImageView back_activity_button;
    String operatorcode, regioncode, operatorname, regionname = null;
    TextView browseplan;
    Button proceed;
    String serviceid;
    PermissionUtils permissionUtils;
    static OxigenTransactionVO oxigenValidateResponce;

    CardView fetchbillcard;
    LinearLayout fetchbilllayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_mobile__prepaid__recharge__service);

        mobilenumber = findViewById(R.id.mobilenumber);
        operator = findViewById(R.id.operator);
        amount = findViewById(R.id.amount);
        proceed = findViewById(R.id.proceed);
        browseplan = findViewById(R.id.browseplan);
        fetchbillcard=findViewById(R.id.fetchbillcard);
        fetchbilllayout=findViewById(R.id.fetchbilllayout);
        permissionUtils = new PermissionUtils(Mobile_Prepaid_Recharge_Service.this);

        browseplan.setVisibility(View.GONE);
        back_activity_button = findViewById(R.id.back_activity_button1);
        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        proceed.setOnClickListener(this);
        browseplan.setOnClickListener(this);
        operator.setClickable(false);


        //change 24-02-2021
        changeEdittextValue(operator);
        changeEdittextValue(amount);
        changeEdittextValue(mobilenumber);


        Intent intent = getIntent();
        serviceid = intent.getStringExtra("serviceid");

        //create offers and recent fragment
      /*  View offer_fragment;
        offer_fragment=findViewById(R.id.offer_fragment);
        Offers_recent_Fragment.createOfferAndRecentLayout(Mobile_Prepaid_Recharge_Service.this);
        offer_fragment.setVisibility(View.VISIBLE);
*/
        Drawable drawable = getResources().getDrawable(R.drawable.contacts);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.appbar));

        mobilenumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mobile, 0, R.drawable.contacts, 0);
        mobilenumber.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        if (mobilenumber.getError() == null) {
                            permissionUtils.check_permission(PermissionHandler.contactPermissionArrayList(Mobile_Prepaid_Recharge_Service.this), Content_Message.CONTACT_PERMISSION, ApplicationConstant.REQ_READ_CONTACT_PERMISSION);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!operator.getText().toString().equals("")) {
                    if (operator.getError() == null) {
                        browseplan.setVisibility(View.VISIBLE);
                    } else {
                        browseplan.setVisibility(View.GONE);
                    }
                }
            }
        });
        mobilenumber.addTextChangedListener(new CustomTextWatcherLengthAction(mobilenumber, 10, operator, "touch"));
        operator.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    //startActivity(new Intent(Mobile_Prepaid_Recharge_Service.this,Listview_With_Image.class));

                    Intent intent = new Intent(Mobile_Prepaid_Recharge_Service.this, Listview_With_Image.class);

                    Gson gson = new Gson();

                    String data = gson.toJson(getDataList());
                    intent.putExtra("datalist", data);
                    intent.putExtra("title", "Mobile Operator");
                    startActivityForResult(intent, 100);
                }
                return false;
            }
        });
    }

    public ArrayList<DataAdapterVO> getDataList() {
        ArrayList<DataAdapterVO> datalist = new ArrayList<>();
        String operator = Session.getSessionByKey(Mobile_Prepaid_Recharge_Service.this, Session.MOBILE_OPERATOR_LIST);
        try {
            JSONObject objectoperator = new JSONObject(operator);
            JSONArray jsonArray = objectoperator.getJSONArray("dataList");
            for (int i = 0; i < jsonArray.length(); i++) {
                DataAdapterVO dataAdapterVO = new DataAdapterVO();
                JSONObject object = jsonArray.getJSONObject(i);
                dataAdapterVO.setText(object.getString("OperatorName"));
                dataAdapterVO.setImageUrl(object.has("imageUrl") ? object.getString("imageUrl") : null);
                dataAdapterVO.setAssociatedValue(object.getString("OperatorAlias"));
                datalist.add(dataAdapterVO);
            }
        } catch (JSONException e) {
            Utility.showToast(this, Content_Message.error_message);
        }
        return datalist;
    }

    //ApplicationConstant.REQ_ENACH_MANDATE

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 100) {
                operatorname = data.getStringExtra("operatorname");
                operatorcode = data.getStringExtra("operator");

                ConnectionVO connectionVO = new ConnectionVO();
                connectionVO.setTitle("Select Circles");
                connectionVO.setSharedPreferenceKey(Session.MOBILE_STATE_LIST);
                connectionVO.setEntityIdKey("RegionAlias");
                connectionVO.setEntityTextKey("RegionName");
                Intent intent = new Intent(getApplicationContext(), ListViewSingleText.class);
                intent.putExtra(ApplicationConstant.INTENT_EXTRA_CONNECTION, connectionVO);
                startActivityForResult(intent, 1000);

            } else if (requestCode == 1000) {
                amount.requestFocus();
                browseplan.setVisibility(View.VISIBLE);
                regionname = data.getStringExtra("valueName");
                regioncode = data.getStringExtra("valueId");
                operator.setText(operatorcode + "-" + data.getStringExtra("valueName"));
                operator.setError(null);
                amount.setError(null);
                if (!mobilenumber.getText().toString().equals("") && Utility.validatePattern(mobilenumber.getText().toString().trim(), ApplicationConstant.MOBILENO_VALIDATION) != null) {
                    mobilenumber.setError(Utility.validatePattern(mobilenumber.getText().toString().trim(), ApplicationConstant.MOBILENO_VALIDATION));
                }
            } else if (requestCode == 101) {
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);
                if (c.moveToFirst()) {
                    String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    String num = "";
                    if (Integer.valueOf(hasNumber) == 1) {
                        Cursor numbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        while (numbers.moveToNext()) {
                            num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+", "");
                        }
                        if (num.length() > 10) {
                            num = num.substring(num.length() - 10);
                        }
                        mobilenumber.setText(num);
                        mobilenumber.setSelection(mobilenumber.getText().toString().length());
                    }
                }
            } else if (requestCode == 102) {
                amount.setText(data.getStringExtra("amount"));
                amount.setSelection(amount.getText().length());
            } else if (requestCode == ApplicationConstant.REQ_ENACH_MANDATE) {
                boolean enachMandateStatus = data.getBooleanExtra("mandate_status", false);
                if (enachMandateStatus) {
                    afterRechargeMoveHistorySummaryActivity(Mobile_Prepaid_Recharge_Service.this, true, oxigenValidateResponce);
                } else {
                    Utility.showSingleButtonDialog(Mobile_Prepaid_Recharge_Service.this, "Alert", data.getStringExtra("msg"), false);
                }
            } else if (requestCode == ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE) {
                boolean enachMandateStatus = data.getBooleanExtra("mandate_status", false);
                if (enachMandateStatus) {
                    proceedToRecharge(oxigenValidateResponce.getTypeId().toString(), data.getStringExtra("bankMandateId"), AuthServiceProviderVO.ENACHIDFC, true);
                } else {
                    Utility.showSingleButtonDialog(Mobile_Prepaid_Recharge_Service.this, "Alert", data.getStringExtra("msg"), false);
                }
            } else if (requestCode == ApplicationConstant.REQ_SI_MANDATE) {
                int SIMandateId = data.getIntExtra("mandateId", 0);
                if (SIMandateId != 0) {
                    proceedToRecharge(oxigenValidateResponce.getTypeId().toString(), String.valueOf(SIMandateId), AuthServiceProviderVO.AUTOPE_PG, true);
                } else {
                    Utility.showSingleButtonDialog(Mobile_Prepaid_Recharge_Service.this, "Error !", Content_Message.error_message, false);
                }
            } else if (requestCode == ApplicationConstant.REQ_UPI_FOR_MANDATE) {
                int upiMandateId = data.getIntExtra("mandateId", 0);
                if (upiMandateId != 0) {
                    proceedToRecharge(oxigenValidateResponce.getTypeId().toString(), String.valueOf(upiMandateId), AuthServiceProviderVO.AUTOPE_PG_UPI, true);
                } else {
                    Utility.showSingleButtonDialog(Mobile_Prepaid_Recharge_Service.this, "Error !", Content_Message.error_message, false);
                }
            }else if(requestCode == ApplicationConstant.REQ_DIRECT_PAYMENT_RESULT){
                if (data != null) {
                    CustomerVO customerVO1 = new Gson().fromJson(data.getStringExtra("data"),CustomerVO.class);
                    if(customerVO1.getStatusCode().equals("400")) {
                        BillPayRequest.utilityPaymentFailed(Mobile_Prepaid_Recharge_Service.this,Integer.parseInt(data.getStringExtra(DirectPaymentActivity.EXTRAS_ID)),new VolleyResponse((VolleyResponse.OnSuccess)(fail)->{
                        },(VolleyResponse.OnError)(Error)->{
                            try {
                                JSONObject jsonObject = new JSONObject(Error);
                                removefetchbilllayout();
                                Utility.showSingleButtonDialog(Mobile_Prepaid_Recharge_Service.this, jsonObject.getString("title"), jsonObject.getString("message"), false);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ExceptionsNotification.ExceptionHandling(Mobile_Prepaid_Recharge_Service.this, Utility.getStackTrace(e));
                            }
                        }));
                    }else {
                        //BillPayRequest.onActivityResult(this, data, requestCode);
                    }
                } else {
                    Utility.showSingleButtonDialog(this, "Error !", "Something went wrong, Please try again!", false);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.proceed:
                oxigenValidateResponce = new OxigenTransactionVO();

                rechargeProceed();
                break;
            case R.id.browseplan:

                HashMap<String, Object> params = new HashMap<String, Object>();
                ConnectionVO connectionVO = OxigenPlanBO.getPlan();

                OxigenPlanVO oxigenPlanVO = new OxigenPlanVO();
                ServiceTypeVO serviceTypeVO = new ServiceTypeVO();
                serviceTypeVO.setServiceTypeId(Integer.parseInt(serviceid));
                oxigenPlanVO.setServiceType(serviceTypeVO);
                oxigenPlanVO.setOperatorAlias(operatorcode);
                oxigenPlanVO.setStateAlias(regioncode);
                Gson gson = new Gson();
                String json = gson.toJson(oxigenPlanVO);
                params.put("volley", json);

                Log.w("getPlan", json);
                connectionVO.setParams(params);

                VolleyUtils.makeJsonObjectRequest(this, connectionVO, new VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                    }

                    @Override
                    public void onResponse(Object resp) throws JSONException {
                        JSONObject response = (JSONObject) resp;
                        Gson gson = new Gson();
                        OxigenPlanVO oxigenPlanVO = gson.fromJson(response.toString(), OxigenPlanVO.class);


                        if (oxigenPlanVO.getStatusCode().equals("400")) {

                            StringBuffer stringBuffer = new StringBuffer();

                            for (int i = 0; i < oxigenPlanVO.getErrorMsgs().size(); i++) {
                                stringBuffer.append(oxigenPlanVO.getErrorMsgs().get(i));
                            }
                            Utility.showSingleButtonDialog(Mobile_Prepaid_Recharge_Service.this, oxigenPlanVO.getDialogTitle(), stringBuffer.toString(), false);
                        } else {
                            String json = gson.toJson(oxigenPlanVO);
                            Session.set_Data_Sharedprefence(Mobile_Prepaid_Recharge_Service.this, Session.CACHE_BROWSE_DATA, json);
                            startActivityForResult(new Intent(Mobile_Prepaid_Recharge_Service.this, Browse_Plan.class), 102);
                        }
                    }
                });
                break;
        }
    }


    public void rechargeProceed() {
        try {
            boolean valid = true;

            if (mobilenumber.getText().toString().equals("")) {
                mobilenumber.setError(ErrorMsg.Field_Required);
                valid = false;
            } else if (!mobilenumber.getText().toString().equals("") && Utility.validatePattern(mobilenumber.getText().toString().trim(), ApplicationConstant.MOBILENO_VALIDATION) != null) {
                mobilenumber.setError(Utility.validatePattern(mobilenumber.getText().toString().trim(), ApplicationConstant.MOBILENO_VALIDATION));
                valid = false;
            }

            if (operator.getText().toString().equals("")) {
                operator.setError(ErrorMsg.Field_Required);
                valid = false;
            }
            if (amount.getText().toString().equals("")) {
                amount.setError(ErrorMsg.Field_Required);
                browseplan.setVisibility(View.GONE);
                valid = false;
            }

            if (valid) {
                String btn[] = {"Cancel", "Ok"};

                JSONArray jsonArray = new JSONArray();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", "Operator");
                jsonObject.put("value", operator.getText().toString());
                jsonArray.put(jsonObject);

                jsonObject = new JSONObject();
                jsonObject.put("key", "Number");
                jsonObject.put("value", mobilenumber.getText().toString());
                jsonArray.put(jsonObject);

                jsonObject = new JSONObject();
                jsonObject.put("key", "Amount");
                jsonObject.put("value", amount.getText().toString());
                jsonArray.put(jsonObject);

                Utility.confirmationDialog(new DialogInterface() {
                    @Override
                    public void confirm(Dialog dialog) {
                        Utility.dismissDialog(Mobile_Prepaid_Recharge_Service.this, dialog);
                        try {
                            oxiMobileRechargeValidation();
                            // proceedToRecharge();
                        } catch (Exception e) {
                            ExceptionsNotification.ExceptionHandling(Mobile_Prepaid_Recharge_Service.this, Utility.getStackTrace(e));
                            //Utility.exceptionAlertDialog(Mobile_Prepaid_Recharge_Service.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
                        }
                    }

                    @Override
                    public void modify(Dialog dialog) {
                        Utility.dismissDialog(Mobile_Prepaid_Recharge_Service.this, dialog);
                    }
                }, Mobile_Prepaid_Recharge_Service.this, jsonArray, null, "Confirmation", btn);
            }
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(Mobile_Prepaid_Recharge_Service.this, Utility.getStackTrace(e));
            // Utility.exceptionAlertDialog(Mobile_Prepaid_Recharge_Service.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }

    private void oxiMobileRechargeValidation() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = OxigenPlanBO.oxiMobileRechargeValidation();

        OxigenTransactionVO oxigenTransactionVO = new OxigenTransactionVO();
        oxigenTransactionVO.setReferenceName("mobile");
        oxigenTransactionVO.setReferenceValue(mobilenumber.getText().toString().trim());
        oxigenTransactionVO.setAmount(Double.parseDouble(amount.getText().toString().trim()));
        oxigenTransactionVO.setStateRegion(regionname);
        oxigenTransactionVO.setOperateName(operatorname);

        CustomerVO customerVO = new CustomerVO();
        customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(Mobile_Prepaid_Recharge_Service.this)));
        oxigenTransactionVO.setCustomer(customerVO);

        ServiceTypeVO serviceTypeVO = new ServiceTypeVO();
        serviceTypeVO.setServiceTypeId(Integer.parseInt(serviceid));
        oxigenTransactionVO.setServiceType(serviceTypeVO);

        Gson gson = new Gson();
        String json = gson.toJson(oxigenTransactionVO);

        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("request", params.toString());

        VolleyUtils.makeJsonObjectRequest(this, connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }

            @Override
            public void onResponse(Object resp) throws JSONException {

                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                oxigenValidateResponce = gson.fromJson(response.toString(), OxigenTransactionVO.class);

                if (oxigenValidateResponce.getStatusCode().equals("400")) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < oxigenValidateResponce.getErrorMsgs().size(); i++) {
                        stringBuffer.append(oxigenValidateResponce.getErrorMsgs().get(i));
                    }
                    Utility.showSingleButtonDialog(Mobile_Prepaid_Recharge_Service.this, oxigenValidateResponce.getDialogTitle(), stringBuffer.toString(), false);
                } else {
                    if (oxigenValidateResponce.getTypeId() == null) {
                        Utility.showSingleButtonDialog(Mobile_Prepaid_Recharge_Service.this, "Error !", "Something went wrong, Please try again!", false);
                        return;
                    }
                    proceed.setVisibility(View.GONE);
                    //remove all edit text focus
                    getWindow().getDecorView().clearFocus();
                    JSONArray dataArry = new JSONArray(oxigenValidateResponce.getAnonymousString3());
                    Typeface typeface = ResourcesCompat.getFont(Mobile_Prepaid_Recharge_Service.this, R.font.poppinssemibold);
                    for (int i = 0; i < dataArry.length(); i++) {
                        JSONObject jsonObject = dataArry.getJSONObject(i);

                        LinearLayout et = new LinearLayout(new ContextThemeWrapper(Mobile_Prepaid_Recharge_Service.this, R.style.confirmation_dialog_layout));
                        et.setPadding(Utility.getPixelsFromDPs(Mobile_Prepaid_Recharge_Service.this, 10), Utility.getPixelsFromDPs(Mobile_Prepaid_Recharge_Service.this, 10), Utility.getPixelsFromDPs(Mobile_Prepaid_Recharge_Service.this, 10), Utility.getPixelsFromDPs(Mobile_Prepaid_Recharge_Service.this, 10));

                        TextView text = new TextView(new ContextThemeWrapper(Mobile_Prepaid_Recharge_Service.this, R.style.confirmation_dialog_filed));
                        text.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
                        text.setText(jsonObject.getString("key"));
                        text.setMaxLines(1);
                        text.setEllipsize(TextUtils.TruncateAt.END);
                        text.setTypeface(typeface);
                        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                        TextView value = new TextView(new ContextThemeWrapper(Mobile_Prepaid_Recharge_Service.this, R.style.confirmation_dialog_value));
                        value.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                        value.setText(jsonObject.getString("value"));
                        value.setTypeface(typeface);
                        value.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                        et.addView(text);
                        et.addView(value);
                        fetchbilllayout.addView(et);
                    }

                    Button billPaybtn = Utility.getButton(Mobile_Prepaid_Recharge_Service.this);
                    billPaybtn.setText("Proceed");
                    fetchbilllayout.addView(billPaybtn);

                    billPaybtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startDirectPaymentActivity(Mobile_Prepaid_Recharge_Service.this,oxigenValidateResponce);
                        }
                    });

                    Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
                    fetchbillcard.startAnimation(animFadeIn);
                    fetchbillcard.setVisibility(View.VISIBLE);
                }






                 /*   Utility.showSelectPaymentTypeDialog(Mobile_Prepaid_Recharge_Service.this, "Payment Type", oxigenValidateResponce.getPaymentTypeObject(), new AlertSelectDialogClick((AlertSelectDialogClick.OnSuccess) (position) -> {
                        int selectPosition = Integer.parseInt(position);
                        if (selectPosition == ApplicationConstant.BankMandatePayment) {
                            // 07/05/2020

                            BillPayRequest.showBankMandateOrSiMandateInfo(Mobile_Prepaid_Recharge_Service.this, oxigenValidateResponce.getBankMandateHtml(), new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk) (ok) -> {
                                MyDialog.showWebviewConditionalAlertDialog(Mobile_Prepaid_Recharge_Service.this, oxigenValidateResponce.getClinkingOnBankMandate(), true, new ConfirmationGetObjet((ConfirmationGetObjet.OnOk) (bankmandatedialog) -> {
                                    HashMap<String, Object> objectHashMap = (HashMap<String, Object>) bankmandatedialog;
                                    Utility.dismissDialog(Mobile_Prepaid_Recharge_Service.this, ((Dialog) Objects.requireNonNull(objectHashMap.get("dialog"))));

                                    if (String.valueOf(objectHashMap.get("data")).equalsIgnoreCase("ok")) {
                                        //on si or recharge and mandate
                                        startSIActivity(Mobile_Prepaid_Recharge_Service.this, oxigenValidateResponce, ApplicationConstant.PG_MANDATE_AND_RECHARGE);
                                    }
                                }, (ConfirmationGetObjet.OnCancel) (cancel) -> {
                                    Utility.dismissDialog(Mobile_Prepaid_Recharge_Service.this, ((Dialog) cancel));
                                    //if mandate is exist proceed bill  direct
                                    //if mandate is not exist check bank bank mandate
                                    // check mandate and adopt bank for service
                                    //oxiPripaidServiceMandateCheck(Mobile_Prepaid_Recharge_Service.this,oxigenValidateResponce.getServiceId(),true,oxigenValidateResponce);
                                    oxigenValidateResponce.setProvider(getAuthServiceProvider(AuthServiceProviderVO.ENACHIDFC));
                                    setBankMandateOrRecharge(Mobile_Prepaid_Recharge_Service.this, oxigenValidateResponce);
                                }));
                            }));

                        } else if (selectPosition == ApplicationConstant.SIMandatePayment) {
                            // recharge on SI mandate

                            BillPayRequest.showBankMandateOrSiMandateInfo(Mobile_Prepaid_Recharge_Service.this, oxigenValidateResponce.getSiMandateHtml(), new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk) (ok) -> {
                                oxigenValidateResponce.setProvider(getAuthServiceProvider(AuthServiceProviderVO.AUTOPE_PG));
                                setBankMandateOrRecharge(Mobile_Prepaid_Recharge_Service.this, oxigenValidateResponce);
                            }));
                            // proceedToRecharge(oxigenValidateResponce.getTypeId().toString(),"AUTOPETXNID60", AuthServiceProviderVO.PAYU);
                        } else if (selectPosition == ApplicationConstant.UPIMandatePayment) {
                            // recharge on SI mandate
                            BillPayRequest.showBankMandateOrSiMandateInfo(Mobile_Prepaid_Recharge_Service.this, oxigenValidateResponce.getUpiMandateHtml(), new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk) (ok) -> {
                                oxigenValidateResponce.setProvider(getAuthServiceProvider(AuthServiceProviderVO.AUTOPE_PG_UPI));
                                setBankMandateOrRecharge(Mobile_Prepaid_Recharge_Service.this, oxigenValidateResponce);
                            }));
                            // proceedToRecharge(oxigenValidateResponce.getTypeId().toString(),"AUTOPETXNID60", AuthServiceProviderVO.PAYU);
                        }
                    }));*/

            }
        });
    }

    public static void startDirectPaymentActivity(Context context, OxigenTransactionVO oxigenTransactionVO) {
        try {
            Intent intent = new Intent(context, DirectPaymentActivity.class);
            intent.putExtra(DirectPaymentActivity.EXTRAS_ID, oxigenTransactionVO.getTypeId());
            intent.putExtra(DirectPaymentActivity.EXTRAS_ENCRYPTED_VALUE, oxigenTransactionVO.getEncryptedValue());
            intent.putExtra(DirectPaymentActivity.EXTRAS_DIRECT_PAYMENT, 1);
            intent.putExtra(DirectPaymentActivity.EXTRAS_TITLE, ApplicationConstant.DIRECT_PAYMENT_TITLE);
            ((Activity) context).startActivityForResult(intent, ApplicationConstant.REQ_DIRECT_PAYMENT_RESULT);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context, Utility.getStackTrace(e));
        }

    }

    public void removefetchbilllayout() {
        oxigenValidateResponce = new OxigenTransactionVO();
        //if fetch bill is true and fetch bill layout not = null
        if (fetchbilllayout.getChildCount() > 0) {
            fetchbilllayout.removeAllViews();
            proceed.setVisibility(View.VISIBLE);
            fetchbillcard.setVisibility(View.GONE);
        }
    }

    public void changeEdittextValue(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                removefetchbilllayout();
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.w("onTextChanged", charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void setBankMandateOrRecharge(Context context, OxigenTransactionVO oxigenTransactionVO) {
        BeforeRecharge.beforeRechargeAddMandate(context, oxigenTransactionVO, new MandateAndRechargeInterface((MandateAndRechargeInterface.OnRecharge) (recharge) -> {
            proceedToRecharge(oxigenTransactionVO.getTypeId().toString(), (String) recharge, oxigenTransactionVO.getProvider().getProviderId(), false);
        }, (MandateAndRechargeInterface.OnMandate) (mandate) -> {
            if (oxigenTransactionVO.getProvider().getProviderId() == AuthServiceProviderVO.AUTOPE_PG) {
                startSIActivity(context, oxigenTransactionVO, ApplicationConstant.PG_MANDATE_AND_RECHARGE);
            } else if (oxigenTransactionVO.getProvider().getProviderId() == AuthServiceProviderVO.ENACHIDFC) {
                startBankMandateActivity(context, oxigenTransactionVO);
            } else if (oxigenTransactionVO.getProvider().getProviderId() == AuthServiceProviderVO.AUTOPE_PG_UPI) {
                startUPIActivity(context, oxigenTransactionVO, ApplicationConstant.PG_MANDATE_AND_RECHARGE);
            }
        }));
    }

    public static AuthServiceProviderVO getAuthServiceProvider(int providerId) {
        // 30/06/2020
        AuthServiceProviderVO authServiceProviderVO = new AuthServiceProviderVO();
        authServiceProviderVO.setProviderId(providerId);
        return authServiceProviderVO;
    }

    public static void startBankMandateActivity(Context context, OxigenTransactionVO oxigenTransactionVO) {
        try {
            Intent intent = new Intent(context, Enach_Mandate.class);
            intent.putExtra("forresutl", true);
            intent.putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(oxigenTransactionVO.getServiceId())));
            ((Activity) context).startActivityForResult(intent, ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context, Utility.getStackTrace(e));
        }

    }

    public static void startSIActivity(Context context, OxigenTransactionVO oxigenTransactionVO, String paymentType) {
        Intent intent = new Intent(context, SI_First_Data.class);
        intent.putExtra("id", oxigenTransactionVO.getTypeId());
        intent.putExtra("amount", oxigenTransactionVO.getNetAmount());
        intent.putExtra("serviceId", oxigenTransactionVO.getServiceId() + "");
        intent.putExtra("paymentType", paymentType);
        ((Activity) context).startActivityForResult(intent, ApplicationConstant.REQ_SI_MANDATE);
    }

    public static void startUPIActivity(Context context, OxigenTransactionVO oxigenTransactionVO, String paymentType) {
        Intent intent = new Intent(context, UPI_Mandate.class);
        intent.putExtra("id", oxigenTransactionVO.getTypeId());
        intent.putExtra("amount", ApplicationConstant.SI_UPI_MANDATE_AMOUNT);
        intent.putExtra("serviceId", oxigenTransactionVO.getServiceId() + "");
        intent.putExtra("paymentType", paymentType);
        ((Activity) context).startActivityForResult(intent, ApplicationConstant.REQ_UPI_FOR_MANDATE);
    }

   /* public void beforeRechargeAddMandate(Context context , OxigenTransactionVO oxigenTransactionVOresp){
        CheckMandateAndShowDialog.oxiServiceMandateCheck(context,oxigenTransactionVOresp.getServiceId(),oxigenTransactionVOresp.getProvider().getProviderId(),new VolleyResponse((VolleyResponse.OnSuccess)(mandatecheckresp)->{
            OxigenTransactionVO oxigenTransactionVO = (OxigenTransactionVO) mandatecheckresp;
            if(oxigenTransactionVO!=null){
                if(oxigenTransactionVO.getStatusCode().equals("ap102")) {

                    if(oxigenTransactionVOresp.getProvider().getProviderId()== AuthServiceProviderVO.AUTOPE_PG){
                        startSIActivity(context,oxigenTransactionVOresp,ApplicationConstant.PG_MANDATE_AND_RECHARGE);
                    }else if(oxigenTransactionVOresp.getProvider().getProviderId()== AuthServiceProviderVO.ENACHIDFC){
                        ((Activity) context).startActivityForResult(new Intent(context, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(oxigenTransactionVOresp.getServiceId()))), ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE);
                    }

                }else if(oxigenTransactionVO.getStatusCode().equals("ap103")){
                    String[] buttons = {"New Bank", "Existing Bank"};
                    Utility.showDoubleButtonDialogConfirmation(new DialogInterface() {
                        @Override
                        public void confirm(Dialog dialog) {
                            dialog.dismiss();
                            createBankListInDialog(context,oxigenTransactionVOresp.getServiceId(),oxigenTransactionVO,new CallBackInterface((CallBackInterface.OnSuccess)(onclick)->{
                                String bankId = (String) onclick;
                                if(!bankId.equals("0")){
                                    proceedToRecharge(oxigenTransactionVOresp.getTypeId().toString(), bankId, oxigenTransactionVOresp.getProvider().getProviderId(),false);
                                }else {
                                    if(oxigenTransactionVOresp.getProvider().getProviderId()== AuthServiceProviderVO.AUTOPE_PG){
                                        startSIActivity(context,oxigenTransactionVOresp,ApplicationConstant.PG_MANDATE_AND_RECHARGE);
                                    }else if(oxigenTransactionVOresp.getProvider().getProviderId()== AuthServiceProviderVO.ENACHIDFC){
                                        ((Activity) context).startActivityForResult(new Intent(context, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(oxigenTransactionVOresp.getServiceId()))), ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE);
                                    }
                                }
                            }));
                        }
                        @Override
                        public void modify(Dialog dialog) {
                            dialog.dismiss();
                            if(oxigenTransactionVOresp.getProvider().getProviderId()== AuthServiceProviderVO.AUTOPE_PG){
                                startSIActivity(context,oxigenTransactionVOresp,ApplicationConstant.PG_MANDATE_AND_RECHARGE);
                            }else if(oxigenTransactionVOresp.getProvider().getProviderId()== AuthServiceProviderVO.ENACHIDFC){
                                ((Activity) context).startActivityForResult(new Intent(context, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(oxigenTransactionVOresp.getServiceId()))), ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE);
                            }
                        }
                    }, context, oxigenTransactionVO.getErrorMsgs().get(0), "", buttons);
                }
            }
        }));
    }


    public void createBankListInDialog(Context context, Integer serivceId, OxigenTransactionVO checkMandateResponse , CallBackInterface callBackInterface){
        try {
            JSONArray arryjson = new JSONArray(checkMandateResponse.getAnonymousString());
            ArrayList<CustomerAuthServiceVO> customerAuthServiceArry = new ArrayList<>();
            for (int i = 0; i < arryjson.length(); i++) {
                JSONObject jsonObject = arryjson.getJSONObject(i);
                CustomerAuthServiceVO customerAuthServiceVO = new CustomerAuthServiceVO();
                customerAuthServiceVO.setBankName(jsonObject.getString("bankName"));
                customerAuthServiceVO.setProviderTokenId(jsonObject.getString("mandateId"));
                customerAuthServiceVO.setCustomerAuthId(jsonObject.getInt("id"));
                customerAuthServiceVO.setAnonymousString(jsonObject.getString("status"));
                customerAuthServiceArry.add(customerAuthServiceVO);
            }
            CustomerAuthServiceVO customerAuthServiceVO = new CustomerAuthServiceVO();
            customerAuthServiceVO.setBankName(null);
            customerAuthServiceVO.setProviderTokenId("Add New Mandate");
            customerAuthServiceVO.setCustomerAuthId(0);
            customerAuthServiceVO.setAnonymousString(null);
            customerAuthServiceArry.add(customerAuthServiceVO);

            Utility.alertselectdialog(context, "Choose from existing Bank", customerAuthServiceArry, new AlertSelectDialogClick((AlertSelectDialogClick.OnSuccess) callBackInterface::onSuccess));

        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(Mobile_Prepaid_Recharge_Service.this , Utility.getStackTrace(e));
            //Utility.exceptionAlertDialog(context, "Alert!", "Something went wrong, Please try again!", "Report", Utility.getStackTrace(e));
      }
    }
*/


    public void proceedToRecharge(String oxigenTypeId, String typeRechargeId, int providerId, boolean isRecharge) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = OxigenPlanBO.oxiMobileRecharge();

        AuthServiceProviderVO authServiceProviderVO = new AuthServiceProviderVO();
        authServiceProviderVO.setProviderId(providerId);
        OxigenTransactionVO oxigenTransactionVO = new OxigenTransactionVO();
        oxigenTransactionVO.setTypeId(Integer.parseInt(oxigenTypeId));
        oxigenTransactionVO.setAnonymousString(typeRechargeId);
        oxigenTransactionVO.setProvider(authServiceProviderVO);
        oxigenTransactionVO.setEventIs(isRecharge);

        CustomerVO customerVO = new CustomerVO();
        customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(this)));
        oxigenTransactionVO.setCustomer(customerVO);

        ServiceTypeVO serviceTypeVO = new ServiceTypeVO();
        serviceTypeVO.setServiceTypeId(Integer.parseInt(serviceid));
        oxigenTransactionVO.setServiceType(serviceTypeVO);
        Gson gson = new Gson();
        String json = gson.toJson(oxigenTransactionVO);

        params.put("volley", json);
        connectionVO.setParams(params);

        Log.w("proceedToRecharge", params.toString());
        VolleyUtils.makeJsonObjectRequest(this, connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }

            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                OxigenTransactionVO oxigenTransactionVOresp = gson.fromJson(response.toString(), OxigenTransactionVO.class);

                if (oxigenTransactionVOresp.getStatusCode().equals("400")) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < oxigenTransactionVOresp.getErrorMsgs().size(); i++) {
                        stringBuffer.append(oxigenTransactionVOresp.getErrorMsgs().get(i));
                    }
                    Utility.showSingleButtonDialog(Mobile_Prepaid_Recharge_Service.this, oxigenTransactionVOresp.getDialogTitle(), stringBuffer.toString(), false);
                } else {
                    // replace oxigenValidateResponce object on success on recharge
                    oxigenValidateResponce = oxigenTransactionVOresp;
                    // remove all dialog
                    dismissDialogCurrentActivity();

                    //if mandate is not exits
                    showMandateSchedulerAfterRecharge(Mobile_Prepaid_Recharge_Service.this, oxigenTransactionVOresp.getHtmlString(), oxigenTransactionVOresp, false);
                   /* if(oxigenTransactionVOresp.isEventIs()) {
                        MyDialog.showSingleButtonBigContentDialog(Mobile_Prepaid_Recharge_Service.this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                            ok.dismiss();
                            afterRechargeMoveHistorySummaryActivity(Mobile_Prepaid_Recharge_Service.this,false,oxigenTransactionVOresp);

                        }),oxigenTransactionVOresp.getDialogTitle(),oxigenTransactionVOresp.getAnonymousString());
                    }else {
                        showMandateSchedulerAfterRecharge(Mobile_Prepaid_Recharge_Service.this,oxigenTransactionVOresp.getAnonymousString(),oxigenTransactionVOresp,false);
                    }*/
                }
            }
        });
    }

    public void showMandateSchedulerAfterRecharge(Context context, String htmlUrl, OxigenTransactionVO oxigenTransactionVOresp, boolean isRecharge) {
        // ask to customer bank mandate after recharge
        MyDialog.showWebviewConditionalAlertDialog(context, htmlUrl, false, new ConfirmationGetObjet((ConfirmationGetObjet.OnOk) (ok) -> {
            //((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(oxigenTransactionVOresp.getServiceId()))), ApplicationConstant.REQ_ENACH_MANDATE);
            HashMap<String, Object> objectHashMap = (HashMap<String, Object>) ok;
            //add Dialog object in list
            GlobalApplication.dialog_List.add(((Dialog) Objects.requireNonNull(objectHashMap.get("dialog"))));
            //((Dialog) Objects.requireNonNull(objectHashMap.get("dialog"))).dismiss();
            try {
                JSONObject alertDialogDate = new JSONObject((String) objectHashMap.get("data"));
                CheckMandateAndShowDialog.setManuallyServiceSchedule(context, oxigenTransactionVOresp, alertDialogDate, new VolleyResponse((VolleyResponse.OnSuccess) (success) -> {
                    OxigenTransactionVO oxigenTransactionVO = (OxigenTransactionVO) success;
                    afterRechargeMoveHistorySummaryActivity(Mobile_Prepaid_Recharge_Service.this, true, oxigenTransactionVO);
                   /* if(cusVO.isEventIs()){
                        afterRechargeMoveHistorySummaryActivity(Mobile_Prepaid_Recharge_Service.this,true,oxigenTransactionVOresp);
                    }else{
                        //afterRechargeAddMandate(context,oxigenTransactionVOresp);
                    }*/
                }));
            } catch (Exception e) {
                ExceptionsNotification.ExceptionHandling(context, Utility.getStackTrace(e));
            }
        }, (ConfirmationGetObjet.OnCancel) (cancel) -> {
            Utility.dismissDialog(Mobile_Prepaid_Recharge_Service.this, ((Dialog) cancel));

            afterRechargeMoveHistorySummaryActivity(Mobile_Prepaid_Recharge_Service.this, false, oxigenTransactionVOresp);
        }));
    }

   /* public void afterRechargeAddMandate(Context context , OxigenTransactionVO oxigenTransactionVOresp){
        oxiPripaidServiceMandateCheck(context,oxigenTransactionVOresp.getServiceId(),oxigenTransactionVOresp,new VolleyResponse((VolleyResponse.OnSuccess)(mandatecheckresp)->{
            OxigenTransactionVO oxigenTransactionVO = (OxigenTransactionVO) mandatecheckresp;
            if(oxigenTransactionVO!=null){
                if(oxigenTransactionVO.getStatusCode().equals("ap102")) {
                    ((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(oxigenTransactionVOresp.getServiceId()))), ApplicationConstant.REQ_ENACH_MANDATE);
                }else if(oxigenTransactionVO.getStatusCode().equals("ap103")){
                    String[] buttons = {"New Bank", "Existing Bank"};
                    Utility.showDoubleButtonDialogConfirmation(new DialogInterface() {
                        @Override
                        public void confirm(Dialog dialog) {
                            dialog.dismiss();
                            createBankListInDialog(context,oxigenTransactionVOresp.getServiceId(),oxigenTransactionVO,new CallBackInterface((CallBackInterface.OnSuccess)(onclick)->{
                                String bankId = (String) onclick;
                                if(!bankId.equals("0")){
                                    afterRechargeMoveHistorySummaryActivity(Mobile_Prepaid_Recharge_Service.this,true,oxigenTransactionVOresp);
                                }else {
                                    ((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(oxigenTransactionVOresp.getServiceId()))), ApplicationConstant.REQ_ENACH_MANDATE);
                                }
                            }));
                        }
                        @Override
                        public void modify(Dialog dialog) {
                            dialog.dismiss();
                            ((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(oxigenTransactionVOresp.getServiceId()))), ApplicationConstant.REQ_ENACH_MANDATE);
                        }
                    }, context, oxigenTransactionVO.getErrorMsgs().get(0), "", buttons);
                }
            }
        }));
    }*/

    public static void afterRechargeMoveHistorySummaryActivity(Context context, boolean getHistoryDetails, OxigenTransactionVO oxigenTransactionVO) {
        dismissDialogCurrentActivity();
        if (getHistoryDetails) {
            CheckMandateAndShowDialog.afterRechargeGetRechargeDetails(context, oxigenTransactionVO.getCustoemrHistoryId(), oxigenTransactionVO.getAnonymousString(), new VolleyResponse((VolleyResponse.OnSuccess) (success) -> {
                CustomerVO customerVO = (CustomerVO) success;
                MyDialog.showSingleButtonBigContentDialog(context, new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk) (ok) -> {
                    Utility.dismissDialog(context, ok);
                    if (customerVO.getCustoemrHistoryId() != null) {
                        ((Activity) context).startActivity(new Intent(context, HistorySummary.class).putExtra("historyId", customerVO.getCustoemrHistoryId().toString()));
                        ((Activity) context).finish();
                    } else {
                        ((Activity) context).finish();
                    }
                }), customerVO.getDialogTitle(), customerVO.getAnonymousString());
            }));
        } else {
            if (oxigenTransactionVO.getCustoemrHistoryId() != null) {
                ((Activity) context).startActivity(new Intent(context, HistorySummary.class).putExtra("historyId", oxigenTransactionVO.getCustoemrHistoryId().toString()));
                ((Activity) context).finish();
            } else {
                ((Activity) context).finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialogCurrentActivity();
    }

    public static void dismissDialogCurrentActivity() {
        if (GlobalApplication.dialog_List.size() > 0) {
            for (Dialog dialog : GlobalApplication.dialog_List) {
                dialog.dismiss();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void PermissionGranted(int request_code) {
        if (request_code == ApplicationConstant.REQ_READ_CONTACT_PERMISSION) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, 101);
        }
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
    }

    @Override
    public void PermissionDenied(int request_code) {
    }

    @Override
    public void NeverAskAgain(int request_code) {
    }
}
