package ir.kitgroup.saleinkonj.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.kitgroup.saleinkonj.DataBase.Tables;

public class ModelTable {
    @SerializedName("Table")
    @Expose
    private List<Tables> tables = null;
    public List<Tables> getTables() {
        return tables;
    }

}