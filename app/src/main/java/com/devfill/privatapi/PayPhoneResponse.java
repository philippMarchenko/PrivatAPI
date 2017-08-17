package com.devfill.privatapi;



import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.math.BigDecimal;
import java.util.List;

@Root(name = "response")
public class PayPhoneResponse {

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


        @Element(name="payment", required = false)
        Payment payment;



        public String getOper() { return this.oper; }
        public void setOper(String _value) { this.oper = _value; }


        public Payment getPayment() { return this.payment; }
        public void setPayment(Payment _value) { this.payment = _value; }


    }

    public static class Payment {

        @Attribute(name="id", required = false)
        String id;


        @Attribute(name="state", required = false)
        String state;


        @Attribute(name="auto_id", required = false)
        String auto_id;


        @Attribute(name="message", required = false)
        String message;


        @Attribute(name="ref", required = false)
        String ref;


        @Attribute(name="amt", required = false)
        String amt;


        @Attribute(name="ccy", required = false)
        String ccy;


        @Attribute(name="comis", required = false)
        String comis;


        @Attribute(name="code", required = false)
        String code;



        public String getId() { return this.id; }
        public void setId(String _value) { this.id = _value; }


        public String getState() { return this.state; }
        public void setState(String _value) { this.state = _value; }


        public String getAuto_id() { return this.auto_id; }
        public void setAuto_id(String _value) { this.auto_id = _value; }


        public String getMessage() { return this.message; }
        public void setMessage(String _value) { this.message = _value; }


        public String getRef() { return this.ref; }
        public void setRef(String _value) { this.ref = _value; }


        public String getAmt() { return this.amt; }
        public void setAmt(String _value) { this.amt = _value; }


        public String getCcy() { return this.ccy; }
        public void setCcy(String _value) { this.ccy = _value; }


        public String getComis() { return this.comis; }
        public void setComis(String _value) { this.comis = _value; }


        public String getCode() { return this.code; }
        public void setCode(String _value) { this.code = _value; }


    }
}