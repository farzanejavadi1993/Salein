package ir.kitgroup.salein1.DataBase;

import com.orm.SugarRecord;

import java.util.Date;

public class Invoice extends SugarRecord {

    public String TBL_UID;//ای دی میز
    public String INV_UID ; //ای دی فاکتور
    public String INV_TOTAL_AMOUNT ;// جمع فاکنور
    public String INV_TOTAL_DISCOUNT ;//جمع تخفیف فاکتور
    public String INV_PERCENT_DISCOUNT ;//درصد تخفیف فاکتور
    public String INV_DET_TOTAL_DISCOUNT ;// جمع تخفیفات ردیف فاکتور
    public String INV_EXTENDED_AMOUNT ;//جمع خالص فاکتور
    public Date INV_DATE ;//تاریخ
    public Date INV_DUE_DATE ;//تاریخ تحویل
    public String INV_DESCRIBTION;//توضیحات
    public Boolean INV_STATUS ;//حذف نشدهtrue و حذف شده false
    public String ACC_CLB_UID;//ای دی مشترک
    public String INV_TOTAL_TAX ;//مالیات
    public String INV_TOTAL_COST;//هزینه
    /// <summary>
    /// در انتظار پرداخت 1
    /// دارای مانده 2
    /// تسویه شده 3
    /// ابطال 4
    /// </summary>
    public String INV_PAYMENT_STATUS ;
    public Boolean INV_FORMAL;//رسمی true
    public String INV_TYPE_ORDER ; // نوع سفارش بیرون بر یا داخلی
    public String Acc_name ;
    public Boolean SendStatus=false ;


    public String getTableID() {
        return TBL_UID;
    }

    public String getFactorID() {
        return INV_UID;
    }

    public String getSumPrice() {
        return INV_TOTAL_AMOUNT;
    }

    public String getSumDiscountRow() {
        return INV_DET_TOTAL_DISCOUNT;
    }
    public String getSumPurePrice() {
        return INV_EXTENDED_AMOUNT;
    }

    public Date getDateOrder() {
        return INV_DATE;
    }

    public Date getDeliveryDate() {
        return INV_DUE_DATE;
    }

    public String getDescription() {
        return INV_DESCRIBTION;
    }



    public String getAccountId() {
        return ACC_CLB_UID;
    }





    public String getTypeOrder() {
        return INV_TYPE_ORDER;
    }




}
