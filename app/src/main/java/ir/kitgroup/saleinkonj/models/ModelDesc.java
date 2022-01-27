package ir.kitgroup.saleinkonj.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelDesc {
    @SerializedName("Description")
    @Expose
    private List<Description> descriptions = null;
    public List<Description> getDescriptions() {
        return descriptions;
    }

}