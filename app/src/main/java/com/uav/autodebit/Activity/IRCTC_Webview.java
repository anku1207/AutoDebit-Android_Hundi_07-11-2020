package com.uav.autodebit.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import com.uav.autodebit.BO.IRCTCBO;
import com.uav.autodebit.BO.PaymentGateWayBO;
import com.uav.autodebit.IRCTC.IRCTCApi;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.AuthServiceProviderVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.PreMandateRequestVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class IRCTC_Webview extends AppCompatActivity implements View.OnClickListener , MyJavaScriptInterface.javascriptinterface {

    public static final String EXTRAS_TITLE = "title";
    public static final String EXTRAS_URL = "url";
    public static final String EXTRAS_FAIL_URL = "fail_url";
    public static final String EXTRAS_SUCCESS_URL = "success_url";
    public static final String EXTRAS_ENACH_MANDATE="enach_mandate_url";
    public static final String EXTRAS_PAY_MODE="pay_mode";

    String fail_url,success_url,url,enach_mandate_url,pay_mode=null;

    TextView title;
    ImageView back_activity_button;
    WebView webView, newWebView;
    ProgressDialog progressBar;
    Boolean doubleBackpress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_r_c_t_c__webview);
        if(getSupportActionBar()!=null) getSupportActionBar().hide();

        title=findViewById(R.id.title);
        back_activity_button = findViewById(R.id.back_activity_button);
        back_activity_button.setOnClickListener(this);

        progressBar = new UAVProgressDialog(this);


        title.setText(getIntent().getStringExtra(EXTRAS_TITLE));
        url=getIntent().getStringExtra(EXTRAS_URL);
        fail_url=getIntent().getStringExtra(EXTRAS_FAIL_URL)+ "app/";
        success_url=getIntent().getStringExtra(EXTRAS_SUCCESS_URL)+ "app/";
        enach_mandate_url=getIntent().getStringExtra(EXTRAS_ENACH_MANDATE);
        pay_mode=getIntent().getStringExtra(EXTRAS_PAY_MODE);


        Log.w("call_url",url);

        openWebView(url+"&app=1&pay_mode="+pay_mode);
    }


    @SuppressLint("SetJavaScriptEnabled")
    void openWebView(final String receiptUrl) {
        Log.w("callUrl", receiptUrl);
        webView=findViewById(R.id.webView);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new IRCTC_Webview.MyBrowser());

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
                if (!IRCTC_Webview.this.isFinishing() && progressBar != null && !progressBar.isShowing()) {
                    try {
                        progressBar.show();
                    } catch (Exception e) {

                    }
                }
                if (newProgress == 100) {
                    Utility.dismissDialog(IRCTC_Webview.this, progressBar);
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
                newWebView = new WebView(IRCTC_Webview.this);
                newWebView.getSettings().setJavaScriptEnabled(true);
                newWebView.getSettings().setBuiltInZoomControls(true);
                newWebView.setInitialScale(1);
                newWebView.getSettings().setUseWideViewPort(true);
                newWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                //for download PDF files
                newWebView.setDownloadListener(new IRCTC_Webview.MyDownLoadListener());


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
                        if (!IRCTC_Webview.this.isFinishing() && progressBar != null && !progressBar.isShowing()) {
                            try {
                                progressBar.show();
                            } catch (Exception e) {
                            }
                        }
                        if (newProgress == 100) {
                            Utility.dismissDialog(IRCTC_Webview.this, progressBar);
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
                            ExceptionsNotification.ExceptionHandling(IRCTC_Webview.this, Utility.getStackTrace(e), "0");
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
                    ExceptionsNotification.ExceptionHandling(IRCTC_Webview.this, Utility.getStackTrace(e), "0");
                }
                Utility.dismissDialog(IRCTC_Webview.this, progressBar);
            }
        });
        //for download PDF files
        webView.setDownloadListener(new MyDownLoadListener());

        webView.setWebViewClient(new IRCTC_Webview.MyBrowser());
        webView.addJavascriptInterface(new MyJavaScriptInterface(this), "HTMLOUT");
        webView.loadUrl(receiptUrl); //receiptUrl

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
            }else if(url.equalsIgnoreCase(enach_mandate_url)){
                IRCTCApi.createIRCTCBankMandateRequest(IRCTC_Webview.this,Integer.parseInt(Session.getCustomerId(IRCTC_Webview.this)),ApplicationConstant.IRCTC,new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
                    PreMandateRequestVO preMandateRequestVO = (PreMandateRequestVO) success;
                    Intent intent = new Intent(IRCTC_Webview.this,Enach_Mandate.class);
                    intent.putExtra("forresutl",true);
                    intent.putExtra("selectservice",new ArrayList<Integer>(Arrays.asList(ApplicationConstant.IRCTC)));
                    intent.putExtra("id",preMandateRequestVO.getRequestId());
                    startActivityForResult(intent, ApplicationConstant.REQ_ENACH_MANDATE);
                }));




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
            Utility.dismissDialog(IRCTC_Webview.this, progressBar);
            try {
                if (url.equalsIgnoreCase(success_url) || url.equalsIgnoreCase(fail_url)) {
                    webView.setVisibility(View.GONE);
                    webView.loadUrl("javascript:HTMLOUT.showHTML(document.getElementById('resp').innerHTML);");
                    webView.loadUrl("javascript:console.log('MAGIC'+document.getElementById('resp').innerHTML);");
                }
            } catch (Exception e) {
                ExceptionsNotification.ExceptionHandling(IRCTC_Webview.this, Utility.getStackTrace(e));
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
        Utility.dismissDialog(IRCTC_Webview.this, progressBar);
        try {
            if (webView != null) {
                webView.scrollTo(0, 0);
            }
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(IRCTC_Webview.this, Utility.getStackTrace(e), "0");
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
        if (doubleBackpress){
            super.onBackPressed();
        }else {
            Toast.makeText(this, getString(R.string.backpress_to_exit), Toast.LENGTH_SHORT).show();
            doubleBackpress = true;
            new Handler().postDelayed(() -> doubleBackpress = false, 2000);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(resultCode==RESULT_OK){
                if(requestCode==ApplicationConstant.REQ_ENACH_MANDATE){

                    boolean enachMandateStatus=data.getBooleanExtra("mandate_status",false);
                    String mandateId=data.getStringExtra("bankMandateId");
                    int actionId=data.getIntExtra("actionId",0);
                    if(enachMandateStatus && actionId!=0 && mandateId!=null){
                        IRCTCApi.updateIRCTCBankMandateStatus(IRCTC_Webview.this,Integer.parseInt(mandateId),actionId,new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
                            Intent intent =new Intent();
                            setResult(RESULT_OK,intent);
                            finish();
                        }));
                    }else{
                        Utility.showSingleButtonDialog(IRCTC_Webview.this,"Alert",data.getStringExtra("msg"),false);
                    }
                }
            }else {
                if(requestCode==ApplicationConstant.REQ_ENACH_MANDATE){
                    finish();
                }
            }
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }
    }

    @Override
    public void htmlresult(String result) {
        try {
            JSONObject object = new JSONObject(result);
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = IRCTCBO.proceedIRCTCMandatePGResponse();

            String anonymousString = object.getString("anonymousString");//json SI response
            String anonymousInteger = object.getString("anonymousInteger");


            CustomerVO customerVO=new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(IRCTC_Webview.this)));
            //autope pg response
            customerVO.setAnonymousString(anonymousString);
            customerVO.setAnonymousInteger(Integer.parseInt(anonymousInteger));

            Gson gson =new Gson();
            String json = gson.toJson(customerVO);
            params.put("volley", json);

            Log.w("htmlresultRequest", json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(IRCTC_Webview.this, connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;
                    Log.w("htmlresult_resp",response.toString());
                    Gson gson = new Gson();
                    CustomerVO htmlRequestResp = gson.fromJson(response.toString(), CustomerVO.class);

                    if (!htmlRequestResp.getStatusCode().equals("200")) {
                        Utility.showSingleButtonDialog(IRCTC_Webview.this,htmlRequestResp.getDialogTitle(),htmlRequestResp.getErrorMsgs().get(0),true);
                    } else {
                        Intent intent =new Intent();
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(IRCTC_Webview.this , Utility.getStackTrace(e));
        }

    }
}