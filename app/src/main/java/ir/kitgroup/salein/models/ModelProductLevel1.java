package ir.kitgroup.salein.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import androidx.annotation.Keep;
@Keep
public class ModelProductLevel1 {
    @SerializedName("ProductLevel1")
    @Expose
    private List<ProductLevel1> productLevel1List = null;
    public List<ProductLevel1> getProductLevel1() {
        return productLevel1List;
    }
}
