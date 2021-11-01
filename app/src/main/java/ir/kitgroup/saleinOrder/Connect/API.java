package ir.kitgroup.saleinOrder.Connect;

import io.reactivex.Observable;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {

    @POST("SyncData")
    Call<String> PostData(@Query("userName") String userName, @Query("password") String password,
                          @Body() String jsonObject,
                          @Query("virtualParam") String virtualParam,
                          @Query("numberPos") String numberPos);


    @GET("DeleteInvoice")
    Call<String> getDeleteInvoice(@Query("userName") String userName, @Query("passWord") String passWord, @Query("InvoiceId") String InvoiceId);




    @GET("login")
    Observable<String> Login(@Query("userName") String userName, @Query("password") String password);


    @POST("CreateAccount")
    Observable<String> addAccount(@Query("userName") String userName, @Query("password") String password
            , @Body() String jsonObject, @Query("virtualParam") String virtualParam);






    @GET("SendSms")
    Observable<String> getSmsLogin(@Query("userName") String userName, @Query("password") String passWord, @Query("message") String message, @Query("mobile") String mobile, @Query("task") Integer task);


    @POST("UpdateAccount")
    Call<String> UpdateAccount(@Query("userName") String userName, @Query("passWord") String passWord, @Body() String jsonAccount,
                               @Query("virtualParam") String virtualParam);

    @GET("productSync")
    Observable<String> getProduct(@Query("userName") String userName, @Query("password") String password, @Query("productId") String productId);


    @GET("AccountSync")
    Observable<String> getInquiryAccount1(@Query("userName") String userName, @Query("password") String password, @Query("mobile") String mobile, @Query("code") String code, @Query("card") String card, @Query("task") int task, @Query("isShowPassWord") int isShowPassWord);


    @GET("GetImage")
    Call<String> getImage(@Query("productId") String productId);


    @GET("SettingSync")
    Observable<String> getSetting1(@Query("userName") String userName, @Query("password") String password);

    @GET("productSync")
    Observable<String> getProduct1(@Query("token") String token, @Query("userName") String userName, @Query("password") String password, @Query("productLevel2Uid") String productLevel2Uid);


    @GET("GetAllInvoice")
    Observable<String> getAllInvoice1(@Query("userName") String userName, @Query("passWord") String passWord, @Query("customerId") String customerId, @Query("date") String date);


    @GET("tableSync")
    Observable<String> getTable1(@Query("userName") String userName, @Query("password") String password);

    @GET("OrderTypeSync")
    Observable<String> getOrderType1(@Query("userName") String userName, @Query("password") String password);


    @GET("GetInvoice")
    Observable<String> getInvoice1(@Query("userName") String userName, @Query("passWord") String passWord, @Query("InvoiceId") String InvoiceId);


    @GET("AccountSearch")
    Observable<String>  getAccountSearch1(@Query("token") String token, @Query("userName") String userName, @Query("password") String password, @Query("word") String word);


    @GET("DescriptionSync")
    Observable<String> getDescription1(@Query("userName") String userName, @Query("password") String password, @Query("productId") String productId);



    @GET("MaxSaleSync")
    Observable<String> getMaxSales(@Query("userName") String userName, @Query("password") String password, @Query("productId") String productId);


    @GET("ProductLevel2Sync")
    Observable<String> getProductLevel2(@Query("token") String token, @Query("userName") String userName, @Query("password") String password, @Query("productLevel1Uid") String productLevel1Uid);

    @GET("ProductLevel1Sync")
    Observable<String> getProductLevel1(@Query("token") String token, @Query("userName") String userName, @Query("password") String password);


    @GET("ProductSearch")
    Observable<String> getSearchProduct(@Query("token") String token,@Query("userName") String userName, @Query("password") String password, @Query("word") String word);


    @POST("CreateAccount")
    Observable<String> addAccount1(@Query("userName") String userName, @Query("password") String password
            , @Body() String jsonObject, @Query("virtualParam") String virtualParam);

}