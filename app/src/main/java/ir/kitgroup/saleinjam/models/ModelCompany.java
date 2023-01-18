package ir.kitgroup.saleinjam.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.kitgroup.saleinjam.DataBase.Company;
import androidx.annotation.Keep;
@Keep
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
