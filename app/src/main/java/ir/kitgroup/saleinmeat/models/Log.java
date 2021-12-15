package ir.kitgroup.saleinmeat.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Log {

    @SerializedName("TypeClass")
    @Expose
    private String typeClass;
    @SerializedName("Current")
    @Expose
    private String current;
    @SerializedName("Message")
    @Expose
    private Integer message;
    @SerializedName("Description")
    @Expose
    private String description;

    public String getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(String typeClass) {
        this.typeClass = typeClass;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public Integer getMessage() {
        return message;
    }

    public void setMessage(Integer message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
