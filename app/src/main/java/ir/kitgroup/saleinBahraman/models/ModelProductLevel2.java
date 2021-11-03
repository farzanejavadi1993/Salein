package ir.kitgroup.saleinBahraman.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelProductLevel2 {
    @SerializedName("ProductLevel2")
    @Expose
    private List<ProductLevel2> productLevel2List = null;
    public List<ProductLevel2> getProductLevel2() {
        return productLevel2List;
    }
}