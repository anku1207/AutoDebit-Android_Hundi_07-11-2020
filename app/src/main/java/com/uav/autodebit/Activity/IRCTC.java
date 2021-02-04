package com.uav.autodebit.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uav.autodebit.BO.IRCTCBO;
import com.uav.autodebit.BO.MandateBO;
import com.uav.autodebit.IRCTC.IRCTCApi;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.IRCTC_Image_With_Text_RecyclerViewAdapter;
import com.uav.autodebit.adpater.OTM_With_Text_RecyclerViewAdapter;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerServicesVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.PreMandateRequestVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class IRCTC extends Base_Activity implements SwipeRefreshLayout.OnRefreshListener {
    ImageView back_activity_button;
    LinearLayout main_layout;
    int activeMandateCount;
    CustomerVO customerResponse;

    String customerServiceId;
    LinearLayout append_layout;
    SwipeRefreshLayout mSwipeRefreshLayout;
    DataAdapterVO selectMandateDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irctc);
        getSupportActionBar().hide();

        main_layout = findViewById(R.id.main_layout);
        back_activity_button = findViewById(R.id.back_activity_button);
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                // Fetching data from server
                // loadRecyclerViewData();
                getDetails();
            }
        });
    }

    @Override
    public void onRefresh() {
        // Fetching data from server
        setAppendLayout(IRCTC.this, selectMandateDataAdapter);
    }

    @SuppressLint("ResourceType")
    public void getDetails() {
        IRCTCApi.getIRCTCPGURLs(IRCTC.this, new VolleyResponse((VolleyResponse.OnSuccess) (success) -> {
            try {
                if (main_layout.getChildCount() > 0) main_layout.removeAllViews();
                customerResponse = (CustomerVO) success;

                if (customerResponse.isEventIs()) {
                    JSONArray jsonArrayMandateTypes = new JSONArray(customerResponse.getAnonymousString1());
                    RecyclerView recyclerView = new RecyclerView(this);
                    recyclerView.setNestedScrollingEnabled(true);
                    recyclerView.setHasFixedSize(true);
                    LinearLayout.LayoutParams layoutparamsRecyclerView = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    recyclerView.setLayoutParams(layoutparamsRecyclerView);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    linearLayoutManager.setReverseLayout(false);
                    linearLayoutManager.setStackFromEnd(false);
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    // recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

                    List<DataAdapterVO> dataAdapterVOS = new ArrayList<>();
                    for (int i = 0; i < jsonArrayMandateTypes.length(); i++) {
                        JSONObject jsonObject = jsonArrayMandateTypes.getJSONObject(i);
                        DataAdapterVO dataAdapterVO = new DataAdapterVO();
                        dataAdapterVO.setImagename(jsonObject.getString("icon"));
                        dataAdapterVO.setText(jsonObject.getString("name"));
                        dataAdapterVO.setId(jsonObject.getInt("id"));
                        dataAdapterVO.setNumber(jsonObject.getInt("mandatecount") + "");
                        dataAdapterVO.setDefaultMandate(jsonObject.getBoolean("defaultMandate"));
                        dataAdapterVO.setServiceName(jsonObject.getString("pay_mode"));
                        dataAdapterVOS.add(dataAdapterVO);
                    }
                    main_layout.addView(recyclerView);
                    //add divider
                    View dividerHorizontal = new View(this);
                    LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            15
                    );
                    layoutparams.topMargin = Utility.dpToPx(IRCTC.this, 10);
                    layoutparams.bottomMargin = Utility.dpToPx(IRCTC.this, 10);
                    dividerHorizontal.setLayoutParams(layoutparams);
                    dividerHorizontal.setBackground(Utility.getDrawableResources(IRCTC.this, R.drawable.border_top_and_bottom));
                    dividerHorizontal.setPadding(1, 1, 1, 1);
                    main_layout.addView(dividerHorizontal);

                    append_layout = new LinearLayout(this);
                    append_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    append_layout.setOrientation(LinearLayout.VERTICAL);
                    main_layout.addView(append_layout);

                    IRCTC_Image_With_Text_RecyclerViewAdapter irctc_image_with_text_recyclerViewAdapter = new IRCTC_Image_With_Text_RecyclerViewAdapter(IRCTC.this, dataAdapterVOS, R.layout.irctc_mandate_card_type_style);
                    recyclerView.setAdapter(irctc_image_with_text_recyclerViewAdapter);
                    recyclerView.getAdapter().notifyDataSetChanged();

                } else {
                    openIRCTCWebviewActivity(customerResponse, ApplicationConstant.REQ_IRCTC_MANDATE_FIRST_RESULT, customerResponse.getPaymentTypeObject());
                }
            } catch (Exception e) {
                e.printStackTrace();
                ExceptionsNotification.ExceptionHandling(this, Utility.getStackTrace(e));
            }
        }, (VolleyResponse.OnError) (error) -> {
        }));
    }


    @SuppressLint("ResourceType")
    public void setAppendLayout(Context context, DataAdapterVO adapterVO) {
       /* //create a view to inflate the layout_item (the xml with the textView created before)
        View view =getLayoutInflater().inflate(layout, append_layout,false);
        //add the view to the main layout
        append_layout.addView(view);*/
        selectMandateDataAdapter = adapterVO;
        if (Integer.parseInt(adapterVO.getNumber()) > 0) {

            RecyclerView recyclerView = new RecyclerView(this);

            recyclerView.setNestedScrollingEnabled(true);
            recyclerView.setHasFixedSize(true);

            LinearLayout.LayoutParams layoutparamsRecyclerView = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            layoutparamsRecyclerView.setMargins(5, 5, 5, 5);

            recyclerView.setLayoutParams(layoutparamsRecyclerView);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setReverseLayout(false);
            linearLayoutManager.setStackFromEnd(false);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);


            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            // recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

            if (adapterVO.getId() == 1) {
                mSwipeRefreshLayout.setRefreshing(true);
                IRCTCApi.getMandateListByPaymentOption(IRCTC.this, adapterVO.getId(), new VolleyResponse((VolleyResponse.OnSuccess) (success) -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    PreMandateRequestVO preMandateRequestVO = (PreMandateRequestVO) success;
                    Type collectionType = new TypeToken<List<PreMandateRequestVO>>() {
                    }.getType();
                    ArrayList<PreMandateRequestVO> preMandateRequestVOS = new Gson().fromJson(preMandateRequestVO.getAnonymousString(), collectionType);
                    OTM_With_Text_RecyclerViewAdapter otm_with_text_recyclerViewAdapter = new OTM_With_Text_RecyclerViewAdapter(IRCTC.this, preMandateRequestVOS, R.layout.upi_mandate_list);
                    recyclerView.setAdapter(otm_with_text_recyclerViewAdapter);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }, (VolleyResponse.OnError) (error) -> {
                    mSwipeRefreshLayout.setRefreshing(false);

                }));

            } else if (adapterVO.getId() == 2) {
                mSwipeRefreshLayout.setRefreshing(true);
                IRCTCApi.getMandateListByPaymentOption(IRCTC.this, adapterVO.getId(), new VolleyResponse((VolleyResponse.OnSuccess) (success) -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    PreMandateRequestVO preMandateRequestVO = (PreMandateRequestVO) success;
                    Type collectionType = new TypeToken<List<PreMandateRequestVO>>() {
                    }.getType();
                    ArrayList<PreMandateRequestVO> preMandateRequestVOS = new Gson().fromJson(preMandateRequestVO.getAnonymousString(), collectionType);
                    OTM_With_Text_RecyclerViewAdapter otm_with_text_recyclerViewAdapter = new OTM_With_Text_RecyclerViewAdapter(IRCTC.this, preMandateRequestVOS, R.layout.upi_mandate_list);
                    recyclerView.setAdapter(otm_with_text_recyclerViewAdapter);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }, (VolleyResponse.OnError) (error) -> {
                    mSwipeRefreshLayout.setRefreshing(false);

                }));
            } else if (adapterVO.getId() == 3) {
                mSwipeRefreshLayout.setRefreshing(true);
                IRCTCApi.getMandateListByPaymentOption(IRCTC.this, adapterVO.getId(), new VolleyResponse((VolleyResponse.OnSuccess) (success) -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    PreMandateRequestVO preMandateRequestVO = (PreMandateRequestVO) success;
                    Type collectionType = new TypeToken<List<PreMandateRequestVO>>() {
                    }.getType();
                    ArrayList<PreMandateRequestVO> preMandateRequestVOS = new Gson().fromJson(preMandateRequestVO.getAnonymousString(), collectionType);
                    OTM_With_Text_RecyclerViewAdapter otm_with_text_recyclerViewAdapter = new OTM_With_Text_RecyclerViewAdapter(IRCTC.this, preMandateRequestVOS, R.layout.upi_mandate_list);
                    recyclerView.setAdapter(otm_with_text_recyclerViewAdapter);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }, (VolleyResponse.OnError) (error) -> {
                    mSwipeRefreshLayout.setRefreshing(false);

                }));
            }

            runOnUiThread(new Runnable() {
                public void run() {
                    // Update UI elements
                    if (append_layout.getChildCount() > 0) append_layout.removeAllViews();
                    TextView tv = new TextView(IRCTC.this);
                    tv.setText("+ Add New Mandate");
                    tv.setLayoutParams(Utility.getLayoutparams(IRCTC.this, 15, 10, 15, 10));
                    tv.setTextColor(Utility.getColorWrapper(IRCTC.this, R.color.defaultTextColor));
                    tv.setGravity(Gravity.LEFT);
                    tv.setTextColor(getApplication().getResources().getColorStateList(R.drawable.text_change_color_black));
                    //track text view set underline
                    tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    append_layout.addView(tv);

                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (adapterVO.getId() == 1) {
                                openEnachActivity(IRCTC.this);
                            } else {
                                openIRCTCWebviewActivity(customerResponse, ApplicationConstant.REQ_IRCTC_MANDATE_RESULT, adapterVO.getServiceName());
                            }
                        }
                    });
                    append_layout.addView(recyclerView);
                }
            });
        } else {
            if (adapterVO.getId() == 1) {
                openEnachActivity(IRCTC.this);
            } else {
                openIRCTCWebviewActivity(customerResponse, ApplicationConstant.REQ_IRCTC_MANDATE_RESULT, adapterVO.getServiceName());
            }
        }
    }

    private void openEnachActivity(Context context) {
        IRCTCApi.createIRCTCBankMandateRequest(context, Integer.parseInt(Session.getCustomerId(context)), ApplicationConstant.IRCTC, new VolleyResponse((VolleyResponse.OnSuccess) (success) -> {
            PreMandateRequestVO preMandateRequestVO = (PreMandateRequestVO) success;
            Intent intent = new Intent(context, Enach_Mandate.class);
            intent.putExtra("forresutl", true);
            intent.putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(ApplicationConstant.IRCTC)));
            intent.putExtra("id", preMandateRequestVO.getRequestId());
            startActivityForResult(intent, ApplicationConstant.REQ_ENACH_MANDATE);
        }));
    }

    private void openIRCTCWebviewActivity(CustomerVO customerVO, int requestCode, String payMode) {
        try {
            JSONObject webViewURLJson = new JSONObject(customerVO.getAnonymousString());
            Intent intent = new Intent(IRCTC.this, IRCTC_Webview.class);
            intent.putExtra(IRCTC_Webview.EXTRAS_URL, webViewURLJson.getString("url"));
            intent.putExtra(IRCTC_Webview.EXTRAS_FAIL_URL, webViewURLJson.getString("fail"));
            intent.putExtra(IRCTC_Webview.EXTRAS_SUCCESS_URL, webViewURLJson.getString("success"));
            intent.putExtra(IRCTC_Webview.EXTRAS_ENACH_MANDATE, webViewURLJson.getString("enach"));
            intent.putExtra(IRCTC_Webview.EXTRAS_TITLE, customerVO.getDialogTitle());
            intent.putExtra(IRCTC_Webview.EXTRAS_PAY_MODE, payMode);
            startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(IRCTC.this, Utility.getStackTrace(e));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == ApplicationConstant.REQ_ENACH_MANDATE) {
                    boolean enachMandateStatus = data.getBooleanExtra("mandate_status", false);
                    String mandateId = data.getStringExtra("bankMandateId");
                    int actionId = data.getIntExtra("actionId", 0);
                    if (enachMandateStatus && actionId != 0 && mandateId != null) {
                        Utility.showSingleButtonDialog(IRCTC.this, "Alert", data.getStringExtra("msg"), false);
                        IRCTCApi.updateIRCTCBankMandateStatus(IRCTC.this, Integer.parseInt(mandateId), actionId, new VolleyResponse((VolleyResponse.OnSuccess) (success) -> {
                            getDetails();
                        }));
                    } else {
                        Utility.showSingleButtonDialog(IRCTC.this, "Alert", data.getStringExtra("msg"), false);
                    }
                } else if (requestCode == ApplicationConstant.REQ_IRCTC_MANDATE_RESULT || requestCode == ApplicationConstant.REQ_IRCTC_MANDATE_FIRST_RESULT) {
                    Utility.showSingleButtonDialog(IRCTC.this, "Alert", data.getStringExtra("msg"), false);
                    getDetails();
                }
            } else {
                if (requestCode == ApplicationConstant.REQ_IRCTC_MANDATE_FIRST_RESULT) {
                    finish();
                } else if (requestCode == ApplicationConstant.REQ_IRCTC_MANDATE_RESULT || requestCode == ApplicationConstant.REQ_ENACH_MANDATE) {
                    getDetails();
                }
            }
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(this, Utility.getStackTrace(e));
        }
    }


    public void startActivityBySelectRadioButtonId(int radioBtnId, int defaultMandate) {
        try {
            RadioButton radioButton = (RadioButton) findViewById(radioBtnId);
            JSONObject mandatePaymentObj = (JSONObject) radioButton.getTag();
            switch (radioBtnId) {
                case 1:
                    BankMandate(IRCTC.this, ApplicationConstant.IRCTC, defaultMandate);
                    break;
                case 2:
                    startSIActivity(IRCTC.this, mandatePaymentObj.getInt("defaultMandateAmount"), ApplicationConstant.IRCTC, ApplicationConstant.PG_MANDATE, defaultMandate);
                    break;
                case 3:
                    startUPIActivity(IRCTC.this, mandatePaymentObj.getInt("defaultMandateAmount"), ApplicationConstant.IRCTC, ApplicationConstant.PG_MANDATE, defaultMandate);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(this, Utility.getStackTrace(e));
        }
    }

    public void BankMandate(Context context, int serviceId, int defaultMandate) {
        startActivityForResult(new Intent(context, Enach_Mandate.class)
                .putExtra("forresutl", true)
                .putExtra("defaultMandate", defaultMandate)
                .putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(serviceId))), ApplicationConstant.REQ_ENACH_MANDATE);
    }

    public static void startSIActivity(Context context, double amount, int serviceId, String paymentType, int defaultMandate) {
        Intent intent = new Intent(context, SI_First_Data.class);
        intent.putExtra("amount", amount);
        intent.putExtra("serviceId", serviceId + "");
        intent.putExtra("paymentType", paymentType);
        intent.putExtra("defaultMandate", defaultMandate);
        ((Activity) context).startActivityForResult(intent, ApplicationConstant.REQ_SI_MANDATE);
    }

    public static void startUPIActivity(Context context, double amount, int serviceId, String paymentType, int defaultMandate) {
        Intent intent = new Intent(context, UPI_Mandate.class);
        intent.putExtra("amount", amount);
        intent.putExtra("serviceId", serviceId + "");
        intent.putExtra("paymentType", paymentType);
        intent.putExtra("defaultMandate", defaultMandate);
        ((Activity) context).startActivityForResult(intent, ApplicationConstant.REQ_UPI_FOR_MANDATE);
    }
}
