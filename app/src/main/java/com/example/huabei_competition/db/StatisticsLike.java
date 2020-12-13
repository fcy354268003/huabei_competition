package com.example.huabei_competition.db;
//好感度类
public class StatisticsLike {
    private int Cishu;
    private int Total;
    private int Week;
    public StatisticsLike(int Cishu,int Total, int Week){
        this.Cishu = Cishu;
        this.Total = Total;
        this.Week = Week;

    }
    public int getLikeId(){
        return Cishu;
    }
    public void setLikeId(int LikeId){
        this.Cishu=LikeId;
    }
    public int getTotal(){
        return Total;
    }
    public void setTotal(int Total){
        this.Total=Total;
    }
    public int getWeek(){
        return Week;
    }
    public void setWeek(int Week){
        this.Week=Week;
    }
}
