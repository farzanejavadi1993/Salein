package ir.kitgroup.saleinpakhshyab.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.kitgroup.saleinpakhshyab.DataBase.Account;

import androidx.annotation.Keep;
@Keep
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

    public void setLog(Log log) {
        this.log = log;
    }

    @SerializedName("Log")
    @Expose
    private Log log = null;




}