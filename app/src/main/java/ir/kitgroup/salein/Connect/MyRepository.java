package ir.kitgroup.salein.Connect;

import android.content.SharedPreferences;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;


import io.reactivex.Observable;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.Fragments.CompanyFragment;
import ir.kitgroup.salein.classes.ConfigRetrofit;


public class MyRepository {
    private API api;
    private SharedPreferences sharedPreferences;


    @Inject
    public MyRepository(API api,SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;

        this.api = api;
    }


    public Observable<String> getSmsLogin(String user,String passWord,String message, String mobile) {
        return api.getSmsLogin(user,passWord, message, mobile, 2);
    }


    public Observable<String> getCompany(String parentAccountId) {
        return api.getCompany(parentAccountId);
    }


    public Observable<String> getInquiryAccount(String user,String passWord,String mobile) {
        return api.getInquiryAccount1(user,passWord, mobile,"", "", 1, 1);
    }


    public Observable<String> addAccount(String user,String passWord,List<Account> accounts) {
        CompanyFragment.JsonObjectAccount jsonObjectAcc = new CompanyFragment.JsonObjectAccount();
        jsonObjectAcc.Account = accounts;
        Gson gson1 = new Gson();
        Type typeJsonObject = new TypeToken<CompanyFragment.JsonObjectAccount>() {
        }.getType();
        return api.addAccount(user,passWord,gson1.toJson(jsonObjectAcc, typeJsonObject),"");
    }


    public Observable<String> getAccountSearch(String user,String passWord,String word) {
        return api.getAccountSearch1("saleinkit_api",user,passWord,word);
    }

    public Observable<String> getSetting(String user,String passWord) {
        return api.getSetting1(user,passWord);
    }

    public Observable<String> getProductLevel1(String user,String passWord) {
        return api.getProductLevel1("saleinkit_api",user,passWord);
    }

    public Observable<String> getProductLevel2(String user,String passWord,String prd1Id) {
        return api.getProductLevel2("saleinkit_api",user,passWord,prd1Id);
    }
    public Observable<String> getProduct1(String user,String passWord,String prd2Id) {
        return api.getProduct1("saleinkit_api",user,passWord,prd2Id);
    }

    public Observable<String> getDiscountPercent(String user,String passWord) {
        return api.getProductDiscountSync("saleinkit_api",user,passWord);
    }

    public Observable<String> getProductVipSync(String user,String passWord,String accountId) {
        return api.getProductVipSync("saleinkit_api",user,passWord,accountId);
    }

    public Observable<String> getUnit(String user,String passWord) {
        return api.getUnitSync(user,passWord);
    }

    public Observable<String> getDescription(String user,String passWord,String Id) {
        return api.getDescription1(user,passWord,Id);
    }

    public Observable<String> getInvoice(String user,String passWord,String Inv_GUID) {
        return api.getInvoice1(user,passWord,Inv_GUID);
    }

    public Observable<String> deleteInvoice(String user,String passWord,String Inv_GUID) {
        return api.getDeleteInvoice(user,passWord,Inv_GUID);
    }

    public Observable<String> getMaxSales(String user,String passWord,String PrdId) {
        return api.getMaxSales(user,passWord,PrdId);
    }
}
