package com.uav.autodebit.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.uav.autodebit.Activity.IRCTC;
import com.uav.autodebit.R;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.DataAdapterVO;

import java.util.HashMap;
import java.util.List;

public class IRCTC_Image_With_Text_RecyclerViewAdapter extends  RecyclerView.Adapter<IRCTC_Image_With_Text_RecyclerViewAdapter.ProdectViewHolder>{
    Context mctx ;

    List<DataAdapterVO> productslist;
    int Activityname;

    HashMap<RadioButton, DataAdapterVO> buttonBooleanHashMap = new HashMap<RadioButton, DataAdapterVO>();



    public IRCTC_Image_With_Text_RecyclerViewAdapter(Context mctx, List<DataAdapterVO> productslist, int Activityname) {
        this.mctx = mctx;
        this.productslist = productslist;
        this.Activityname=Activityname;
    }

    @Override
    public IRCTC_Image_With_Text_RecyclerViewAdapter.ProdectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(mctx);
        View  view = layoutInflater.inflate(Activityname, parent, false);
        return new IRCTC_Image_With_Text_RecyclerViewAdapter.ProdectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IRCTC_Image_With_Text_RecyclerViewAdapter.ProdectViewHolder holder, int position) {

        final DataAdapterVO dataAdapterVO=productslist.get(position);
        holder.image_View.setImageDrawable(Utility.GetImage(mctx,dataAdapterVO.getImagename()));
        holder.type_Text.setText(dataAdapterVO.getText());

        holder.mail_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (RadioButton key: buttonBooleanHashMap.keySet()) {
                    key.setChecked(false);
                }
                buttonBooleanHashMap.put( holder.radio_Button,dataAdapterVO);
                holder.radio_Button.setChecked(true);
                ((IRCTC)mctx).setAppendLayout(mctx,dataAdapterVO);
            }
        });
        if(dataAdapterVO.isDefaultMandate()){
            holder.mail_Layout.performClick();
        }
    }

    @Override
    public int getItemCount() {
        return productslist.size();
    }
    public  class ProdectViewHolder extends RecyclerView.ViewHolder {

        RadioButton radio_Button;
        ImageView image_View;
        LinearLayout mail_Layout;
        TextView type_Text;
        public ProdectViewHolder(View itemView) {
            super(itemView);
            image_View=(ImageView)itemView.findViewById(R.id.image_View);
            radio_Button=itemView.findViewById(R.id.radio_Button);
            mail_Layout=itemView.findViewById(R.id.mail_Layout);
            type_Text=itemView.findViewById(R.id.type_Text);

            if (productslist.size() < 4) {
                mail_Layout.setLayoutParams(new LinearLayout.LayoutParams(
                        Utility.getDeviceWidth(mctx)/productslist.size(),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
            }

        }
    }
}
