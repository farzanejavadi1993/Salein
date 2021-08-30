package ir.kitgroup.salein1order.DataBase;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class ProductGroupLevel1 extends SugarRecord {

    @Unique
    @SerializedName("I")
    @Expose
    private String I;
    @SerializedName("PI")
    @Expose
    private String PI;
    @SerializedName("BID")
    @Expose
    private String BID;
    @SerializedName("FID")
    @Expose
    private String FID;
    @SerializedName("N")
    @Expose
    private String N;
    @SerializedName("C")
    @Expose
    private String C;
    @SerializedName("STS")
    @Expose
    private Boolean STS;
    @SerializedName("TKN")
    @Expose
    private Integer TKN;

    public String getPRDLVLUID() {
        return I;
    }

    public void setPRDLVLUID(String pRDLVLUID) {
        this.I = pRDLVLUID;
    }


    public String getPRDLVLNAME() {
        return N;
    }


    public Integer getPRDLVLTOUCHKEYNUMBER() {
        return TKN;
    }



    public  boolean Click;
    public  boolean setImage;
    public ProductGroupLevel1(){}


}
