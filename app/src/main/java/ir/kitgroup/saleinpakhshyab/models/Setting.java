package ir.kitgroup.saleinpakhshyab.models;

import com.orm.dsl.Unique;
import androidx.annotation.Keep;
@Keep
public class Setting  {
    @Unique
    public String DEFAULT_PRICE_INVOICE; //قیمت فروش در اپلیکیشن
    public String MAX_SALE;//بررسی مانده کالا    0بررسی شود    1بررسی نشود
    public String DEFAULT_CUSTOMER;
    public String ORDER_TYPE_APP;//کد نوع سفارشی مه شامل هزینه توزیع نمیشود
    public String SERVICE_TIME;//زمان های تحویل سفارش
    public String SERVICE_DAY;//روزهای تحویل سفارش
    public String CLOSE_DAY="";//روزهای تعطیل مجموعه
    public String PEYK;//آی دی کالای توزیع
    public String PAYMENT_TYPE;//نوع پرداخت   1پرداخت در محل    2پرداخت با باشگاه   3هردو
    public String LINK_PAYMENT;//لینک باشگاه
    public String ACC_STATUS_APP;//تایید و یا عدم تایید مشترک     1تایید شده   0تایید نشده
    public String COEF;//1 بر اساس ضریب 1                 2بر اساس ضریب 2           بدون مقدار سفارش گیری پیش فرض
    public String MENU="0";//1 منوی سفارشی               بدون مقدار منوی اصلی
}
