package com.kolotseyd.f_studioapp.Data;

public class FullMoneyGainReportData {

    private String date;
    private double totalMoney;
    private double cashMoney;
    private double cardMoney;
    private double editMoney;

    public FullMoneyGainReportData(String date, double totalMoney, double cashMoney, double cardMoney, double editMoney) {
        this.date = date;
        this.totalMoney = totalMoney;
        this.cashMoney = cashMoney;
        this.cardMoney = cardMoney;
        this.editMoney = editMoney;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public double getCashMoney() {
        return cashMoney;
    }

    public void setCashMoney(double cashMoney) {
        this.cashMoney = cashMoney;
    }

    public double getCardMoney() {
        return cardMoney;
    }

    public void setCardMoney(double cardMoney) {
        this.cardMoney = cardMoney;
    }

    public double getEditMoney() {
        return editMoney;
    }

    public void setEditMoney(double editMoney) {
        this.editMoney = editMoney;
    }
}
