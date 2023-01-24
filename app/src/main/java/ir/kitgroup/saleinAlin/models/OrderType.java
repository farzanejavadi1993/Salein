package ir.kitgroup.saleinAlin.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import androidx.annotation.Keep;
@Keep
public class OrderType {

    @SerializedName("I")
    @Expose
    private String i;
    @SerializedName("N")
    @Expose
    private String n;
    @SerializedName("C")
    @Expose
    private Integer c;
    @SerializedName("TY")
    @Expose
    private Integer ty;
    @SerializedName("TY_N")
    @Expose
    private String tyN;

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

    public Integer getC() {
        return c;
    }

    public void setC(Integer c) {
        this.c = c;
    }

    public Integer getTy() {
        return ty;
    }

    public void setTy(Integer ty) {
        this.ty = ty;
    }

    public String getTyN() {
        return tyN;
    }

    public void setTyN(String tyN) {
        this.tyN = tyN;
    }


    public  boolean Click;
}
