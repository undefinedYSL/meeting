package com.example.administrator.testonly;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Administrator on 2018/3/6 0006.
 */

public class InformAdapter extends RecyclerView.Adapter<InformAdapter.ViewHolder>{

    private Context mContext;

    private List<Inform> mInformList;


    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView informName1,content1;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView;
            informName1 = (TextView)itemView.findViewById(R.id.tv_inform);
            content1 = (TextView)itemView.findViewById(R.id.tv_content);
        }
    }


    public InformAdapter(List<Inform> informList){
        mInformList = informList;
    }

    @Override
    public InformAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.inform_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int posstion = holder.getAdapterPosition();
                Inform inform = mInformList.get(posstion);
                Intent intent = new Intent(mContext,ExistedInformActivity.class);
                intent.putExtra(ExistedInformActivity.INFORM_NAME,inform.getInformname());
                mContext.startActivity(intent);
            }
        });
//        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//               int position = holder.getAdapterPosition();
//               Inform inform = mInformList.get(position);
//               longControl.btnLongpress(position);
//                return true;
//            }
//        });
        return holder;
    }


    @Override
    public void onBindViewHolder(InformAdapter.ViewHolder holder, int position) {
        Inform inform = mInformList.get(position);
        holder.informName1.setText(inform.getInformname());
        holder.content1.setText(inform.getContent());

    }

    @Override
    public int getItemCount() {
        return mInformList.size();
    }
}
