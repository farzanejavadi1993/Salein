package ir.kitgroup.salein.models;


import android.content.SharedPreferences;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ir.kitgroup.salein.classes.Util;

public class Product {

    @SerializedName("I")
    @Expose
    private String i;
    @SerializedName("PID1")
    @Expose
    private String pid1;
    @SerializedName("PID2")
    @Expose
    private String pid2;
    @SerializedName("PID3")
    @Expose
    private String pid3;
    @SerializedName("N")
    @Expose
    private String n;
    @SerializedName("NIP")
    @Expose
    private String nip;
    @SerializedName("PU1")
    @Expose
    private Double pu1;
    @SerializedName("PU2")
    @Expose
    private Double pu2;
    @SerializedName("PU3")
    @Expose
    private Double pu3;
    @SerializedName("PU4")
    @Expose
    private Double pu4;
    @SerializedName("PU5")
    @Expose
    private Double pu5;
    @SerializedName("DES")
    @Expose
    private String des;
    @SerializedName("TAX")
    @Expose
    private Boolean tax;
    @SerializedName("STS")
    @Expose
    private Boolean sts;
    @SerializedName("PERC_DIS")
    @Expose
    private Double percDis;
    @SerializedName("COEF")
    @Expose
    private Double coef;
    @SerializedName("KEY")
    @Expose
    private Integer key;


    private Double Amount;

    public Double getAmount() {
        double amount=0.0;
        if (Amount!=null)
            amount=Amount;
        return amount;
    }


    public void setAmount(Double amount) {
        Amount = amount;
    }

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public String getPid1() {
        return pid1;
    }

    public void setPid1(String pid1) {
        this.pid1 = pid1;
    }

    public String getPid2() {
        return pid2;
    }

    public void setPid2(String pid2) {
        this.pid2 = pid2;
    }

    public String getPid3() {
        return pid3;
    }

    public void setPid3(String pid3) {
        this.pid3 = pid3;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }


    public Double getPrice(SharedPreferences sharedPreferences) {
        Double showPrice = 0.0;
        try {

            String priceList = Util.getPrice(sharedPreferences);

            switch (priceList) {
                case "2":
                    showPrice = pu2;
                    break;
                case "3":
                    showPrice = pu3;
                    break;
                case "4":
                    showPrice = pu4;
                    break;
                case "5":
                    showPrice = pu5;
                    break;
                default:
                    showPrice = pu1;
                    break;
            }



        } catch (Exception ignored) {
        }
        return showPrice;
    }




    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public Boolean getTax() {
        return tax;
    }

    public void setTax(Boolean tax) {
        this.tax = tax;
    }

    public Boolean getSts() {
        return sts;
    }

    public void setSts(Boolean sts) {
        this.sts = sts;
    }

    public Double getPercDis() {
        return percDis;
    }

    public void setPercDis(Double percDis) {
        this.percDis = percDis;
    }

    public Double getCoef() {
        return coef;
    }

    public void setCoef(Double coef) {
        this.coef = coef;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

}
