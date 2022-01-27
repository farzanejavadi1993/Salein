package ir.kitgroup.saleinkonj.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.kitgroup.saleinkonj.DataBase.Company;

public class ModelCompany {

    @SerializedName("Company")
    @Expose
    private List<Company> company = null;

    public List<Company> getCompany() {
        return company;
    }

    public void setCompany(List<Company> company) {
        this.company = company;
    }

}
