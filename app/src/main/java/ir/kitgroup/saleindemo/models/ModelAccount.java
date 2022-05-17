package ir.kitgroup.saleindemo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.kitgroup.saleindemo.DataBase.User;
import androidx.annotation.Keep;
@Keep
public class ModelAccount {

    @SerializedName("Account")
    @Expose
    private List<User> AccountList = null;



    public List<User> getAccountList() {
        return AccountList;
    }
    public void setAccountList(List<User> AccountList) {
        this.AccountList = AccountList;
    }


}