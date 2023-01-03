package ir.kitgroup.saleinfingilkabab.Connect;

import com.google.gson.JsonElement;


import io.reactivex.Observable;

import ir.kitgroup.saleinfingilkabab.classes.Util;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface CompanyAPI {

    @POST("SyncData")
    Observable<String> sendOrder(@Query("userName") String userName, @Query("password") String password,
                                 @Body() String jsonObject,
                                 @Query("virtualParam") String virtualParam,
                                 @Query("numberPos") String numberPos);

    @GET("DeleteInvoice")
    Observable<String> getDeleteInvoice(@Query("userName") String userName, @Query("passWord") String passWord, @Query("InvoiceId") String InvoiceId);


    @GET("login")
    Observable<String> Login(@Query("userName") String userName, @Query("password") String password);


    @GET("UnitSync")
    Observable<String> getUnitSync(@Query("userName") String userName, @Query("password") String password);


    @POST("CreateAccount")
    Observable<String> addAccount(@Query("userName") String userName, @Query("password") String password
            , @Body() Util.JsonObjectAccount jsonObject);

    @GET("CalculateDelivery")
    Observable<Integer> getTransportationCost(@Query("latCustomer") double latCustomer, @Query("longCustomer") double longCustomer, @Query("latCompany") double latCompany,
                                              @Query("longCompany") double lonCompany,
                                              @Query("invoiceprice") double invoicePrice);


    @POST("UpdateInvoiceDesc")
    Observable<String> sendFeedBack(@Query("userName") String userName, @Query("password") String password,
                                    @Body() String jsonObject);


    @GET("SendSms")
    Observable<String> getSmsLogin(@Query("userName") String userName, @Query("password") String passWord, @Query("message") String message, @Query("mobile") String mobile, @Query("task") Integer task);


    @POST("UpdateAccount")
    Observable<String> updateAccount(@Query("userName") String userName, @Query("passWord") String passWord, @Body() String jsonAccount, @Query("virtualParam") String virtualParam);

    @GET("productSync")
    Observable<String> getProduct(@Query("userName") String userName, @Query("password") String password, @Query("productId") String productId);


    @GET("AccountSync")
    Observable<String> getInquiryAccount1(@Query("userName") String userName, @Query("password") String password, @Query("mobile") String mobile, @Query("code") String code, @Query("card") String card, @Query("task") int task, @Query("isShowPassWord") int isShowPassWord);


    @GET("SettingSync")
    Observable<String> getSetting1(@Query("userName") String userName, @Query("password") String password);


    @GET("GetAllInvoice")
    Observable<String> getAllInvoice1(@Query("userName") String userName, @Query("passWord") String passWord, @Query("customerId") String customerId, @Query("date") String date);


    @GET("tableSync")
    Observable<String> getTable1(@Query("userName") String userName, @Query("password") String password);

    @GET("OrderTypeSync")
    Observable<String> getOrderType1(@Query("userName") String userName, @Query("password") String password);


    @GET("GetInvoice")
    Observable<String> getInvoice1(@Query("userName") String userName, @Query("passWord") String passWord, @Query("InvoiceId") String InvoiceId);


    @GET("AccountSearch")
    Observable<String> getAccountSearch1(@Query("token") String token, @Query("userName") String userName, @Query("password") String password, @Query("word") String word);


    @GET("DescriptionSync")
    Observable<String> getDescription1(@Query("userName") String userName, @Query("password") String password, @Query("productId") String productId);


    @GET("MaxSaleSync")
    Observable<String> getMaxSales(@Query("userName") String userName, @Query("password") String password, @Query("productId") String productId);

    @GET("ProductLevel1Sync")
    Observable<String> getProductLevel1(@Query("token") String token, @Query("userName") String userName, @Query("password") String password);

    @GET("ProductLevel2Sync")
    Observable<String> getProductLevel2(@Query("token") String token, @Query("userName") String userName, @Query("password") String password, @Query("productLevel1Uid") String productLevel1Uid);

    @GET("productSync")
    Observable<String> getProduct1(@Query("token") String token, @Query("userName") String userName, @Query("password") String password, @Query("productLevel2Uid") String productLevel2Uid);


    @GET("CustomTabSync")
    Observable<JsonElement> getCustomTab(@Query("token") String token, @Query("userName") String userName, @Query("password") String password);


    @GET("ProductCustomSync")
    Observable<JsonElement> getProductCustomSync(@Query("token") String token, @Query("userName") String userName, @Query("password") String password, @Query("tabNumber") int key);


    @GET("ProductSearch")
    Observable<String> getSearchProduct(@Query("token") String token, @Query("userName") String userName, @Query("password") String password, @Query("word") String word);


    @GET("ProductDiscountSync")
    Observable<String> getProductDiscountSync(@Query("token") String token, @Query("userName") String userName, @Query("password") String password);


    @GET("ProductVipSync")
    Observable<String> getProductVipSync(@Query("token") String token, @Query("userName") String userName, @Query("password") String password, @Query("customerId") String customerId);


}