package ir.kitgroup.saleinmeat.DataBase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.util.Date;

public class Invoice extends SugarRecord {


    @Unique
    @SerializedName("INV_UID")//ای دی فاکتور
    @Expose
    public String INV_UID;


    @SerializedName("TBL_UID")//ای دی میز
    @Expose
    public String TBL_UID;



    @SerializedName("ACC_UID")
    @Expose
    public String accUid;

    @SerializedName("INV_TOTAL_AMOUNT")// جمع فاکنور
    @Expose
    public Double INV_TOTAL_AMOUNT;

    @SerializedName("INV_TOTAL_DISCOUNT")//جمع تخفیف فاکتور
    @Expose
    public Double INV_TOTAL_DISCOUNT;

    @SerializedName("INV_PERCENT_DISCOUNT")//درصد تخفیف فاکتور
    @Expose
    public Double INV_PERCENT_DISCOUNT;

    @SerializedName("INV_DET_TOTAL_DISCOUNT")// جمع تخفیفات ردیف فاکتور
    @Expose
    public Double INV_DET_TOTAL_DISCOUNT;

    @SerializedName("INV_TOTAL_TAX")//مالیات
    @Expose
    public Double INV_TOTAL_TAX;

    @SerializedName("INV_TOTAL_COST")//هزینه
    @Expose
    public Double INV_TOTAL_COST;

    @SerializedName("INV_DUE_DATE")//تاریخ تحویل
    @Expose
    public Date INV_DUE_DATE;

    @SerializedName("INV_STATUS")//حذف نشدهtrue و حذف شده false
    @Expose
    public Boolean INV_STATUS;

    @SerializedName("INV_DUE_DATE1")//تاریخ تحویل
    @Expose
    public String INV_DUE_DATE1;

    @SerializedName("INV_SYNC")//وضعیت
    @Expose
    public String INV_SYNC;

    @SerializedName("INV_EXTENDED_AMOUNT")//جمع خالص فاکتور
    @Expose
    public Double INV_EXTENDED_AMOUNT;


    @SerializedName("INV_DESCRIBTION")//توضیحات
    @Expose
    public String INV_DESCRIBTION;


    @SerializedName("ACC_CLB_UID")//ای دی مشترک
    @Expose
    public String ACC_CLB_UID;

    @SerializedName("INV_TYPE_ORDER")// نوع سفارش بیرون بر یا داخلی
    @Expose
    public Integer INV_TYPE_ORDER;


    @SerializedName("ACC_CLB_ADDRESS")//آدرس1
    @Expose
    public String ACC_CLB_ADDRESS;



    @SerializedName("ACC_CLB_ADDRESS2")//آدرس2
    @Expose
    public String ACC_CLB_ADDRESS2;



    @SerializedName("ACC_CLB_DEFAULT_ADDRESS")//آدرس پیش فرض
    @Expose
    public String ACC_CLB_DEFAULT_ADDRESS;


    @SerializedName("INV_DUE_TIME")//تاریخ تحویل
    @Expose
    public String INV_DUE_TIME;


    @SerializedName("INV_DUE_DATE_PERSIAN")//  شمسی تاریخ تحویل
    @Expose
    public String INV_DUE_DATE_PERSIAN;



    @SerializedName("INV_DATE_PERSIAN")
    @Expose
    public String INV_DATE_PERSIAN;








    @SerializedName("INV_DATE")
    @Expose
    public Date INV_DATE;


    @SerializedName("INV_DATE1")
    @Expose
    public String INV_DATE1;
    @SerializedName("ROW_NUMBER")
    @Expose
    public Integer rowNumber;
    @SerializedName("INV_PARENT_UID")
    @Expose
    public Object invParentUid;
    @SerializedName("BUS_UNIT_UID")
    @Expose
    public String busUnitUid;
    @SerializedName("FIS_PERIOD_UID")
    @Expose
    public String fisPeriodUid;
    @SerializedName("SAL_CAT_UID")
    @Expose
    public String salCatUid;
    @SerializedName("INV_NUMBER")
    @Expose
    public String invNumber;
    @SerializedName("INV_DAILY_NUMBER")
    @Expose
    public String invDailyNumber;
    @SerializedName("INV_DEPOSIT")
    @Expose
    public Double invDeposit;

    @SerializedName("INV_TOTAL_EXCHANGE")
    @Expose
    public Double invTotalExchange;
    @SerializedName("INV_DISCOUNT_EXCHANGE")
    @Expose
    public Double invDiscountExchange;
    @SerializedName("INV_EXTENDED_EXCHANGE")
    @Expose
    public Object invExtendedExchange;
    @SerializedName("INV_REFERENCE")
    @Expose
    public Object invReference;
    @SerializedName("INV_STATUS_CONTROL")
    @Expose
    public Boolean invStatusControl;
    @SerializedName("INV_FINAL_STATUS_CONTROL")
    @Expose
    public Boolean invFinalStatusControl;
    @SerializedName("INVOICE_FINAL_STATUS_CONTROL")
    @Expose
    public Object invoiceFinalStatusControl;
    @SerializedName("SYS_USR_CREATEDON")
    @Expose
    public String sysUsrCreatedon;
    @SerializedName("SYS_USR_CREATEDBY")
    @Expose
    public String sysUsrCreatedby;
    @SerializedName("SYS_USR_MODIFIEDON")
    @Expose
    public String sysUsrModifiedon;
    @SerializedName("SYS_USR_MODIFIEDBY")
    @Expose
    public String sysUsrModifiedby;
    @SerializedName("INV_TIME")
    @Expose
    public String invTime;
    @SerializedName("SYS_USR_USERNAME")
    @Expose
    public String sysUsrUsername;
    @SerializedName("CST_UID")
    @Expose
    public Object cstUid;



    @SerializedName("INV_APPLICANT_UID")
    @Expose
    public String invApplicantUid;
    @SerializedName("ACC_NAME")
    @Expose
    public Object accName;
    @SerializedName("ACC_CLB_NAME")
    @Expose
    public Object accClbName;
    @SerializedName("INV_PAYMENT_STATUS")
    @Expose
    public Integer invPaymentStatus;
    @SerializedName("INV_SECTION")
    @Expose
    public Integer invSection;
    @SerializedName("INV_START_TIME")
    @Expose
    public String invStartTime;
    @SerializedName("INV_END_TIME")
    @Expose
    public String invEndTime;
    @SerializedName("INV_CHARGE")
    @Expose
    public Boolean invCharge;
    @SerializedName("INV_FORMAL")
    @Expose
    public Boolean invFormal;
    @SerializedName("INV_CURRENCY")
    @Expose
    public Integer invCurrency;
    @SerializedName("INV_PRINT")
    @Expose
    public Boolean invPrint;















































   public String Acc_name ;
    public Boolean SendStatus=false ;






}
