package ir.kitgroup.saleinjam.Connect;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import ir.kitgroup.saleinjam.DataBase.Account;
import ir.kitgroup.saleinjam.DataBase.Company;
import ir.kitgroup.saleinjam.models.Advertise;
import ir.kitgroup.saleinjam.models.AppDetail;
import ir.kitgroup.saleinjam.models.Log;

public class MainRepository {

    private final MainApi api;

    @Inject
    public MainRepository(MainApi api) {
        this.api = api;
    }

    public Observable<List<Log>> addAccountToServer(String accounts) {
        return api.addAccountToServer("",accounts);
    }

    public Observable<List<AppDetail>> getApp(String id) {
        return api.getApp(id);
    }

    public Observable<List<Account>> getCustomerFromServer(String mobile) {
        return api.getCustomerFromServer(mobile);
    }


    public Observable<List<Company>> getCompany(String id) {
        return api.getCompany(id);
    }

    public Observable<String> getAllCompany(String parentAccountId,int page) {
        return api.getAllCompany(parentAccountId,1,page);
    }
    public Observable<List<Advertise>> getAdvsByCompanyId(List<String> companiesId, int page, String appId, String customerId) {
        return api.getAdvsByCompanyId(companiesId, page,appId,customerId);
    }
}
