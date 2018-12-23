package com.example.administrator.testonly;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/6 0006.
 */

public class Inform {



    private String informname;
    private String content;


    public Inform(String informname, String content){
//        super();
        this.informname = informname;
        this.content = content;
    }


    public String getInformname(){
        return informname;
    }

    public String getContent(){
        return content;
    }


//    public void setSomething(String informname,String content){
//        this.informname = informname;
//        this.content = content;
//    }


}
