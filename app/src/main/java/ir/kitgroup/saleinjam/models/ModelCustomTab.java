package ir.kitgroup.saleinjam.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelCustomTab {
    @SerializedName("CustomTab")
    @Expose
    private List<CustomTab> customTab = null;

    public List<CustomTab> getCustomTab() {
        return customTab;
    }

    public void setCustomTab(List<CustomTab> customTab) {
        this.customTab = customTab;
    }
}
