package ir.kitgroup.saleintop.Connect;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {

    @GET("productSync")
    Call<String> getProduct(@Query("token") String token, @Query("userName") String userName, @Query("password") String password,@Query("task") String task);


    @GET("AccountSync")
    Call<String> getAccount(@Query("token") String token, @Query("userName") String userName, @Query("password") String password);


    @POST("SyncData")
    Call<String> PostData(@Query("userName") String userName, @Query("password") String password,
                          @Body() String jsonObject,
                          @Query("virtualParam") String virtualParam,
                          @Query("numberPos") String numberPos);

    @GET("OrderTypeSync")
    Call<String> getOrderType(@Query("userName") String userName, @Query("password") String password);


    @GET("SettingSync")
    Call<String> getSetting(@Query("userName") String userName, @Query("password") String password);

    @GET("login")
    Call<String> Login(@Query("userName") String userName, @Query("password") String password);


    @GET("MaxSaleSync")
    Call<String> getMaxSales(@Query("userName") String userName, @Query("password") String password, @Query("productId") String productId);

    @GET("DescriptionSync")
    Call<String> getDescription(@Query("userName") String userName, @Query("password") String password, @Query("productId") String productId);


    @POST("CreateAccount")
    Call<String> addAccount(@Query("userName") String userName, @Query("password") String password
   , @Body() String jsonObject,@Query("virtualParam") String virtualParam);

    @GET("tableSync")
    Call<String> getTable(@Query("userName") String userName, @Query("password") String password);


    @GET("AccountSearch")
    Call<String> getAccountSearch(@Query("token") String token, @Query("userName") String userName, @Query("password") String password, @Query("word") String word);


    @GET("AccountSync")
    Call<String> getInquiryAccount(@Query("userName") String userName, @Query("password") String password, @Query("mobile") String mobile, @Query("code") String code, @Query("card") String card, @Query("task") int task);


    @GET("SendSms")
    Call<String> getSmsLogin(@Query("userName") String userName, @Query("password") String passWord,@Query("message") String message,@Query("mobile") String mobile);


    @GET("GetInvoice")
    Call<String> getInvoice(@Query("userName") String userName, @Query("passWord") String passWord,@Query("InvoiceId") String InvoiceId);




    @GET("GetAllInvoice")
    Call<String> getAllInvoice(@Query("userName") String userName, @Query("passWord") String passWord,@Query("customerId") String customerId,@Query("date") String date);




    @GET("DeleteInvoice")
    Call<String> getDeleteInvoice(@Query("userName") String userName, @Query("passWord") String passWord,@Query("InvoiceId") String InvoiceId);



    @POST("UpdateAccount")
    Call<String> UpdateAccount(@Query("userName") String userName, @Query("passWord") String passWord, @Body() String jsonAccount,
                               @Query("virtualParam") String virtualParam);


}