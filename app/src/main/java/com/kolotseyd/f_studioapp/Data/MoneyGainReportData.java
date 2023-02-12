package com.kolotseyd.f_studioapp.Data;

public class MoneyGainReportData {
    private String date;
    private double moneyGain;

    public MoneyGainReportData(String date, double moneyGain) {
        this.date = date;
        this.moneyGain = moneyGain;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getMoneyGain() {
        return moneyGain;
    }

    public void setMoneyGain(double moneyGain) {
        this.moneyGain = moneyGain;
    }
}
