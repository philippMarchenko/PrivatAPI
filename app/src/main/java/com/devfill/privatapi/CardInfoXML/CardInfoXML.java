package com.devfill.privatapi.CardInfoXML;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "response")
public class CardInfoXML {

    @Element(name="merchant", required = false)
    Merchant merchant;


    @Element(name="data", required = false)
    Data data;


    @Attribute(name="version", required = false)
    String version;



    public Merchant getMerchant() { return this.merchant; }
    public void setMerchant(Merchant _value) { this.merchant = _value; }


    public Data getData() { return this.data; }
    public void setData(Data _value) { this.data = _value; }


    public String getVersion() { return this.version; }
    public void setVersion(String _value) { this.version = _value; }



    public static class Merchant {

        @Element(name="id", required = false)
        String id;


        @Element(name="signature", required = false)
        String signature;



        public String getId() { return this.id; }
        public void setId(String _value) { this.id = _value; }


        public String getSignature() { return this.signature; }
        public void setSignature(String _value) { this.signature = _value; }


    }

    public static class Data {

        @Element(name="oper", required = false)
        String oper;


        @Element(name="info", required = false)
        Info info;



        public String getOper() { return this.oper; }
        public void setOper(String _value) { this.oper = _value; }


        public Info getInfo() { return this.info; }
        public void setInfo(Info _value) { this.info = _value; }


    }

    public static class Info {

        @Element(name="cardbalance", required = false)
        Cardbalance cardbalance;



        public Cardbalance getCardbalance() { return this.cardbalance; }
        public void setCardbalance(Cardbalance _value) { this.cardbalance = _value; }


    }

    public static class Cardbalance {

        @Element(name="card", required = false)
        Card card;


        @Element(name="av_balance", required = false)
        String av_balance;


        @Element(name="bal_date", required = false)
        String bal_date;


        @Element(name="bal_dyn", required = false)
        String bal_dyn;


        @Element(name="balance", required = false)
        String balance;


        @Element(name="fin_limit", required = false)
        String fin_limit;


        @Element(name="trade_limit", required = false)
        String trade_limit;



        public Card getCard() { return this.card; }
        public void setCard(Card _value) { this.card = _value; }


        public String getAv_balance() { return this.av_balance; }
        public void setAv_balance(String _value) { this.av_balance = _value; }


        public String getBal_date() { return this.bal_date; }
        public void setBal_date(String _value) { this.bal_date = _value; }


        public String getBal_dyn() { return this.bal_dyn; }
        public void setBal_dyn(String _value) { this.bal_dyn = _value; }


        public String getBalance() { return this.balance; }
        public void setBalance(String _value) { this.balance = _value; }


        public String getFin_limit() { return this.fin_limit; }
        public void setFin_limit(String _value) { this.fin_limit = _value; }


        public String getTrade_limit() { return this.trade_limit; }
        public void setTrade_limit(String _value) { this.trade_limit = _value; }


    }

    public static class Card {

        @Element(name="account", required = false)
        String account;


        @Element(name="card_number", required = false)
        String card_number;


        @Element(name="acc_name", required = false)
        String acc_name;


        @Element(name="acc_type", required = false)
        String acc_type;


        @Element(name="currency", required = false)
        String currency;


        @Element(name="card_type", required = false)
        String card_type;


        @Element(name="main_card_number", required = false)
        String main_card_number;


        @Element(name="card_stat", required = false)
        String card_stat;


        @Element(name="src", required = false)
        String src;



        public String getAccount() { return this.account; }
        public void setAccount(String _value) { this.account = _value; }


        public String getCard_number() { return this.card_number; }
        public void setCard_number(String _value) { this.card_number = _value; }


        public String getAcc_name() { return this.acc_name; }
        public void setAcc_name(String _value) { this.acc_name = _value; }


        public String getAcc_type() { return this.acc_type; }
        public void setAcc_type(String _value) { this.acc_type = _value; }


        public String getCurrency() { return this.currency; }
        public void setCurrency(String _value) { this.currency = _value; }


        public String getCard_type() { return this.card_type; }
        public void setCard_type(String _value) { this.card_type = _value; }


        public String getMain_card_number() { return this.main_card_number; }
        public void setMain_card_number(String _value) { this.main_card_number = _value; }


        public String getCard_stat() { return this.card_stat; }
        public void setCard_stat(String _value) { this.card_stat = _value; }


        public String getSrc() { return this.src; }
        public void setSrc(String _value) { this.src = _value; }


    }
}