package ir.kitgroup.salein.DataBase;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class ProductGroupLevel2 extends SugarRecord {

    @Unique
    @SerializedName("I")
    @Expose
    public String I;
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
    public Integer TKN;
    @SerializedName("CSMT")
    @Expose
    private Integer CSMT;
    @SerializedName("CSMB")
    @Expose
    private Integer CSMB;

    public String getPRDLVLUID() {
        return I;
    }

    public void setPRDLVLUID(String pRDLVLUID) {
        this.I = pRDLVLUID;
    }

    public String getPRDLVLPARENTUID() {
        return PI;
    }



    public String getPRDLVLNAME() {
        return N;
    }



    public Integer getPRDLVLTOUCHKEYNUMBER() {
        return TKN;
    }

    public boolean Click;


    public ProductGroupLevel2(){}

}