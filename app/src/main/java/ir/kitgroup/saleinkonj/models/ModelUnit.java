package ir.kitgroup.saleinkonj.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.kitgroup.saleinkonj.DataBase.Unit;

public class ModelUnit {

    @SerializedName("Unit")
    @Expose
    private List<Unit> unit = null;

    public List<Unit> getUnit() {
        return unit;
    }

    public void setUnit(List<Unit> unit) {
        this.unit = unit;
    }

}