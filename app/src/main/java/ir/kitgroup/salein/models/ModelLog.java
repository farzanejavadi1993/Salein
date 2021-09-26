package ir.kitgroup.salein.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelLog {
    @SerializedName("Log")
    @Expose
    private List<Log> logs = null;
    public List<Log> getLogs() {
        return logs;
    }

}
