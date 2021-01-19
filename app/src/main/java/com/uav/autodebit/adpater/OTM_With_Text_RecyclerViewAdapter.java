package com.uav.autodebit.adpater;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.uav.autodebit.IRCTC.IRCTCApi;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.PreMandateRequestVO;

import java.util.HashMap;
import java.util.List;

public class OTM_With_Text_RecyclerViewAdapter extends RecyclerView.Adapter<OTM_With_Text_RecyclerViewAdapter.ProdectViewHolder> {
    Context mctx;

    List<PreMandateRequestVO> productslist;
    int Activityname;

    HashMap<RadioButton, PreMandateRequestVO> buttonBooleanHashMap = new HashMap<RadioButton, PreMandateRequestVO>();

    public OTM_With_Text_RecyclerViewAdapter(Context mctx, List<PreMandateRequestVO> productslist, int Activityname) {
        this.mctx = mctx;
        this.productslist = productslist;
        this.Activityname = Activityname;
    }

    @Override
    public OTM_With_Text_RecyclerViewAdapter.ProdectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mctx);
        View view = layoutInflater.inflate(Activityname, parent, false);
       /* ProdectViewHolder holder=new ProdectViewHolder(view);
        return holder;*/
        return new OTM_With_Text_RecyclerViewAdapter.ProdectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OTM_With_Text_RecyclerViewAdapter.ProdectViewHolder holder, int position) {

        final PreMandateRequestVO preMandateRequestVO = productslist.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.text_View.setText(Html.fromHtml(preMandateRequestVO.getAnonymousString(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.text_View.setText(Html.fromHtml(preMandateRequestVO.getAnonymousString()));
        }

        if (preMandateRequestVO.getImage() != null && Utility.GetImage(mctx, preMandateRequestVO.getImage()) != null) {
            holder.image_mandate.setVisibility(View.VISIBLE);
            holder.image_mandate.setImageDrawable(Utility.GetImage(mctx, preMandateRequestVO.getImage()));
        } else {
            holder.image_mandate.setVisibility(View.GONE);
        }

        holder.mail_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.radio_Button.isChecked()){
                    return;
                }

                String[] buttons = {"Yes", "No"};
                Utility.confirmationDialogTextType(new DialogInterface() {
                    @Override
                    public void confirm(Dialog dialog) {
                        Utility.dismissDialog(mctx, dialog);
                        IRCTCApi.updateIRCTCDefaultMandate(mctx, preMandateRequestVO.getRequestId(), new VolleyResponse((VolleyResponse.OnSuccess) (success) -> {
                            for (RadioButton key : buttonBooleanHashMap.keySet()) {
                                key.setChecked(false);
                            }
                            buttonBooleanHashMap.clear();
                            for(PreMandateRequestVO preMandateRequestVO1 : productslist){
                                preMandateRequestVO1.setEventIs(false);
                            }
                            preMandateRequestVO.setEventIs(true);
                            productslist.set(position, preMandateRequestVO);
                            buttonBooleanHashMap.put(holder.radio_Button, preMandateRequestVO);
                            holder.radio_Button.setChecked(true);
                        }));
                    }

                    @Override
                    public void modify(Dialog dialog) {
                        Utility.dismissDialog(mctx, dialog);
                    }
                }, mctx, null, "Are you sure you want to change your default mandate?", null, buttons);

            }
        });
        if (preMandateRequestVO.isEventIs()) {
           /* for (RadioButton key : buttonBooleanHashMap.keySet()) {
                key.setChecked(false);
            }*/
            buttonBooleanHashMap.put(holder.radio_Button, preMandateRequestVO);
            holder.radio_Button.setChecked(true);
        }else{
            holder.radio_Button.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return productslist.size();
    }

    public class ProdectViewHolder extends RecyclerView.ViewHolder {

        RadioButton radio_Button;
        TextView text_View;
        LinearLayout mail_Layout;
        ImageView image_mandate;

        public ProdectViewHolder(View itemView) {
            super(itemView);
            text_View = itemView.findViewById(R.id.text_View);
            radio_Button = itemView.findViewById(R.id.radio_Button);
            mail_Layout = itemView.findViewById(R.id.mail_Layout);
            image_mandate = itemView.findViewById(R.id.image_mandate);
        }
    }
}
