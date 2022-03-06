package ir.kitgroup.saleinmeat.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.kitgroup.saleinmeat.DataBase.Tables;

public class ModelTable {
    @SerializedName("Table")
    @Expose
    private List<Tables> tables = null;
    public List<Tables> getTables() {
        return tables;
    }

}