package ir.kitgroup.salein.models;

import com.orm.dsl.Unique;
import androidx.annotation.Keep;
@Keep
public class Setting  {
    @Unique
    public String DEFAULT_PRICE_INVOICE;
    public String MAX_SALE;
    public String DEFAULT_CUSTOMER;
    public String ORDER_TYPE_APP;
    public String SERVICE_TIME;
    public String UPDATE_APP;
    public String VERSION_APP;
    public String SERVICE_DAY;
    public String CLOSE_DAY="";
    public String PEYK;
    public String PAYMENT_TYPE;
    public String LINK_UPDATE;
    public String LINK_PAYMENT;
    public String ACC_STATUS_APP;
    public String COEF;//1 بر اساس ضریب 1                 2بر اساس ضریب 2           بدون مقدار سفارش گیری پیش فرض
    public String MENU="0";//1 منوی سفارشی               بدون مقدار منوی اصلی

}
