package ir.kitgroup.salein1.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.kitgroup.salein1.DataBase.Setting;

public class ModelLog {
    @SerializedName("Log")
    @Expose
    private List<Log> logs = null;
    public List<Log> getLogs() {
        return logs;
    }

}
