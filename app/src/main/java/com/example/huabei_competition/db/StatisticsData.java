package com.example.huabei_competition.db;

public class StatisticsData {
    private int Year;
    private int Month;
    private int Day;
    private int Sum;
    public void StatisticsData(int Year,int Month,int Day,int Sum){
        this.Year=Year;
        this.Month=Month;
        this.Day=Day;
        this.Sum=Sum;
    }
    public int getYear(){
        return Year;
    }
    public void setYear(int Year){
        this.Year=Year;
    }
    public int getMonth(){
        return Month;
    }
    public void setMonth(int Month){
        this.Month=Month;
    }
    public int getDay(){
        return Day;
    }
    public void setDay(int Day){
        this.Day=Day;
    }
    public int getSum(){
        return Sum;
    }
    public void setSum(int Year){
        this.Sum=Sum;
    }

}
