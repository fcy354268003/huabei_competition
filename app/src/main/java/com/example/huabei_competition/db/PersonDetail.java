package com.example.huabei_competition.db;

import org.litepal.crud.LitePalSupport;

public class  PersonDetail extends LitePalSupport {


    //public String img1;
    //public String img2;
    public int Id;
    public String Name;
    public String Zihao;
    public String Dynasty;
    public String Summary;


    public PersonDetail(int Id, String Name, String Zihao, String Dynasty, String Summary) {
    }



    public int getId(){
        return Id;
    }
    public void setId(int Id){
        this.Id=Id;
    }
    public String getName(){
        return Name;
    }
    public void setName(String Name){
        this.Name=Name;
    }
    public String getDynasty(){
        return Dynasty;
    }
    public void setDynasty(String Dynasty){
        this.Dynasty=Dynasty;
    }
    public String getZihao(){
        return Zihao;
    }
    public void setZihao(String Zihao){
        this.Zihao=Zihao;
    }
    public String getSummary(){
        return Summary;
    }





}

