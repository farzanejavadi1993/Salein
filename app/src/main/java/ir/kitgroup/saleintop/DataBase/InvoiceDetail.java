package ir.kitgroup.saleintop.DataBase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class InvoiceDetail extends SugarRecord {

    @Unique
    @SerializedName("INV_DET_UID")//ای دی ردیف
    @Expose
    public String INV_DET_UID;
    
    @SerializedName("INV_UID")//ای دی فاکتور
    @Expose
    public String INV_UID;
    
    @SerializedName("ROW_NUMBER")//شماره ردیف
    @Expose
    public Integer ROW_NUMBER;
    
    
    @SerializedName("WAR_HOS_UID")
    @Expose
    public String warHosUid;
    @SerializedName("PRD_NAME")
    @Expose
    public String prdName;
    @SerializedName("PRD_CODE")
    @Expose
    public String prdCode;
    @SerializedName("PRD_BARCODE")
    @Expose
    public Object prdBarcode;
    
    @SerializedName("INV_DET_QUANTITY")//مقدار ردیف
    @Expose
    public Double INV_DET_QUANTITY;
   
    @SerializedName("UOM_UID")
    @Expose
    public String uomUid;
   
    @SerializedName("INV_DET_PRICE_PER_UNIT")//فی کالا
    @Expose
    public String INV_DET_PRICE_PER_UNIT;
    
    @SerializedName("INV_DET_PERCENT_DISCOUNT")//درصد تخفیف کالا
    @Expose
    public Double INV_DET_PERCENT_DISCOUNT;


    @SerializedName("INV_DET_DISCOUNT")//مبلغ تخفیف کالا
    @Expose
    public String INV_DET_DISCOUNT;
    
    @SerializedName("INV_DET_TOTAL_AMOUNT")//جمع کل ردیف
    @Expose
    public String INV_DET_TOTAL_AMOUNT;
    
    @SerializedName("INV_DET_PRICE_PER_UNIT_BEFORE_DISCOUNT")
    @Expose
    public Object invDetPricePerUnitBeforeDiscount;
    
    @SerializedName("INV_DET_TAX")//مالیات
    @Expose
    public String INV_DET_TAX;
    
    
    @SerializedName("INV_DET_STATUS")//وضعیت true
    @Expose
    public Boolean INV_DET_STATUS;
    
    
    @SerializedName("PRD_UID")//ای دی کالا
    @Expose
    public String PRD_UID;
    
    @SerializedName("INV_DET_TAX_VALUE")//درصد مالیات
    @Expose
    public String INV_DET_TAX_VALUE;
    @SerializedName("INV_DET_PRICE_PER_UNIT_EXCHANGE")
    @Expose
    public Double invDetPricePerUnitExchange;
    @SerializedName("INV_DET_PRICE_EXCHANGE")
    @Expose
    public Double invDetPriceExchange;
    @SerializedName("INV_DET_DESCRIBTION")//توضیح ردیف
    @Expose
    public String INV_DET_DESCRIBTION;


    public String TBL;


}
