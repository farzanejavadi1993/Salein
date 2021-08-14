package ir.kitgroup.salein1.DataBase;

import com.orm.SugarRecord;

public class Invoicedetail extends SugarRecord {


    public String INV_DET_UID ;//ای دی ردیف
    public String INV_UID;//ای دی فاکتور
    public String ROW_NUMBER ;//شماره ردیف
    public String INV_DET_QUANTITY ;//مقدار ردیف
    public String INV_DET_PRICE_PER_UNIT ;//فی کالا
    public String INV_DET_PERCENT_DISCOUNT ;//درصد تخفیف کالا
    public String INV_DET_DISCOUNT ;//مبلغ تخفیف کالا
    public String INV_DET_TOTAL_AMOUNT ;//جمع کل ردیف
    public String INV_DET_TAX ;//مالیات
    public Boolean INV_DET_STATUS ;//وضعیت true
    public String PRD_UID;//ای دی کالا
    public String INV_DET_TAX_VALUE ;//درصد مالیات
    public String INV_DET_DESCRIBTION ;//توضیح ردیف

}
