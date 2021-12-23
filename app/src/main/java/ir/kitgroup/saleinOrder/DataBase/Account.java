package ir.kitgroup.saleinOrder.DataBase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class Account extends SugarRecord {


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
    @SerializedName("M")
    @Expose
    public String M;
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
    @SerializedName("TN")
    @Expose
    private String TN;
    @SerializedName("PN")
    @Expose
    private String PN;
    @SerializedName("AN")
    @Expose
    private String AN;
    @SerializedName("CN")
    @Expose
    private String CN;
    @SerializedName("CCD")
    @Expose
    private String CCD;




    @SerializedName("LAT")
    @Expose
    public Double LAT;


    @SerializedName("LNG")
    @Expose
    public Double LNG;



    @SerializedName("LAT1")
    @Expose
    public Double LAT1;


    @SerializedName("LNG1")
    @Expose
    public Double LNG1;


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


}
