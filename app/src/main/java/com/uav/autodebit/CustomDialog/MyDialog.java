package com.uav.autodebit.CustomDialog;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.uav.autodebit.DMRC.DMRCApi;
import com.uav.autodebit.Interface.BigContentDialogIntetface;
import com.uav.autodebit.Interface.CallBackInterface;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.ConfirmationGetObjet;
import com.uav.autodebit.Interface.DateInterface;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.ErrorMsg;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.BeneAccVO;
import com.uav.autodebit.vo.CityVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.RefundVO;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MyDialog {


    @SuppressLint("SetJavaScriptEnabled")
    public static void showWebviewAlertDialog(Context context, String html, boolean backBtnCloseDialog, ConfirmationDialogInterface confirmationDialogInterface) {
        final Dialog cusdialog = new Dialog(context);
        cusdialog.requestWindowFeature(1);
        Objects.requireNonNull(cusdialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));
        cusdialog.setContentView(R.layout.webview_alert_dialog);
        cusdialog.setCanceledOnTouchOutside(false);
        cusdialog.setCancelable(backBtnCloseDialog);
        cusdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        WebView webview = cusdialog.findViewById(R.id.webview);
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
        });

        webview.setVerticalScrollBarEnabled(false);
        webview.setHorizontalScrollBarEnabled(false);
        webview.getSettings().setBuiltInZoomControls(false);

        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setInitialScale(1);
        webview.getSettings().setUseWideViewPort(true);

        // webview.loadData(html, "text/html; charset=utf-8", "UTF-8");

        webview.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
        webview.addJavascriptInterface(new Object() {
            @JavascriptInterface // For API 17+
            public void performClick(String message) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (message.equalsIgnoreCase("ok")) {
                            confirmationDialogInterface.onOk(cusdialog);
                        } else if (message.equalsIgnoreCase("cancel")) {
                            confirmationDialogInterface.onCancel(cusdialog);
                        }
                    }
                });
            }
        }, "ok");
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(cusdialog.getWindow().getAttributes());
        lp.width = (WindowManager.LayoutParams.MATCH_PARENT);
        lp.height = (WindowManager.LayoutParams.WRAP_CONTENT);

        if (!((Activity) context).isFinishing() && !cusdialog.isShowing()) cusdialog.show();
        cusdialog.getWindow().setAttributes(lp);
    }


    @SuppressLint("SetJavaScriptEnabled")
    public static void showWebviewConditionalAlertDialog(Context context, String value, boolean backBtnCloseDialog, ConfirmationGetObjet confirmationGetObjet) {
        try {
            final Dialog cusdialog = new Dialog(context);
            cusdialog.requestWindowFeature(1);
            Objects.requireNonNull(cusdialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));
            cusdialog.setContentView(R.layout.webview_alert_dialog);
            cusdialog.setCanceledOnTouchOutside(false);
            cusdialog.setCancelable(backBtnCloseDialog);
            // cusdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            String[] value_split = value.split("\\|");
            WebView webview = cusdialog.findViewById(R.id.webview);
            LinearLayout addview = cusdialog.findViewById(R.id.addview);

            if (value_split.length > 1) {
                TextView textView = Utility.getTextView(context, value_split[1]);
                addview.addView(textView);
                textView.setLayoutParams(Utility.getLayoutparams(context, 0, 0, 0, 0));
                addview.setVisibility(View.VISIBLE);
                addview.setPadding(10, 10, 10, 10);
            }

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
            });


            webview.setVerticalScrollBarEnabled(false);
            webview.setHorizontalScrollBarEnabled(false);
            webview.getSettings().setBuiltInZoomControls(false);

            webview.getSettings().setLoadsImagesAutomatically(true);
            webview.getSettings().setDomStorageEnabled(true);
            webview.setInitialScale(1);
            webview.getSettings().setUseWideViewPort(true);

            // webview.loadData(html, "text/html; charset=utf-8", "UTF-8");
            webview.loadUrl(value_split[0]); //receiptUrl
            webview.addJavascriptInterface(new Object() {
                @JavascriptInterface // For API 17+
                public void performClick(String message) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (message.equalsIgnoreCase("cancel")) {
                                confirmationGetObjet.onCancel(cusdialog);
                            } else {
                                HashMap<String, Object> objectsHashMap = new HashMap<>();
                                objectsHashMap.put("dialog", cusdialog);
                                objectsHashMap.put("data", message);
                                confirmationGetObjet.onOk(objectsHashMap);
                            }
                        }
                    });
                }
            }, "ok");
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(cusdialog.getWindow().getAttributes());
            lp.width = (WindowManager.LayoutParams.MATCH_PARENT);
            lp.height = (WindowManager.LayoutParams.WRAP_CONTENT);
            cusdialog.getWindow().setAttributes(lp);

            webview.setWebViewClient(new WebViewClient() {
                final ProgressDialog progressBar = ProgressDialog.show(context, null, " Please wait...", false, false);

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    if (progressBar != null && !progressBar.isShowing()) progressBar.show();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    Utility.dismissDialog(context, progressBar);
                    if (!cusdialog.isShowing()) cusdialog.show();
                }

                @TargetApi(android.os.Build.VERSION_CODES.M)
                public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                    Utility.dismissDialog(context, progressBar);

                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(context, Utility.getStackTrace(e));
        }

    }


    public static void showScheduleMandateDialog(ConfirmationGetObjet confirmationGetObjet, Context context, String Msg, String title, boolean backBtnCloseDialog, String... buttons) {
        String leftButton = (buttons.length == 0 ? "Yes" : buttons[0]);//(leftButton ==null?"Modify": leftButton);
        String rightButton = (buttons.length <= 1 ? "No" : buttons[1]);//(rightButton==null?"Next":rightButton);
        try {
            final Dialog cusdialog = new Dialog(context);
            cusdialog.requestWindowFeature(1);
            cusdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cusdialog.setContentView(R.layout.manual_schedule_mandate);
            cusdialog.setCanceledOnTouchOutside(false);
            cusdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            cusdialog.setCancelable(backBtnCloseDialog);


            TextView titletext = cusdialog.findViewById(R.id.title);
            TextView msg = cusdialog.findViewById(R.id.message);
            EditText days = cusdialog.findViewById(R.id.days);
            EditText datetext = cusdialog.findViewById(R.id.date);
            EditText amount = cusdialog.findViewById(R.id.amount);

            Button yes = cusdialog.findViewById(R.id.button1);
            Button no = cusdialog.findViewById(R.id.button2);

            yes.setText(leftButton);
            no.setText(rightButton);


            if (title == null || title.equals("")) titletext.setVisibility(View.GONE);
            if (Msg == null || Msg.equals("")) titletext.setVisibility(View.GONE);

            titletext.setText(title);
            msg.setText(Msg);

            days.addTextChangedListener(new TextWatcher() {
                // the user's changes are saved here
                public void onTextChanged(CharSequence c, int start, int before, int count) {
                }

                public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                    // this space intentionally left blank
                }

                public void afterTextChanged(Editable c) {
                    // this one too
                    if (!c.toString().equals("")) {
                        String date = Utility.convertDate2String(Utility.addDayInSelectDate(new Date(), Integer.parseInt(c.toString())), "dd/MM/yyyy");
                        datetext.setText(date);
                    } else {
                        datetext.setText(null);
                    }
                }
            });

            datetext.setClickable(false);
            datetext.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                        try {
                            Calendar c = Calendar.getInstance();
                            if (!datetext.getText().toString().equals("")) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                                c.setTime(sdf.parse(datetext.getText().toString()));
                            }
                            DatePickerDialog datePickerDialog = Utility.DatePickerReturnDate(context, c, new DateInterface((DateInterface.OnSuccess) (stringdate) -> {
                                datetext.setError(null);
                                days.setError(null);
                                datetext.setText(stringdate);
                                days.setText(Utility.getTwoDatedeff(Utility.removeTime(new Date()), Utility.convertString2Date(stringdate, "dd/MM/yyyy")) + "");
                                amount.requestFocus();

                            }));

                            Date bookingDate = new Date();
                            datePickerDialog.getDatePicker().setMinDate(Utility.addDayInSelectDate(bookingDate, 1).getTime());
                            datePickerDialog.show();
                        } catch (Exception e) {
                            ExceptionsNotification.ExceptionHandling(context, Utility.getStackTrace(e));
                        }
                        return true;
                    }
                    return false;
                }
            });

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        days.setError(null);
                        datetext.setError(null);
                        amount.setError(null);

                        boolean validation = true;
                        if (days.getText().toString().equals("")) {
                            days.setError(ErrorMsg.user_Registration_Filed_Required);
                            validation = false;

                        }
                        if (datetext.getText().toString().equals("")) {
                            datetext.setError(ErrorMsg.user_Registration_Filed_Required);
                            validation = false;

                        }
                        if (amount.getText().toString().equals("")) {
                            amount.setError(ErrorMsg.user_Registration_Filed_Required);
                            validation = false;

                        }

                        if (validation) {

                            HashMap<String, Object> objectsHashMap = new HashMap<>();
                            objectsHashMap.put("dialog", cusdialog);

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("day", days.getText().toString());
                            jsonObject.put("amount", amount.getText().toString());
                            objectsHashMap.put("data", jsonObject.toString());

                            confirmationGetObjet.onOk(objectsHashMap);
                        }
                    } catch (Exception e) {
                        ExceptionsNotification.ExceptionHandling(context, Utility.getStackTrace(e));
                    }
                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmationGetObjet.onCancel(cusdialog);

                }
            });

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(cusdialog.getWindow().getAttributes());
            lp.width = (WindowManager.LayoutParams.MATCH_PARENT);
            lp.height = (WindowManager.LayoutParams.WRAP_CONTENT);

            if (!cusdialog.isShowing()) cusdialog.show();
            cusdialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(context, Utility.getStackTrace(e));
        }
    }


    public static void showSingleButtonBigContentDialog(Context context, ConfirmationDialogInterface confirmationDialogInterface, String title, String msg, String... buttons) {

        String leftButton = (buttons.length == 0 ? "OK" : buttons[0]);//(leftButton ==null?"Modify": leftButton);
        final Dialog var3 = new Dialog(context);


        var3.requestWindowFeature(1);
        var3.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        var3.setContentView(R.layout.singlebutton_bigcontent_dialog);
        var3.setCanceledOnTouchOutside(false);
        //   var3.setCancelable(false);

        var3.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView title_text = (TextView) var3.findViewById(R.id.title);

        if (title == null || title.equals("")) {
            title_text.setVisibility(View.GONE);
        } else {
            title_text.setText(title);
            title_text.setVisibility(View.VISIBLE);
        }
        TextView msg_text = (TextView) var3.findViewById(R.id.message);
        msg_text.setText(msg);
        Button button = (Button) var3.findViewById(R.id.btn);
        button.setText(leftButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View var) {
                confirmationDialogInterface.onOk(var3);
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(var3.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        if (!((Activity) context).isFinishing() && !var3.isShowing()) var3.show();
        var3.getWindow().setAttributes(lp);
    }


    public static void showDoubleButtonBigContentDialog(Context context, BigContentDialogIntetface bigContentDialogIntetface, String title, String msg, String... buttons) {

        String leftButton = (buttons.length == 0 ? "Modify" : buttons[0]);//(leftButton ==null?"Modify": leftButton);
        String rightButton = (buttons.length <= 1 ? "Next" : buttons[1]);//(rightButton==null?"Next":rightButton);
        Dialog var3 = new Dialog(context);


        var3.requestWindowFeature(1);
        var3.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        var3.setContentView(R.layout.doublebutton_bigcontent_dialog);
        var3.setCanceledOnTouchOutside(false);
        //   var3.setCancelable(false);

        var3.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView title_text = (TextView) var3.findViewById(R.id.title);

        if (title == null || title.equals("")) {
            title_text.setVisibility(View.GONE);
        } else {
            title_text.setText(title);
            title_text.setVisibility(View.VISIBLE);
        }
        TextView msg_text = (TextView) var3.findViewById(R.id.message);
        msg_text.setText(msg);
        Button button1 = (Button) var3.findViewById(R.id.button1);
        button1.setText(leftButton);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View var) {
                bigContentDialogIntetface.button1(var3);
            }
        });


        Button button2 = (Button) var3.findViewById(R.id.button2);
        button2.setText(rightButton);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View var) {
                bigContentDialogIntetface.button2(var3);
            }
        });
        if (buttons.length == 1) {
            button2.setVisibility(View.GONE);
        }

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(var3.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        if (!((Activity) context).isFinishing() && !var3.isShowing()) var3.show();
        var3.getWindow().setAttributes(lp);


    }



    public static void showCustomerAddressDialog(Context context, DMRC_Customer_CardVO dmrc_customer_cardVO, CallBackInterface callBackInterface, String title, String... buttons){
        try {
            String btnName= (buttons.length==0 ?"Proceed":buttons[0]);//(leftButton ==null?"Modify": leftButton);
            Dialog customDialog = new Dialog(context);


            customDialog.requestWindowFeature(1);
            customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            customDialog.setContentView(R.layout.customer_address_dialog);
            customDialog.setCanceledOnTouchOutside(false);
            // var3.setCancelable(false);
            customDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(customDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity= Gravity.CENTER;
            customDialog.getWindow().setAttributes(lp);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                customDialog.getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
            }
            if(!((Activity)context).isFinishing() && !customDialog.isShowing())  customDialog.show();


            TextView title_text = (TextView)customDialog.findViewById(R.id.title_text);

            EditText customername=customDialog.findViewById(R.id.customername);
            EditText mobilenumber=customDialog.findViewById(R.id.mobilenumber);
            EditText pin=customDialog.findViewById(R.id.pin);
            EditText  city=customDialog.findViewById(R.id.city);
            EditText state=customDialog.findViewById(R.id.state);
            EditText permanentaddress=customDialog.findViewById(R.id.permanentaddress);
            EditText email = customDialog.findViewById(R.id.email);

            ViewGroup parent = (ViewGroup) email.getParent();
            if (parent != null) {
                parent.setVisibility(View.GONE);
            }

            Button verify=customDialog.findViewById(R.id.verify);
            pin.setInputType(InputType.TYPE_CLASS_NUMBER);
            city.setKeyListener(null);
            state.setKeyListener(null);
            city.setShowSoftInputOnFocus(false);
            state.setShowSoftInputOnFocus(false);


            if(title==null || title.equals("")){
                title_text.setVisibility(View.GONE);
            }else {
                title_text.setText(title);
                title_text.setVisibility(View.VISIBLE);
            }

            verify.setText(btnName);
            customername.setText(dmrc_customer_cardVO.getCustomerName());
            mobilenumber.setText(dmrc_customer_cardVO.getMobileNumber());
            permanentaddress.setText(dmrc_customer_cardVO.getAddress());

            pin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean gainFocus) {
                    //onFocus
                    if (gainFocus) {
                        //set the row background to a different color
                    }
                    //onBlur
                    else {
                        if (pin.length() < 6) {
                            pin.setError(Utility.getErrorSpannableString(context,  ErrorMsg.Pincode_6_characters_Error_Message));
                            city.setText("");
                            state.setText("");
                        }
                    }
                }
            });

            pin.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (pin.length() == 6) {
                        DMRCApi.getCityByPincodeForDMRC(context,pin.getText().toString().trim(),new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
                            CityVO cityVO = (CityVO) success;
                            city.setText(cityVO.getCityName());
                            state.setText(cityVO.getStateRegion().getStateRegionName());
                            city.setError(null);
                            state.setError(null);
                            pin.setError(null);

                            Utility.hideKeyBoardByView(context,customDialog.getCurrentFocus());
                        },(VolleyResponse.OnError)(error)->{
                            city.setText("");
                            state.setText("");
                            pin.setError(error);
                        }));
                    }
                }
                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            pin.setText(dmrc_customer_cardVO.getPincode());

            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText[] editTexts= {customername,mobilenumber,pin,city,state,permanentaddress};
                    if(Utility.setErrorOnEdittext(editTexts)){
                        if(Utility.validatePattern(mobilenumber.getText().toString().trim(), ApplicationConstant.MOBILENO_VALIDATION)!=null){
                            mobilenumber.setError(Utility.validatePattern(mobilenumber.getText().toString().trim(), ApplicationConstant.MOBILENO_VALIDATION));
                            return;
                        }
                        if(pin.getText().toString().trim().length()<6){
                            pin.setError("Pincode is Wrong");
                            return;
                        }
                        Utility.dismissDialog(context,customDialog);

                        dmrc_customer_cardVO.setAddress(permanentaddress.getText().toString().trim());
                        dmrc_customer_cardVO.setMobileNumber(mobilenumber.getText().toString().trim());
                        dmrc_customer_cardVO.setCustomerName(customername.getText().toString().trim());
                        dmrc_customer_cardVO.setPincode(pin.getText().toString().trim());
                        callBackInterface.onSuccess(dmrc_customer_cardVO);

                    }
                }
            });
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }



    public static void showImageSingleButtonDialog(Context context, String title, String msg , int drawableImage,ConfirmationDialogInterface confirmationDialogInterface , String... buttons){

        String btnName= (buttons.length==0 ?"OK":buttons[0]);//(leftButton ==null?"Modify": leftButton);

        Dialog customDialog = new Dialog(context);
        customDialog.requestWindowFeature(1);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        customDialog.setContentView(R.layout.image_alert_dialog);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.setCancelable(false);
        customDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        TextView dialog_title = (TextView)customDialog.findViewById(R.id.title);
        ImageView dialog_imageView = customDialog.findViewById(R.id.dialog_image);
        TextView dialog_msg = (TextView)customDialog.findViewById(R.id.message);
        Button dialog_btn = (Button)customDialog.findViewById(R.id.btn);

        dialog_title.setText(title);
        dialog_msg.setText(msg);
        dialog_btn.setText(btnName);
        dialog_imageView.setImageDrawable(Utility.getDrawableResources(context,drawableImage));
        dialog_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View var) {
                confirmationDialogInterface.onOk(customDialog);
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(customDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        customDialog.getWindow().setAttributes(lp);
        if(!((Activity)context).isFinishing() && !customDialog.isShowing())  customDialog.show();
    }





    public static void changeCustomerAddressDialog(Context context, CustomerVO customerVO, boolean isCancelBackBtn,CallBackInterface callBackInterface, String title, String... buttons){
        try {
            String btnName= (buttons.length==0 ?"Proceed":buttons[0]);//(leftButton ==null?"Modify": leftButton);
            Dialog customDialog = new Dialog(context);


            customDialog.requestWindowFeature(1);
            customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            customDialog.setContentView(R.layout.customer_address_dialog);
           // customDialog.setCanceledOnTouchOutside(false);
            customDialog.setCancelable(isCancelBackBtn);
            customDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(customDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity= Gravity.CENTER;
            customDialog.getWindow().setAttributes(lp);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                customDialog.getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
            }
            if(!((Activity)context).isFinishing() && !customDialog.isShowing())  customDialog.show();


            TextView title_text = (TextView)customDialog.findViewById(R.id.title_text);

            EditText customername=customDialog.findViewById(R.id.customername);
            EditText mobilenumber=customDialog.findViewById(R.id.mobilenumber);
            EditText pin=customDialog.findViewById(R.id.pin);
            EditText  city=customDialog.findViewById(R.id.city);
            EditText state=customDialog.findViewById(R.id.state);
            EditText permanentaddress=customDialog.findViewById(R.id.permanentaddress);
            Button verify=customDialog.findViewById(R.id.verify);
            EditText email = customDialog.findViewById(R.id.email);

            pin.setInputType(InputType.TYPE_CLASS_NUMBER);
            city.setKeyListener(null);
            state.setKeyListener(null);
            city.setShowSoftInputOnFocus(false);
            state.setShowSoftInputOnFocus(false);


            if(title==null || title.equals("")){
                title_text.setVisibility(View.GONE);
            }else {
                title_text.setText(title);
                title_text.setVisibility(View.VISIBLE);
            }

            verify.setText(btnName);
            customername.setText(customerVO.getName());
            mobilenumber.setText(customerVO.getMobileNumber());
            permanentaddress.setText(customerVO.getAddress1());
            email.setText(customerVO.getEmailId());

            pin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean gainFocus) {
                    //onFocus
                    if (gainFocus) {
                        //set the row background to a different color
                    }
                    //onBlur
                    else {
                        if (pin.length() < 6) {
                            pin.setError(Utility.getErrorSpannableString(context,  ErrorMsg.Pincode_6_characters_Error_Message));
                            city.setText("");
                            state.setText("");
                        }
                    }
                }
            });

            pin.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (pin.length() == 6) {
                        DMRCApi.pinCodebycity(context,pin.getText().toString().trim(),new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
                            CityVO cityVO = (CityVO) success;
                            city.setText(cityVO.getCityName());
                            state.setText(cityVO.getStateRegion().getStateRegionName());
                            city.setError(null);
                            state.setError(null);
                            pin.setError(null);

                            Utility.hideKeyBoardByView(context,customDialog.getCurrentFocus());
                        },(VolleyResponse.OnError)(error)->{
                            city.setText("");
                            state.setText("");
                            pin.setError(error);
                        }));
                    }
                }
                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            pin.setText(customerVO.getPincode());

            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText[] editTexts= {customername,mobilenumber,pin,city,state,permanentaddress,email};
                    if(Utility.setErrorOnEdittext(editTexts)){
                        if(Utility.validatePattern(mobilenumber.getText().toString().trim(), ApplicationConstant.MOBILENO_VALIDATION)!=null){
                            mobilenumber.setError(Utility.validatePattern(mobilenumber.getText().toString().trim(),ApplicationConstant.MOBILENO_VALIDATION));
                            return;
                        }
                        if(!email.getText().toString().trim().equals("") && Utility.validatePattern(email.getText().toString().trim(), ApplicationConstant.EMAIL_VALIDATION)!=null){
                            email.setError(Utility.validatePattern(email.getText().toString().trim(),ApplicationConstant.EMAIL_VALIDATION));
                            return;
                        }
                        if(pin.getText().toString().trim().length()<6){
                            pin.setError("Pincode is Wrong");
                            return;
                        }

                        CustomerVO customerVO1 = new CustomerVO();
                        customerVO1.setName(customername.getText().toString().trim());
                        customerVO1.setMobileNumber(mobilenumber.getText().toString().trim());
                        customerVO1.setAddress1(permanentaddress.getText().toString().trim());
                        customerVO1.setPincode(pin.getText().toString().trim());
                        customerVO1.setEmailId(email.getText().toString().trim());

                        HashMap<String, Object> objectsHashMap = new HashMap<>();
                        objectsHashMap.put("dialog", customDialog);
                        objectsHashMap.put("customer", customerVO1);
                        callBackInterface.onSuccess(objectsHashMap);
                    }
                }
            });
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }



    public static void addBankDetails(Context context, String headingText,boolean backBtnCloseDialog, ConfirmationGetObjet confirmationGetObjet) {
        final Dialog cusdialog = new Dialog(context);
        cusdialog.requestWindowFeature(1);
        Objects.requireNonNull(cusdialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));
        cusdialog.setContentView(R.layout.activity_add_bank_details);
        cusdialog.setCanceledOnTouchOutside(false);
        cusdialog.setCancelable(backBtnCloseDialog);
        cusdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        EditText fullName,accountNumber,confirmAC,ifscCode;
        Button addBeneficiary;
        TextView textHeading;

        fullName=cusdialog.findViewById(R.id.fullName);
        accountNumber=cusdialog.findViewById(R.id.accountNumber);
        confirmAC=cusdialog.findViewById(R.id.confirmAC);
        ifscCode=cusdialog.findViewById(R.id.ifscCode);
        addBeneficiary=cusdialog.findViewById(R.id.addBeneficiary);
        textHeading=cusdialog.findViewById(R.id.textHeading);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(cusdialog.getWindow().getAttributes());
        lp.width = (WindowManager.LayoutParams.MATCH_PARENT);
        lp.height = (WindowManager.LayoutParams.WRAP_CONTENT);

        if(headingText==null || headingText.isEmpty()){
            textHeading.setVisibility(View.GONE);
        }else {
            textHeading.setVisibility(View.VISIBLE);
            textHeading.setText(headingText);
        }



        addBeneficiary.setOnClickListener(new View.OnClickListener() {
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

                if(fullName.getText().toString().isEmpty()){
                    fullName.setError("Full Name cannot be left blank");
                    return;
                }
                if(fullName.length()<3) {
                    fullName.setError("Full Name should be more than 2 letters");
                    return;
                }

                RefundVO refundVO = new RefundVO();
                refundVO.setName(fullName.getText().toString().trim());
                refundVO.setIfscCode(ifscCode.getText().toString().trim());
                refundVO.setAccountNumber(accountNumber.getText().toString().trim());

                HashMap<String,Object> objectHashMap = new HashMap<>();
                objectHashMap.put("dialog",cusdialog);
                objectHashMap.put("data",refundVO);

                confirmationGetObjet.onOk(objectHashMap);

            }
        });
        if (!((Activity) context).isFinishing() && !cusdialog.isShowing()) cusdialog.show();
        cusdialog.getWindow().setAttributes(lp);
    }




    public static void sendPaymentDialog(Context context, boolean backBtnCloseDialog, View view,BeneAccVO beneAccVO, ConfirmationGetObjet confirmationGetObjet) {
        final Dialog cusdialog = new Dialog(context);
        cusdialog.requestWindowFeature(1);
        Objects.requireNonNull(cusdialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));
        cusdialog.setContentView(R.layout.send_payment_dialog);
        cusdialog.setCanceledOnTouchOutside(false);
        cusdialog.setCancelable(backBtnCloseDialog);
        cusdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        EditText fullName,accountNumber,ifscCode,amount;
        Button addBeneficiary;
        TextView textHeading;

        fullName=cusdialog.findViewById(R.id.fullName);
        accountNumber=cusdialog.findViewById(R.id.accountNumber);
        ifscCode=cusdialog.findViewById(R.id.ifscCode);
        addBeneficiary=cusdialog.findViewById(R.id.addBeneficiary);
        textHeading=cusdialog.findViewById(R.id.heading);
        amount=cusdialog.findViewById(R.id.amount);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(cusdialog.getWindow().getAttributes());
        lp.width = (WindowManager.LayoutParams.MATCH_PARENT);
        lp.height = (WindowManager.LayoutParams.WRAP_CONTENT);
        cusdialog.getWindow().setAttributes(lp);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            cusdialog.getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }


        if(beneAccVO.getDialogTitle()==null || beneAccVO.getDialogTitle().equals("")){
            textHeading.setVisibility(View.GONE);
        }else {
            textHeading.setVisibility(View.VISIBLE);
            textHeading.setText(beneAccVO.getDialogTitle());
        }
        fullName.setText(beneAccVO.getBeneNameRespose());
        accountNumber.setText(beneAccVO.getBeneficialAccountNum());
        ifscCode.setText(beneAccVO.getBeneficialIFSCcode());

        fullName.setEnabled(false);
        accountNumber.setEnabled(false);
        ifscCode.setEnabled(false);


        addBeneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validate=true;
                if(accountNumber.getText().toString().isEmpty()){
                    accountNumber.setError("Account Number cannot be left blank");
                    validate=false;
                }
                if(accountNumber.length()<7 || accountNumber.length()>18) {
                    accountNumber.setError("Account Number  should be of 7-18 digits");
                    validate=false;
                }
                if(ifscCode.getText().toString().isEmpty()){
                    ifscCode.setError("IFSC Code cannot be left blank");
                    validate=false;
                }
                if(fullName.getText().toString().isEmpty()){
                    fullName.setError("Full Name cannot be left blank");
                    validate=false;
                }
                if(fullName.length()<3) {
                    fullName.setError("Full Name should be more than 2 letters");
                    validate=false;
                }
                if(amount.getText().toString().isEmpty()){
                    amount.setError("Amount Number cannot be left blank");
                    validate=false;
                }
                if(!validate) return;

                BeneAccVO accVO = new BeneAccVO();
                accVO.setBeneNameRespose(fullName.getText().toString().trim());
                accVO.setBeneficialIFSCcode(ifscCode.getText().toString().trim());
                accVO.setBeneficialAccountNum(accountNumber.getText().toString().trim());
                accVO.setAnonymousAmount(Double.parseDouble(amount.getText().toString().trim()));
                accVO.setBeneficialAccountId(beneAccVO.getBeneficialAccountId());

                HashMap<String,Object> objectHashMap = new HashMap<>();
                objectHashMap.put("dialog",cusdialog);
                objectHashMap.put("data",accVO);

                confirmationGetObjet.onOk(objectHashMap);

            }
        });
        if (!((Activity) context).isFinishing() && !cusdialog.isShowing()){
            cusdialog.show();
            if(view !=null)Utility.enableDisableView(view,true);

            // focus set and open keyboard
            amount.requestFocus();
            cusdialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
      }




    public static void txnDialogAutope(Context context, String headingText, String messageText, boolean backBtnCloseDialog, Map<String, Integer> stringIntegerMap, ConfirmationGetObjet confirmationGetObjet, String ... btn) {

        try {
            String btnName= (btn.length==0 ?"Ok":btn[0]);//(leftButton ==null?"Modify": leftButton);

            Drawable background = Utility.getDrawableResources(context,stringIntegerMap.get("background"));
            Drawable icon = Utility.getDrawableResources(context,stringIntegerMap.get("icon"));
            Drawable btn_background = Utility.getDrawableResources(context,stringIntegerMap.get("btn_background"));
            int btn_color = stringIntegerMap.get("btn_color");

            final Dialog cusdialog = new Dialog(context);
            cusdialog.requestWindowFeature(1);
            Objects.requireNonNull(cusdialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));
            cusdialog.setContentView(R.layout.txn_dialog_design);
            cusdialog.setCanceledOnTouchOutside(false);
            cusdialog.setCancelable(backBtnCloseDialog);
            cusdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            Button buttonOk;
            TextView textHeading,message;
            LinearLayout iconHeadingLayout;
            ImageView imageView;


            final Animation animation = AnimationUtils.loadAnimation(context,R.anim.bounce);

            textHeading=cusdialog.findViewById(R.id.textHeading);
            message=cusdialog.findViewById(R.id.message);
            buttonOk=cusdialog.findViewById(R.id.buttonOk);
            iconHeadingLayout=cusdialog.findViewById(R.id.iconHeadingLayout);
            imageView=cusdialog.findViewById(R.id.imageView);

            iconHeadingLayout.setBackground(background);
            //imageView.setAnimation(R.raw.success);
            imageView.setImageDrawable(icon);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(cusdialog.getWindow().getAttributes());
            lp.width = (WindowManager.LayoutParams.MATCH_PARENT);
            lp.height = (WindowManager.LayoutParams.WRAP_CONTENT);

            if(headingText==null || headingText.isEmpty()){
                textHeading.setVisibility(View.GONE);
            }else {
                textHeading.setVisibility(View.VISIBLE);
                textHeading.setText(headingText);
                textHeading.setAnimation(animation);
            }
            if(messageText==null || messageText.isEmpty()){
                message.setVisibility(View.GONE);
            }else {
                message.setVisibility(View.VISIBLE);
                message.setText(messageText);
            }
            buttonOk.setText(btnName);
            buttonOk.setBackground(btn_background);
            buttonOk.setTextColor(context.getResources().getColor(btn_color));
            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmationGetObjet.onOk(cusdialog);
                }
            });

            if (!((Activity) context).isFinishing() && !cusdialog.isShowing()) cusdialog.show();
            cusdialog.getWindow().setAttributes(lp);
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(context, Utility.getStackTrace(e));
        }
    }



    public static void showCheckBoxSingleButtonDialog(Context context, String title, String msg , ConfirmationGetObjet confirmationGetObjet , String... buttons){

        String btnName= (buttons.length==0 ?"OK":buttons[0]);//(leftButton ==null?"Modify": leftButton);

        Dialog customDialog = new Dialog(context);
        customDialog.requestWindowFeature(1);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        customDialog.setContentView(R.layout.design_check_box_dialog);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.setCancelable(true);
        customDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        TextView dialog_title = (TextView)customDialog.findViewById(R.id.title);
        CheckBox dialog_checkbox = customDialog.findViewById(R.id.condition_checkbox);
        TextView dialog_checkbox_text = (TextView)customDialog.findViewById(R.id.condition_text);
        Button dialog_btn = (Button)customDialog.findViewById(R.id.btn);

        if(title==null){
            dialog_title.setVisibility(View.GONE);
        }else {
            dialog_title.setText(title);
            dialog_title.setVisibility(View.VISIBLE);
        }

        dialog_checkbox_text.setText(msg);
        dialog_btn.setText(btnName);
        dialog_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View var) {
                Utility.dismissDialog(context,customDialog);
                if(dialog_checkbox.isChecked()){
                    confirmationGetObjet.onOk(1);
                }else {
                    confirmationGetObjet.onOk(0);
                }
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(customDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        customDialog.getWindow().setAttributes(lp);
        if(!((Activity)context).isFinishing() && !customDialog.isShowing())  customDialog.show();
    }



}
