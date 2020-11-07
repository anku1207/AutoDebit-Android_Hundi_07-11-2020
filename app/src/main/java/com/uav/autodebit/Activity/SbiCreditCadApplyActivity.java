package com.uav.autodebit.Activity;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.uav.autodebit.R;

public class SbiCreditCadApplyActivity extends Base_Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sbi_credit_cad_apply);
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();

    }

    public void onClickBackButton(View view) {
        finish();
    }
}