package ir.kitgroup.saleindemo.models;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import androidx.annotation.Keep;
@Keep
public class ProductLevel2 {

    @SerializedName("I")
    @Expose
    private String i;
    @SerializedName("PI")
    @Expose
    private String pi;
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
    @SerializedName("CSMT")
    @Expose
    private Integer csmt;
    @SerializedName("CSMB")
    @Expose
    private Integer csmb;


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

    public Integer getCsmt() {
        return csmt;
    }

    public void setCsmt(Integer csmt) {
        this.csmt = csmt;
    }

    public Integer getCsmb() {
        return csmb;
    }

    public void setCsmb(Integer csmb) {
        this.csmb = csmb;
    }

}
