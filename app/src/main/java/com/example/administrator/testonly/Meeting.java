package com.example.administrator.testonly;

/**
 * Created by Administrator on 2018/3/6 0006.
 */

public class Meeting {
    private String meetingname,boardroom,host,time,date;

    private int imageID;

    public Meeting (String meetingname,String boardroom,String host,String date,String time,int imageID){
        this.meetingname = meetingname;
        this.boardroom = boardroom;
        this.host = host;
        this.date = date;
        this.time = time;
        this.imageID = imageID;
    }

    public String getMeetingname(){
        return meetingname;
    }

    public String getBoardroom(){
        return boardroom;
    }

    public String getHost(){
        return host;
    }

    public String getDate(){
        return date;
    }

    public String getTime(){
        return time;
    }

    public int getImageID(){
        return imageID;
    }
}
