package ir.kitgroup.salein.DataBase;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.util.List;

import ir.kitgroup.salein.models.AppDetail;


@Keep
public class Account extends SugarRecord {
    @SerializedName("I")
    @Expose
    private String i;
    @SerializedName("N")
    @Expose
    private String n;
    @SerializedName("LN")
    @Expose
    private String ln;
    @SerializedName("M")
    @Expose
    private String m;
    @SerializedName("P1")
    @Expose
    private String p1;
    @SerializedName("ADR")
    @Expose
    private String adr;
    @SerializedName("ADR2")
    @Expose
    private Object adr2;
    @SerializedName("PM")
    @Expose
    private String pm;

    public List<AppDetail> getApps() {
        return apps;
    }

    public void setApps(List<AppDetail> apps) {
        this.apps = apps;
    }

    @SerializedName("APP")
    @Expose
    private List<AppDetail> apps = null;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    @SerializedName("IMEI")
    @Expose
    private String imei;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private String version;
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @SerializedName("APPID")
    @Expose
    private String appId;

    public String getPm() {
        return pm;
    }

    public void setPm(String pm) {
        this.pm = pm;
    }



    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getLn() {
        return ln;
    }

    public void setLn(String ln) {
        this.ln = ln;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getP1() {
        return p1;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public String getAdr() {
        return adr;
    }

    public void setAdr(String adr) {
        this.adr = adr;
    }

    public Object getAdr2() {
        return adr2;
    }

    public void setAdr2(Object adr2) {
        this.adr2 = adr2;
    }

}
