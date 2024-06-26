package ir.kitgroup.salein.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import androidx.annotation.Keep;
@Keep
public class ModelDesc {
    @SerializedName("Description")
    @Expose
    private List<Description> descriptions = null;
    public List<Description> getDescriptions() {
        return descriptions;
    }

}