package ir.kitgroup.saleinOrder.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Keep
public class CustomTab {

    @SerializedName("T")
    @Expose
    private Integer t;
    @SerializedName("TN")
    @Expose
    private String tn;

    public  boolean Click;

    public Integer getT() {
        return t;
    }

    public void setT(Integer t) {
        this.t = t;
    }

    public String getTn() {
        return tn;
    }

    public void setTn(String tn) {
        this.tn = tn;
    }

}