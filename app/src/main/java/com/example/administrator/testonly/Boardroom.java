package com.example.administrator.testonly;

/**
 * Created by Administrator on 2018/3/6 0006.
 */

public class Boardroom {

    private String boardroomname;
    private String telephone;
    private String site;

    public Boardroom(String boardroomname,String telephone,String site){
        this.boardroomname = boardroomname;
        this.telephone = telephone;
        this.site = site;
    }

    public String getBoardroomname(){
        return boardroomname;
    }

    public String getTelephone(){
        return telephone;
    }

    public String getSite(){
        return site;
    }
}
