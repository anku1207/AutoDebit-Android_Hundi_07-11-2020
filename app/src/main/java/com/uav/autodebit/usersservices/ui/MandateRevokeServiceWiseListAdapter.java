package com.uav.autodebit.usersservices.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.print.PrintAttributes;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uav.autodebit.BO.CustomerBO;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerServiceOperatorVO;
import com.uav.autodebit.vo.CustomerVO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MandateRevokeServiceWiseListAdapter extends RecyclerView.Adapter<MandateRevokeServiceWiseListAdapter.UsersMandateViewHolder> {

    Context context;
    ArrayList<CustomerServiceOperatorVO> services;
    public MandateRevokeServiceWiseListAdapter(Context context, ArrayList<CustomerServiceOperatorVO> services){
        this.context = context;
        this.services = services;
    }
    @NonNull
    @Override
    public MandateRevokeServiceWiseListAdapter.UsersMandateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_listitem, parent, false);
        return new UsersMandateViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MandateRevokeServiceWiseListAdapter.UsersMandateViewHolder holder, int position) {

        try{
               CustomerServiceOperatorVO pro=services.get(position);
               JSONArray jsonArray =new JSONArray(pro.getAnonymousString());

               if(holder.serviceInfo.getChildCount()>0) holder.serviceInfo.removeAllViews();
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject =jsonArray.getJSONObject(i);
                    if(jsonObject.has("value") &&  jsonObject.has("value"))
                    createServiceDetails(holder.serviceInfo,jsonObject.getString("key"),jsonObject.getString("value"));
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(pro.getcSOId()!=null || pro.getServiceType().getServiceTypeId()!=null){
                            Intent intent =new Intent(context,MandateDetailActivity.class);
                            intent.putExtra("csoid",pro.getcSOId());
                            intent.putExtra("serviceTypeId",pro.getServiceType().getServiceTypeId());
                            ((Activity)context).startActivityForResult(intent, ApplicationConstant.REQ_MANDATE_DETAIL_ACTIVITY_RESULT);
                        }

                    }
                });

        }catch (Exception e){
            Toast.makeText(context,"Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createServiceDetails(LinearLayout serviceInfo, String key, String valuepair) {

        Typeface typeface = ResourcesCompat.getFont(context, R.font.poppinssemibold);

        LinearLayout et = new LinearLayout(new ContextThemeWrapper(context,R.style.confirmation_dialog_filed));

        TextView text = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_filed));
        text.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
        text.setTextColor(context.getResources().getColor(R.color.defaultTextColor));
        text.setText(key);
        text.setMaxLines(1);
        text.setTextSize(12);
        text.setEllipsize(TextUtils.TruncateAt.END);
        text.setTypeface(typeface);


        ImageView iv  = new ImageView(new ContextThemeWrapper(context, R.style.confirmation_dialog_value_image));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(48, 48, (float) 0.2);
        lp.setMargins(2,4,2,4);
        iv.setLayoutParams(lp);

        TextView text1 = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_value_image));
        text1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,0));
        text1.setTextColor(context.getResources().getColor(R.color.defaultTextColor));
        text1.setText(" : ");
        text1.setTextSize(12);
        text1.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        TextView value = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_value_image));
        value.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));
        //value.setTextColor(context.getResources().getColor(R.color.defaultTextColor));
        value.setTextSize(12);
        value.setText(valuepair);

       // et.addView(iv);

        et.addView(text);
        et.addView(text1);
        et.addView(value);

        if(key.equals("Service Name") || key.equals("Operater Name" )){
            et.removeView(text);
            et.removeView(text1);
            value.setTypeface(text.getTypeface(), Typeface.BOLD);
            value.setTextColor(context.getResources().getColor(R.color.defaultTextColor));

        }

        serviceInfo.addView(et);

    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    static class UsersMandateViewHolder extends RecyclerView.ViewHolder{
        TextView serviceName;
        LinearLayout serviceInfo;
        Button modify;
        ImageView revoke,editMandate;



        public UsersMandateViewHolder(@NonNull View itemView) {
            super(itemView);
           //serviceName = itemView.findViewById(R.id.service_name);
           serviceInfo =itemView.findViewById(R.id.llDynamic);
        }

    }
}
