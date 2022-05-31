package ir.kitgroup.salein.DataBase;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.io.Serializable;

@Keep
public class InvoiceDetail extends SugarRecord implements Serializable {

    @Unique
    @SerializedName("INV_DET_UID")//ای دی ردیف
    @Expose
    public String INV_DET_UID;

    @SerializedName("INV_UID")//ای دی فاکتور
    @Expose
    public String INV_UID;

    @SerializedName("ROW_NUMBER")//شماره ردیف
    @Expose
    public Integer ROW_NUMBER = 0;


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

    public Double getQuantity() {
        double quantity=0;
        if (INV_DET_QUANTITY!=null)
            quantity=INV_DET_QUANTITY;
        return quantity ;
    }

    public void setINV_DET_QUANTITY(Double INV_DET_QUANTITY) {
        this.INV_DET_QUANTITY = INV_DET_QUANTITY;
    }

    @SerializedName("INV_DET_QUANTITY")//مقدار ردیف
    @Expose
    public Double INV_DET_QUANTITY = 0.0;

    @SerializedName("UOM_UID")
    @Expose
    public String uomUid;

    public Double getPrice() {
        double price=0.0;
        if (INV_DET_PRICE_PER_UNIT!=null)
            price=Double.parseDouble(INV_DET_PRICE_PER_UNIT);
        return price;
    }

    public void setINV_DET_PRICE_PER_UNIT(String INV_DET_PRICE_PER_UNIT) {
        this.INV_DET_PRICE_PER_UNIT = INV_DET_PRICE_PER_UNIT;
    }

    @SerializedName("INV_DET_PRICE_PER_UNIT")//فی کالا
    @Expose
    public String INV_DET_PRICE_PER_UNIT;

    public Double getDiscount() {

        double discount = 0.0;
        if (INV_DET_PERCENT_DISCOUNT != null)
            discount = INV_DET_PERCENT_DISCOUNT;
        return discount;
    }

    public void setINV_DET_PERCENT_DISCOUNT(Double INV_DET_PERCENT_DISCOUNT) {
        this.INV_DET_PERCENT_DISCOUNT = INV_DET_PERCENT_DISCOUNT;
    }

    @SerializedName("INV_DET_PERCENT_DISCOUNT")//درصد تخفیف کالا
    @Expose
    public Double INV_DET_PERCENT_DISCOUNT = 0.0;


    @SerializedName("INV_DET_DISCOUNT")//مبلغ تخفیف کالا
    @Expose
    public String INV_DET_DISCOUNT = "0";

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
    public Double invDetPricePerUnitExchange = 0.0;
    @SerializedName("INV_DET_PRICE_EXCHANGE")
    @Expose
    public Double invDetPriceExchange = 0.0;
    @SerializedName("INV_DET_DESCRIBTION")//توضیح ردیف
    @Expose
    public String INV_DET_DESCRIBTION;


    public String TBL;


}
