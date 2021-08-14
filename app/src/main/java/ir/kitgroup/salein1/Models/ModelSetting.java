package ir.kitgroup.salein1.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.kitgroup.salein1.DataBase.OrderType;
import ir.kitgroup.salein1.DataBase.Setting;

public class ModelSetting {

    @SerializedName("Setting")
    @Expose
    private List<Setting> settings = null;
    public List<Setting> getSettings() {
        return settings;
    }



}