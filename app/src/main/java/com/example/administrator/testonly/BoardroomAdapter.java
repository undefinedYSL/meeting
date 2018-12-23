package com.example.administrator.testonly;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2018/3/6 0006.
 */

public class BoardroomAdapter extends RecyclerView.Adapter<BoardroomAdapter.ViewHolder>{

    private Context mContext;
    private List<Boardroom> mBoardroomList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView boardroomname1,telephone1,site1;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView;
            boardroomname1 = (TextView)itemView.findViewById(R.id.tv_boardroomname);
            telephone1 = (TextView)itemView.findViewById(R.id.tv_type);
            site1 = (TextView)itemView.findViewById(R.id.tv_site);
//            test = (Button)itemView.findViewById(R.id.btn_test);
        }
    }
    public BoardroomAdapter(List<Boardroom> boardroomList){
        mBoardroomList = boardroomList;
    }

    @Override
    public BoardroomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext ==null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.boardroom_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int posstion = holder.getAdapterPosition();
                Boardroom boardroom = mBoardroomList.get(posstion);
                Intent intent = new Intent(mContext,ExistedBoardroomActivity.class);
                intent.putExtra(ExistedBoardroomActivity.BOARDROOM_NAME,boardroom.getBoardroomname());
                intent.putExtra(ExistedBoardroomActivity.BOARDROOM_POSITION,posstion);
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(BoardroomAdapter.ViewHolder holder, final int position) {
        Boardroom boardroom = mBoardroomList.get(position);
        holder.boardroomname1.setText(boardroom.getBoardroomname());
        holder.telephone1.setText(boardroom.getTelephone());
        holder.site1.setText(boardroom.getSite());
    }

    @Override
    public int getItemCount() {
        return mBoardroomList.size();
    }

}
