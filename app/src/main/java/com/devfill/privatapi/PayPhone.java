package com.devfill.privatapi;


public class PayPhone{

    private String name;    //кому отправляем
    private String amt;     //сумма
    private String number;     //номер



    public  PayPhone(String name, String number, String amt){
        this.name = name;
        this.amt = amt;
        this.number = number;

    }

    public  PayPhone(){
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


}