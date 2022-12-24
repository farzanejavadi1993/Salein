package ir.kitgroup.salein.DataBase;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.util.List;

import ir.kitgroup.salein.models.AppDetail;


@Keep
public class Account extends SugarRecord {
    @Unique
    @SerializedName("I")
    @Expose
    private String i;
    @SerializedName("C")
    @Expose
    private String C;

    @SerializedName("STAPP")
    @Expose
    public Boolean STAPP;
    @SerializedName("STS")
    @Expose
    public boolean STS;
    @SerializedName("M2")
    @Expose
    private String M2;
    @SerializedName("CRDT")
    @Expose
    public Double CRDT;
    @SerializedName("PSW")
    @Expose
    public String PSW;

    @SerializedName("PC")//INTRODUCTION CODE
    @Expose
    public String PC;
    @SerializedName("LAT")
    @Expose
    public String LAT;
    @SerializedName("LNG")
    @Expose
    public String LNG;
    @SerializedName("LAT1")
    @Expose
    public String LAT1;
    @SerializedName("LNG1")
    @Expose
    public String LNG1;
    @SerializedName("S")
    @Expose
    public String S;
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
    private String adr2;
    @SerializedName("PM")
    @Expose
    private String pm;
    @SerializedName("APP")
    @Expose
    private List<AppDetail> apps = null;
    @SerializedName("IMEI")
    @Expose
    private String imei;

    public String getC() {
        return C;
    }

    public void setC(String c) {
        C = c;
    }

    public Boolean getSTAPP() {
        return STAPP;
    }

    public void setSTAPP(Boolean STAPP) {
        this.STAPP = STAPP;
    }

    public boolean isSTS() {
        return STS;
    }

    public void setSTS(boolean STS) {
        this.STS = STS;
    }

    public String getM2() {
        return M2;
    }

    public void setM2(String m2) {
        M2 = m2;
    }

    public Double getCRDT() {
        return CRDT;
    }

    public void setCRDT(Double CRDT) {
        this.CRDT = CRDT;
    }

    public String getPSW() {
        return PSW;
    }

    public void setPSW(String PSW) {
        this.PSW = PSW;
    }

    public String getPC() {
        return PC;
    }

    public void setPC(String PC) {
        this.PC = PC;
    }

    public Double getLAT() {
        double lat = 0.0;
        if (!LAT.equals("") && !LAT.equals("-") && LAT != null)
            lat = Double.parseDouble(LAT);

        return lat;
    }

    public void setLAT(String LAT) {
        this.LAT = LAT;
    }

    public Double getLNG() {
        double lng = 0.0;
        if (!LNG.equals("") && !LNG.equals("-") && LNG != null)
            lng = Double.parseDouble(LNG);

        return lng;
    }

    public void setLNG(String LNG) {
        this.LNG = LNG;
    }

    public Double getLAT1() {
        double lat = 0.0;
        if (!LAT1.equals("") && !LAT1.equals("-") && LAT1 != null)
            lat = Double.parseDouble(LAT1);

        return lat;
    }

    public void setLAT1(String LAT1) {
        this.LAT1 = LAT1;
    }

    public Double getLNG1() {
        double lng = 0.0;
        if (!LNG1.equals("") && !LNG1.equals("-") && LNG1 != null)
            lng = Double.parseDouble(LNG1);

        return lng;
    }

    public void setLNG1(String LNG1) {
        this.LNG1 = LNG1;
    }

    public String getS() {
        return S;
    }

    public void setS(String s) {
        S = s;
    }

    @SerializedName("APPID")
    @Expose
    private String appId;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private String version;

    public List<AppDetail> getApps() {
        return apps;
    }

    public void setApps(List<AppDetail> apps) {
        this.apps = apps;
    }


    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }


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
        String address="";
        if (adr!=null && !adr.equals("-") )
            address=adr;
        return address;
    }

    public void setAdr(String adr) {
        this.adr = adr;
    }

    public String getAdr2() {
        String address="";
        if (adr2!=null && !adr2.equals("-") )
            address=adr2;
        return address;
    }

    public void setAdr2(String adr2) {
        this.adr2 = adr2;
    }


}
