package com.zcorp.app.foodvision.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zcorp.app.foodvision.Data.Cloud_Data;
import com.zcorp.app.foodvision.R;

import java.util.ArrayList;

/**
 * Created by mankiratsingh on 13/09/17.
 */

public class Description_RV_Adatper extends RecyclerView.Adapter<Description_RV_Adatper.Descriptions> {

    ArrayList<Cloud_Data> list;
    Context context;

    public Description_RV_Adatper(ArrayList<Cloud_Data> list, Context context){
        this.list=list;
        this.context=context;
    }

    @Override
    public Descriptions onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_description_rv,parent,false);
        Descriptions desc =new Descriptions(v);
        return desc;
    }

    @Override
    public void onBindViewHolder(Descriptions holder, int position) {
        holder.description.setText(list.get(position).getDescription());

        if(list.get(position).getScore()>.85){
            holder.score.setText("Very Likely");
            holder.score.setBackgroundResource(R.drawable.button_very_likely);

        }else if(list.get(position).getScore()>.70){
            holder.score.setText("Likely");
            holder.score.setBackgroundResource(R.drawable.button_likely);

        }else{
            holder.score.setText("Maybe");
            holder.score.setBackgroundResource(R.drawable.button_maybe);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Descriptions extends RecyclerView.ViewHolder{

        TextView description;
        Button score;

        public Descriptions(View itemView) {
            super(itemView);

            description= itemView.findViewById(R.id.output_desc);
            score= itemView.findViewById(R.id.score);


        }
    }
}
