package ir.kitgroup.salein.DataBase;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

@Keep
public class Users extends SugarRecord {


    @Unique
    @SerializedName("I")
    @Expose
    public String I;
    @SerializedName("N")
    @Expose
    public String N;
    @SerializedName("C")
    @Expose
    private String C;
    public String Token;
    public String getC() {
        String c="";
        if (C!=null)
            c=C;
        return c;
    }

    public void setC(String c) {
        C = c;
    }

    @SerializedName("M")
    @Expose
    public String M;

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


    @SerializedName("P1")
    @Expose
    private String P1;

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


    public String getM() {
        return M;
    }

    public void setM(String m) {
        M = m;
    }

    public String getM2() {
        return M2;
    }

    public void setM2(String m2) {
        M2 = m2;
    }

    public String getP1() {
        return P1;
    }

    public void setP1(String p1) {
        P1 = p1;
    }

    @SerializedName("ADR")
    @Expose
    public String ADR;

    @SerializedName("ADR2")
    @Expose
    public String ADR2;


    @SerializedName("S")
    @Expose
    public String S;

    public String getS() {
        return S;
    }

    public void setS(String s) {
        S = s;
    }

    public String getACCCLBUID() {
        return I;
    }

    public void setACCCLBUID(String aCCCLBUID) {
        this.I = aCCCLBUID;
    }


    public String getACCCLBNAME() {
        return N;
    }

    public String getACCCLBCODE() {
        return C;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private String version;

    public String getSaleinUser() {
        return SaleinUser;
    }

    public void setSaleinUser(String saleinUser) {
        SaleinUser = saleinUser;
    }

    private String SaleinUser;

}
