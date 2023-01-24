package ir.kitgroup.saleinAlin.DataBase;


import android.content.SharedPreferences;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import ir.kitgroup.saleinAlin.classes.Util;

@Keep
public class Product extends SugarRecord {

    @Unique

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

    @SerializedName("UM1")
    @Expose
    public String UM1;
    @SerializedName("COEF2")
    @Expose
    private Double coef2;

    public Integer getRe() {

        return Re;
    }


    public void setRe(Integer re) {
        Re = re;
    }

    public Integer Re;




    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public  String operate;

    private Double Amount;

    public Double getAmount() {
        double amount = 0.0;
        if (Amount != null)
            amount = Amount;
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

    public String getPid3() {
        return pid3;
    }



    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
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


    public int getiIndex() {
        return iIndex;
    }

    public void setiIndex(int iIndex) {
        this.iIndex = iIndex;
    }

    private int iIndex;

}
