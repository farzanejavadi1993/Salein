package ir.kitgroup.salein1.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.kitgroup.salein1.DataBase.Account;

public class ModelAccount {

    @SerializedName("Account")
    @Expose
    private List<Account> AccountList = null;



    public List<Account> getAccountList() {
        return AccountList;
    }
    public void setAccountList(List<Account> AccountList) {
        this.AccountList = AccountList;
    }


}