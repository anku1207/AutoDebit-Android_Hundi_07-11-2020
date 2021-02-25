package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.PaymentGateWayBO;
import com.uav.autodebit.BO.SiBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DirectPaymentActivity extends AppCompatActivity implements View.OnClickListener, MyJavaScriptInterface.javascriptinterface {
    public static final String EXTRAS_ENCRYPTED_VALUE = "encryptedValue";
    public static final String EXTRAS_ID = "action_id";
    public static final String EXTRAS_DIRECT_PAYMENT = "is_direct_payment";
    public static final String EXTRAS_TITLE = "title_activity";
    public static final String EXTRAS_SERVICE_TYPE_ID = "service_type_id";


    String encryptedValue,action_Id=null;

    String cancel_url, redirect_url,url=null;
    int isDirectPayment,service_type_id;

    TextView title;
    ImageView back_activity_button;
    WebView webView, newWebView;
    ProgressDialog progressBar;
    Boolean doubleBackpress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_payment);
        if(getSupportActionBar()!=null) getSupportActionBar().hide();

        title=findViewById(R.id.title);
        back_activity_button = findViewById(R.id.back_activity_button);
        back_activity_button.setOnClickListener(this);
        progressBar = new UAVProgressDialog(this);

        title.setText(getIntent().getStringExtra(EXTRAS_TITLE));
        encryptedValue=getIntent().getStringExtra(EXTRAS_ENCRYPTED_VALUE);
        action_Id=String.valueOf(getIntent().getIntExtra(EXTRAS_ID,0));
        isDirectPayment=getIntent().getIntExtra(EXTRAS_DIRECT_PAYMENT,0);
        service_type_id=getIntent().getIntExtra(EXTRAS_SERVICE_TYPE_ID,0);

        getDirectPaymentURL(DirectPaymentActivity.this,Session.getCustomerId(this),new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
            try {
                JSONObject jsonObject = (JSONObject) success;
                url=jsonObject.getString("url")+"?app=1&value="+encryptedValue+"&hasDirectPayment="+(isDirectPayment==1);
                Log.w("url_run",url);
                cancel_url =jsonObject.getString("cancelUrl")+ "app/";
                redirect_url =jsonObject.getString("redirectUrl")+ "app/";
                openWebView(url);
            }catch (Exception e){
                ExceptionsNotification.ExceptionHandling(this, Utility.getStackTrace(e));
            }
        }));
    }



    @SuppressLint("SetJavaScriptEnabled")
    void openWebView(final String receiptUrl) {
        Log.w("callUrl", receiptUrl);
        webView=findViewById(R.id.webView);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new MyBrowser());

        // webview.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.setInitialScale(1);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setMinimumFontSize(16);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
        settings.setDomStorageEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (!DirectPaymentActivity.this.isFinishing() && progressBar != null && !progressBar.isShowing()) {
                    try {
                        progressBar.show();
                    } catch (Exception e) {

                    }
                }
                if (newProgress == 100) {
                    Utility.dismissDialog(DirectPaymentActivity.this, progressBar);
                }
            }
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
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                newWebView = new WebView(DirectPaymentActivity.this);
                newWebView.getSettings().setJavaScriptEnabled(true);
                newWebView.getSettings().setBuiltInZoomControls(true);
                newWebView.setInitialScale(1);
                newWebView.getSettings().setUseWideViewPort(true);
                newWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                //for download PDF files
                newWebView.setDownloadListener(new DirectPaymentActivity.MyDownLoadListener());


                newWebView.setWebChromeClient(new WebChromeClient() {

                    @Override
                    public void onCloseWindow(WebView window) {
                        super.onCloseWindow(window);
                        if (newWebView != null) {
                            try {
                                webView.removeView(newWebView);
                            } catch (Exception e) {
                            }
                        }
                    }

                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        if (!DirectPaymentActivity.this.isFinishing() && progressBar != null && !progressBar.isShowing()) {
                            try {
                                progressBar.show();
                            } catch (Exception e) {
                            }
                        }
                        if (newProgress == 100) {
                            Utility.dismissDialog(DirectPaymentActivity.this, progressBar);
                        }
                    }
                });
                newWebView.setWebViewClient(new WebViewClient());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                newWebView.setLayoutParams(params);

                webView.addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        try {
                            if (url.contains(".pdf")) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.parse(url), "application/pdf");
                                try {
                                    if (newWebView != null) {
                                        try {
                                            webView.removeView(newWebView);
                                        } catch (Exception e) {
                                        }
                                    }
                                    view.getContext().startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    //user does not have a pdf viewer installed
                                }
                            } else {
                                view.loadUrl(url);
                            }
                            return true;
                        } catch (Exception e) {
                            return true;
                        }
                    }
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        Log.w("pagestart", url);
                    }
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        Log.w("newWebcie", url);
                        try {
                            if (webView != null) {
                                webView.scrollTo(0, 0);
                            }
                        } catch (Exception e) {
                            ExceptionsNotification.ExceptionHandling(DirectPaymentActivity.this, Utility.getStackTrace(e), "0");
                        }

                    }

                    @SuppressWarnings("deprecation")
                    public void onReceivedError(WebView view, int errorCode,
                                                String description, String failingUrl) {
                        showError(description, progressBar);
                    }

                    @TargetApi(android.os.Build.VERSION_CODES.M)
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        showError((String) error.getDescription(), progressBar);
                    }

                    @TargetApi(android.os.Build.VERSION_CODES.M)
                    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                        showError(errorResponse.getReasonPhrase().toString(), progressBar);
                    }
                });
                return true;
            }

            @Override
            public void onCloseWindow(WebView window) {
                super.onCloseWindow(window);
                try {
                    newWebView.destroy();
                } catch (Exception e) {
                    ExceptionsNotification.ExceptionHandling(DirectPaymentActivity.this, Utility.getStackTrace(e), "0");
                }
                Utility.dismissDialog(DirectPaymentActivity.this, progressBar);
            }
        });
        //for download PDF files
        webView.setDownloadListener(new DirectPaymentActivity.MyDownLoadListener());

        webView.setWebViewClient(new MyBrowser());
        webView.addJavascriptInterface(new MyJavaScriptInterface(this), "HTMLOUT");
        webView.loadUrl(receiptUrl); //receiptUrl

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                backBtnFun();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_activity_button) {
            backBtnFun();
        }
    }

    public void backBtnFun(){
        cancelTransaction();
    }


    private void cancelTransaction() {
        String[] buttons = {"Yes","No"};
        Utility.confirmationDialogTextType(new DialogInterface() {
            @Override
            public void confirm(Dialog dialog) {
                Utility.dismissDialog(DirectPaymentActivity.this, dialog);
                finish();
            }
            @Override
            public void modify(Dialog dialog) {
                Utility.dismissDialog(DirectPaymentActivity.this, dialog);
            }
        },DirectPaymentActivity.this,null, Content_Message.CANCEL_TRANSACTION,"Cancel Transaction",buttons);
    }

    @Override
    public void htmlresult(String result) {
        try {
            JSONObject object = new JSONObject(result);
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = SiBO.proceedAutoPePgResponse();
            CustomerAuthServiceVO customerAuthServiceVO = new CustomerAuthServiceVO();
            String anonymousString = object.getString("anonymousString");//json SI response
            String anonymousInteger = object.getString("anonymousInteger");

            CustomerVO customerVO = new CustomerVO();
            customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(DirectPaymentActivity.this)));
            customerAuthServiceVO.setCustomer(customerVO);
            customerAuthServiceVO.setAnonymousString(anonymousString);
            customerAuthServiceVO.setAnonymousInteger(Integer.parseInt(anonymousInteger));
            Gson gson = new Gson();
            String json = gson.toJson(customerAuthServiceVO);
            params.put("volley", json);

            Log.w("htmlresultRequest", json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(DirectPaymentActivity.this, connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {

                    JSONObject response = (JSONObject) resp;
                    Intent intent =new Intent();
                    setResult(RESULT_OK,intent);
                    intent.putExtra(EXTRAS_ID,action_Id);
                    intent.putExtra("data",response.toString());
                    finish();


                    /*JSONObject response = (JSONObject) resp;
                    CustomerVO customerVO1 = new Gson().fromJson(response.toString(),CustomerVO.class);
                    if(customerVO1.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) customerVO1.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(DirectPaymentActivity.this,customerVO1.getDialogTitle(),sb.toString(),true);
                    }else{
                        Intent intent =new Intent();
                        intent.putExtra(EXTRAS_ID,action_Id);
                        intent.putExtra("data",customerVO1.getAnonymousString());
                        setResult(RESULT_OK,intent);
                        finish();
                    }*/
                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(DirectPaymentActivity.this , Utility.getStackTrace(e));
        }


    }


    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.w("URL", url);

            if (url.contains(".pdf")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "application/pdf");
                try {
                    if (newWebView != null) {
                        try {
                            webView.removeView(newWebView);
                        } catch (Exception e) {
                        }
                    }
                    view.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    //user does not have a pdf viewer installed
                }
            }else if(url.contains("upi:")){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity( intent );
            }else{
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            Utility.dismissDialog(DirectPaymentActivity.this, progressBar);
            try {
                if (url.equalsIgnoreCase(redirect_url) || url.equalsIgnoreCase(cancel_url)) {
                    webView.setVisibility(View.GONE);
                    webView.loadUrl("javascript:HTMLOUT.showHTML(document.getElementById('resp').innerHTML);");
                    webView.loadUrl("javascript:console.log('MAGIC'+document.getElementById('resp').innerHTML);");
                }
            } catch (Exception e) {
                ExceptionsNotification.ExceptionHandling(DirectPaymentActivity.this, Utility.getStackTrace(e));
            }
        }

        @SuppressWarnings("deprecation")
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            showError(description, progressBar);
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            showError((String) error.getDescription(), progressBar);
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            showError(errorResponse.getReasonPhrase().toString(), progressBar);
        }
    }

    private void showError(String description, Dialog progressBar) {
        Utility.dismissDialog(DirectPaymentActivity.this, progressBar);
        try {
            if (webView != null) {
                webView.scrollTo(0, 0);
            }
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(DirectPaymentActivity.this, Utility.getStackTrace(e), "0");
        }
        Log.e("weverrir", description);
    }


    public class MyDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            if (url != null) {
                if (newWebView != null) {
                    try {
                        webView.removeView(newWebView);
                    } catch (Exception e) {
                    }
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        }
    }


    private void getDirectPaymentURL(Context context, String encryptedValue, VolleyResponse volleyResponse) {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = PaymentGateWayBO.getDirectpaymentURLs();

            CustomerVO customerVO=new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(encryptedValue));
            Gson gson =new Gson();
            String json = gson.toJson(customerVO);
            params.put("volley", json);
            Log.w("htmlresultRequest", json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(context, connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object response) throws JSONException {
                    JSONObject jsonObject = (JSONObject) response;

                    if (!jsonObject.getString("status").equals("200")) {
                        Utility.alertDialog(context, "Error !", jsonObject.getString("errorMsg"), "Ok");
                    } else  {
                        volleyResponse.onSuccess(jsonObject);
                    }
                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }
}