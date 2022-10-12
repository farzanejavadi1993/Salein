package ir.kitgroup.salein.Connect;

import java.util.List;

import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.models.AppDetail;
import ir.kitgroup.salein.models.Log;
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

  @POST("GetAccount")
    Observable<List<Company>> getCompany(@Query("id") String id);

   @GET("GetCustomerFromServer")
    Observable<List<Account>> getCustomerFromServer(@Query("mobile") String mobile);
}
