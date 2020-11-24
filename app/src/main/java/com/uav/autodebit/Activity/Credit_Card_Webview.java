package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.uav.autodebit.R;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;

public class Credit_Card_Webview extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRAS_TITLE = "title";
    public static final String EXTRAS_URL = "url";
    public static final String EXTRAS_FAIL_URL = "fail_url";
    public static final String EXTRAS_SUCCESS_URL = "success_url";
    public static final String EXTRAS_TXN_ID = "txn_id";
    String fail_url,success_url,url,txn_id=null;

    TextView title;
    ImageView back_activity_button;
    WebView webView, newWebView;
    ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit__card__webview);
        if(getSupportActionBar()!=null) getSupportActionBar().hide();



        title=findViewById(R.id.title);
        back_activity_button = findViewById(R.id.back_activity_button);
        back_activity_button.setOnClickListener(this);

        progressBar = new UAVProgressDialog(this);


        title.setText(getIntent().getStringExtra(EXTRAS_TITLE));
        url=getIntent().getStringExtra(EXTRAS_URL);
        txn_id=getIntent().getStringExtra(EXTRAS_TXN_ID);
        fail_url=getIntent().getStringExtra(EXTRAS_FAIL_URL)+ "app/";
        success_url=getIntent().getStringExtra(EXTRAS_SUCCESS_URL)+ "app/";
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_activity_button) {
            cancelTransaction();
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                cancelTransaction();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    private void cancelTransaction() {
        String[] buttons = {"Yes","No"};
        Utility.confirmationDialogTextType(new DialogInterface() {
            @Override
            public void confirm(Dialog dialog) {
                Utility.dismissDialog(Credit_Card_Webview.this, dialog);
                finish();
            }
            @Override
            public void modify(Dialog dialog) {
                Utility.dismissDialog(Credit_Card_Webview.this, dialog);
            }
        },Credit_Card_Webview.this,null,"Do you want to cancel the transaction","Cancel Transaction",buttons);
    }
}