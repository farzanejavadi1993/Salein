package ir.kitgroup.saleinkhavari.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import androidx.annotation.Keep;
@Keep
public class ModelLog {
    @SerializedName("Log")
    @Expose
    private List<Log> logs = null;
    public List<Log> getLogs() {
        return logs;
    }

}
