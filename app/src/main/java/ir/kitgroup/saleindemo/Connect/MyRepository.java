package ir.kitgroup.saleindemo.Connect;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import javax.inject.Inject;
import io.reactivex.Observable;
import ir.kitgroup.saleindemo.DataBase.Account;
import ir.kitgroup.saleindemo.DataBase.InvoiceDetail;
import ir.kitgroup.saleindemo.Fragments.CompanyFragment;
import ir.kitgroup.saleindemo.Fragments.MapFragment;
import ir.kitgroup.saleindemo.ui.launcher.orderItem.OrderListFragment;
import ir.kitgroup.saleindemo.Fragments.PaymentMobileFragment;
import ir.kitgroup.saleindemo.models.Invoice;
import ir.kitgroup.saleindemo.models.PaymentRecieptDetail;


public class MyRepository {
    private final API api;

    @Inject
    public MyRepository(API api) {
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
    public Observable<String> getListProduct(String user, String passWord, String prd2Id) {
        return api.getProduct1("saleinkit_api",user,passWord,prd2Id);
    }

    public Observable<String> getProduct(String user,String passWord,String prdId) {
        return api.getProduct(user,passWord,prdId);
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

    public Observable<String> getSearchProduct(String user,String passWord,String word) {
        return api.getSearchProduct("saleinkit_api",user,passWord,word);
    }

    public Observable<String> getAllInvoice(String user,String passWord,String accId,String date) {
        return api.getAllInvoice1(user,passWord,accId,date);
    }

    public Observable<String> sendFeedBack(String user, String passWord,List<Invoice> invoice, List<InvoiceDetail> invoiceDetail, List<PaymentRecieptDetail> clsPaymentRecieptDetail) {
        OrderListFragment.JsonObject jsonObject = new OrderListFragment.JsonObject();
        jsonObject.Invoice = invoice;
        jsonObject.InvoiceDetail = invoiceDetail;
        jsonObject.PaymentRecieptDetail = clsPaymentRecieptDetail;

        Gson gson = new Gson();
        Type typeJsonObject = new TypeToken<OrderListFragment.JsonObject>() {
        }.getType();
        return api.sendFeedBack(user,passWord,gson.toJson(jsonObject, typeJsonObject));
    }


    public Observable<String> sendOrder(String user,String passWord,List<Invoice> invoice, List<InvoiceDetail> invoiceDetail, List<PaymentRecieptDetail> clsPaymentRecieptDetail,String numberPos) {
        PaymentMobileFragment.JsonObject jsonObject = new PaymentMobileFragment.JsonObject();
        jsonObject.Invoice = invoice;
        jsonObject.InvoiceDetail = invoiceDetail;
        jsonObject.PaymentRecieptDetail = clsPaymentRecieptDetail;

        Gson gson = new Gson();
        Type typeJsonObject = new TypeToken<PaymentMobileFragment.JsonObject>() {
        }.getType();
        return api.sendOrder(user,passWord, gson.toJson(jsonObject, typeJsonObject),"",numberPos);
    }




    public Observable<String> getTable(String user,String passWord) {
        return api.getTable1(user,passWord); }


    public Observable<String> getTypeOrder(String user,String passWord) {
        return api.getOrderType1(user,passWord); }


    public Observable<String> updateAccount(String user,String passWord,List<Account> accounts) {
        MapFragment.JsonObjectAccount jsonObjectAcc = new MapFragment.JsonObjectAccount();
        jsonObjectAcc.Account = accounts;

        Gson gson = new Gson();
        Type typeJsonObject = new TypeToken<MapFragment.JsonObjectAccount>() {
        }.getType();
        return api.updateAccount(user,passWord,gson.toJson(jsonObjectAcc, typeJsonObject),"");
    }

    public Observable<String> login(String user,String passWord) {
        return api.Login(user,passWord); }



}
