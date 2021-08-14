package ir.kitgroup.salein1.DataBase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class Account extends SugarRecord {

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
