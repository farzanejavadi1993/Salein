package ir.kitgroup.saleintop.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductLevel1 {

    @SerializedName("I")
    @Expose
    private String i;
    @SerializedName("PI")
    @Expose
    private String pi;
    @SerializedName("BID")
    @Expose
    private String bid;
    @SerializedName("FID")
    @Expose
    private String fid;
    @SerializedName("N")
    @Expose
    private String n;
    @SerializedName("C")
    @Expose
    private String c;
    @SerializedName("STS")
    @Expose
    private Boolean sts;
    @SerializedName("TKN")
    @Expose
    private Integer tkn;



    public  boolean Click;

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public String getPi() {
        return pi;
    }

    public void setPi(String pi) {
        this.pi = pi;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public Boolean getSts() {
        return sts;
    }

    public void setSts(Boolean sts) {
        this.sts = sts;
    }

    public Integer getTkn() {
        return tkn;
    }

    public void setTkn(Integer tkn) {
        this.tkn = tkn;
    }



}
