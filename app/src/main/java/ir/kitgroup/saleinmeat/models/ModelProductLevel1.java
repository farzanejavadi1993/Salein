package ir.kitgroup.saleinmeat.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelProductLevel1 {
    @SerializedName("ProductLevel1")
    @Expose
    private List<ProductLevel1> productLevel1List = null;
    public List<ProductLevel1> getProductLevel1() {
        return productLevel1List;
    }
}
