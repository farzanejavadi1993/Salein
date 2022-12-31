package ir.kitgroup.saleinfingilkabab.Connect;

import java.util.List;

import ir.kitgroup.saleinfingilkabab.DataBase.Account;
import ir.kitgroup.saleinfingilkabab.DataBase.Company;
import ir.kitgroup.saleinfingilkabab.models.AppDetail;
import ir.kitgroup.saleinfingilkabab.models.Log;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import io.reactivex.Observable;

public interface MainApi {
       @POST("AddCustomerToServer")
    Observable<List<Log>> addAccountToServer(@Query("companyId") String companyId
            , @Body()  String jsonObject);

    @POST("GetApp")
    Observable<List<AppDetail>> getApp(@Query("id") String id);

    @POST("GetAccountDetailById")
    Observable<List<Company>> getCompany(@Query("id") String id);

   @GET("GetCustomerFromServer")
    Observable<List<Account>> getCustomerFromServer(@Query("mobile") String mobile);


    @GET("GetAccount")
    Observable<String> getAllCompany(@Query("parentAccountId") String parentAccountId ,@Query("appType") int appType ,@Query("page") int page);
}