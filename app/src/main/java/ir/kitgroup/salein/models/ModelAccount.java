package ir.kitgroup.salein.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.kitgroup.salein.DataBase.Users;
import androidx.annotation.Keep;
@Keep
public class ModelAccount {

    @SerializedName("Account")
    @Expose
    private List<Users> AccountList = null;



    public List<Users> getAccountList() {
        return AccountList;
    }
    public void setAccountList(List<Users> AccountList) {
        this.AccountList = AccountList;
    }


}