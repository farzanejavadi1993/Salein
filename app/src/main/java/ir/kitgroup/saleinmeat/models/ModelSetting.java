package ir.kitgroup.saleinmeat.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelSetting {

    @SerializedName("Setting")
    @Expose
    private List<Setting> settings = null;
    public List<Setting> getSettings() {
        return settings;
    }



}