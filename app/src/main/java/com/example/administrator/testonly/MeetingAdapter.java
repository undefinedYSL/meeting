package com.example.administrator.testonly;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Administrator on 2018/3/6 0006.
 */

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewHolder> {

    private Context mContext;

    private List<Meeting> mMeetingList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView meetingImage;
        TextView meetingname1,boardroom1,host1,date1,time1;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView =(CardView)itemView;
            meetingImage  = (ImageView)itemView.findViewById(R.id.iv_meeting);
            meetingname1 = (TextView)itemView.findViewById(R.id.tv_meeting);
            boardroom1 = (TextView)itemView.findViewById(R.id.tv_boardroom);
            host1 = (TextView)itemView.findViewById(R.id.tv_host);
            date1 = (TextView)itemView.findViewById(R.id.tv_date1);
            time1 = (TextView)itemView.findViewById(R.id.tv_time1);

        }
    }

    public MeetingAdapter(List<Meeting> meetingList){
        mMeetingList = meetingList;
    }

    @Override
    public MeetingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.meeting_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int posstion = holder.getAdapterPosition();
                Meeting meeting = mMeetingList.get(posstion);
                Intent intent = new Intent(mContext,ExistedMeetingActivity.class);
                intent.putExtra(ExistedMeetingActivity.MEETING_NAME,meeting.getMeetingname());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(MeetingAdapter.ViewHolder holder, int position) {
        Meeting meeting = mMeetingList.get(position);
        holder.meetingname1.setText(meeting.getMeetingname());
        holder.boardroom1.setText(meeting.getBoardroom());
        holder.host1.setText(meeting.getHost());
        holder.time1.setText(meeting.getTime());
        holder.date1.setText(meeting.getDate());
        Glide.with(mContext).load(meeting.getImageID()).into(holder.meetingImage);

    }

    @Override
    public int getItemCount() {
        return mMeetingList.size();
    }


}
