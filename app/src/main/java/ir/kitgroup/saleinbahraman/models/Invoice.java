package ir.kitgroup.saleinbahraman.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import androidx.annotation.Keep;
@Keep
public class Invoice {


    @SerializedName("INV_UID")//ای دی فاکتور
    @Expose
    public String INV_UID;
    @SerializedName("INV_PARENT_UID")
    @Expose
    private String invParentUid;
    @SerializedName("BUS_UNIT_UID")
    @Expose
    private String busUnitUid;
    @SerializedName("FIS_PERIOD_UID")
    @Expose
    private String fisPeriodUid;
    @SerializedName("SAL_CAT_UID")
    @Expose
    private String salCatUid;
    @SerializedName("ACC_UID")
    @Expose
    private String accUid;
    @SerializedName("INV_NUMBER")
    @Expose
    public String invNumber;//شماره فیش
    @SerializedName("INV_DAILY_NUMBER")
    @Expose
    private String invDailyNumber;
    @SerializedName("INV_TOTAL_AMOUNT")// جمع فاکنور
    @Expose
    public Double INV_TOTAL_AMOUNT;
    @SerializedName("INV_TOTAL_DISCOUNT")//جمع تخفیف فاکتور
    @Expose
    public Double INV_TOTAL_DISCOUNT;
    @SerializedName("INV_PERCENT_DISCOUNT")//درصد تخفیف فاکتور
    @Expose
    public Double INV_PERCENT_DISCOUNT;
    @SerializedName("INV_DET_TOTAL_DISCOUNT")// جمع تخفیفات ردیف
    @Expose
    public Double INV_DET_TOTAL_DISCOUNT;
    @SerializedName("INV_TOTAL_TAX")//مالیات
    @Expose
    public Double INV_TOTAL_TAX;
    @SerializedName("INV_TOTAL_COST")//هزینه
    @Expose
    public Double INV_TOTAL_COST;
    @SerializedName("INV_DEPOSIT")
    @Expose
    private Double invDeposit;
    @SerializedName("INV_EXTENDED_AMOUNT")//جمع خالص فاکتور
    @Expose
    public Double INV_EXTENDED_AMOUNT;
    @SerializedName("INV_TOTAL_EXCHANGE")
    @Expose
    private Double invTotalExchange;
    @SerializedName("INV_DISCOUNT_EXCHANGE")
    @Expose
    private Double invDiscountExchange;
    @SerializedName("INV_EXTENDED_EXCHANGE")
    @Expose
    private String invExtendedExchange;
    @SerializedName("INV_REFERENCE")
    @Expose
    private String invReference;
    @SerializedName("INV_DATE")//تاریخ ارسال سفارش
    @Expose
    public Date INV_DATE;
    @SerializedName("INV_DATE1")//تاریخ تحویل
    @Expose
    public String INV_DATE1;
    @SerializedName("INV_DATE_PERSIAN")
    @Expose
    public String INV_DATE_PERSIAN;
    @SerializedName("INV_DUE_DATE")// تاریخ تحویل میلادی
    @Expose
    public Date INV_DUE_DATE;
    @SerializedName("INV_DUE_DATE1")
    @Expose
    public String INV_DUE_DATE1;
    @SerializedName("INV_DUE_DATE_PERSIAN")//  تاریخ تحویل شمسی
    @Expose
    public String INV_DUE_DATE_PERSIAN;
    @SerializedName("INV_DUE_TIME")//ساعت تحویل
    @Expose
    public String INV_DUE_TIME;
    @SerializedName("INV_DESCRIBTION")//توضیحات
    @Expose
    public String INV_DESCRIBTION;
    @SerializedName("INV_STATUS_CONTROL")
    @Expose
    private Boolean invStatusControl;
    @SerializedName("INV_STATUS")//حذف نشدهtrue و حذف شده false
    @Expose
    public Boolean INV_STATUS;
    @SerializedName("INV_FINAL_STATUS_CONTROL")
    @Expose
    private Boolean invFinalStatusControl;
    @SerializedName("INVOICE_FINAL_STATUS_CONTROL")
    @Expose
    private String invoiceFinalStatusControl;
    @SerializedName("SYS_USR_CREATEDON")
    @Expose
    private String sysUsrCreatedon;
    @SerializedName("SYS_USR_CREATEDBY")
    @Expose
    private String sysUsrCreatedby;
    @SerializedName("SYS_USR_MODIFIEDON")
    @Expose
    private String sysUsrModifiedon;
    @SerializedName("SYS_USR_MODIFIEDBY")
    @Expose
    private String sysUsrModifiedby;
    @SerializedName("ROW_NUMBER")
    @Expose
    private Integer rowNumber;
    @SerializedName("INV_TIME")
    @Expose
    private String invTime;
    @SerializedName("SYS_USR_USERNAME")
    @Expose
    private String sysUsrUsername;
    @SerializedName("CST_UID")
    @Expose
    private String cstUid;
    @SerializedName("ACC_CLB_UID")//ای دی مشترک
    @Expose
    public String ACC_CLB_UID;
    @SerializedName("ACC_CLB_ADDRESS")//آدرس1
    @Expose
    public String ACC_CLB_ADDRESS;
    @SerializedName("ACC_CLB_ADDRESS2")//آدرس2
    @Expose
    public String ACC_CLB_ADDRESS2;
    @SerializedName("ACC_CLB_DEFAULT_ADDRESS")//آدرس پیش فرض
    @Expose
    public String ACC_CLB_DEFAULT_ADDRESS;
    @SerializedName("ACC_CLB_LAT")
    @Expose
    private String accClbLat;
    @SerializedName("ACC_CLB_LONG")
    @Expose
    private String accClbLong;
    @SerializedName("ACC_CLB_LAT1")
    @Expose
    private String accClbLat1;
    @SerializedName("ACC_CLB_LONG1")
    @Expose
    private String accClbLong1;
    @SerializedName("INV_APPLICANT_UID")
    @Expose
    private String invApplicantUid;
    @SerializedName("ACC_NAME")
    @Expose
    private String accName;
    @SerializedName("ACC_CLB_NAME")
    @Expose
    public String ACC_CLB_NAME;
    @SerializedName("INV_PAYMENT_STATUS")
    @Expose
    private Integer invPaymentStatus;
    @SerializedName("INV_SECTION")
    @Expose
    private Integer invSection;
    @SerializedName("INV_START_TIME")
    @Expose
    private String invStartTime;
    @SerializedName("INV_END_TIME")
    @Expose
    private String invEndTime;
    @SerializedName("INV_CHARGE")
    @Expose
    private Boolean invCharge;
    @SerializedName("INV_SYNC")//وضعیت
    @Expose
    private String invSync;
    @SerializedName("INV_FORMAL")
    @Expose
    private Boolean invFormal;
    @SerializedName("INV_TYPE_ORDER")// نوع سفارش بیرون بر یا داخلی
    @Expose
    public Integer INV_TYPE_ORDER;
    @SerializedName("INV_CURRENCY")
    @Expose
    private Integer invCurrency;
    @SerializedName("TBL_UID")//ای دی میز
    @Expose
    public String TBL_UID;
    @SerializedName("INV_PRINT")
    @Expose
    private Boolean invPrint;
    @SerializedName("INV_DEFAULT_ADDRESS")//آدرس پیش فرض
    @Expose
    public String INV_DEFAULT_ADDRESS;
    @SerializedName("INV_STEP")
    @Expose
    public Integer INV_STEP;
    //1 در انتظار تایید
//2 تایید شده
//3 در حال ارسال
//4 تحویل شده


}
