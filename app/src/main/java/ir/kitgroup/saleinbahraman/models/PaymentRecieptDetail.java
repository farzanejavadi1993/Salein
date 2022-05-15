package ir.kitgroup.saleinbahraman.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import androidx.annotation.Keep;
@Keep
public class PaymentRecieptDetail {

    @SerializedName("PAY_RCIPT_DET_UID")//ای دی
    @Expose
    public String PAY_RCIPT_DET_UID;

    @SerializedName("PAY_RCIPT_DET_TOTAL_AMOUNT")//مبلغ
    @Expose
    public Double PAY_RCIPT_DET_TOTAL_AMOUNT ;


    @SerializedName("PAY_RCIPT_DET_DESCRIBTION")//توضیح
    @Expose
    public String PAY_RCIPT_DET_DESCRIBTION  ;

    @SerializedName("PAY_RCIPT_DET_DRAFT")//شماره پیگیری
    @Expose
    public String PAY_RCIPT_DET_DRAFT ;


    @SerializedName("PAY_RCIPT_DET_TYPE")//نوع پرداخت
    @Expose
    public String PAY_RCIPT_DET_TYPE  ;

    /// <summary>
    /// 1 - naghd,
    /// 2 - kart,
    /// 3 - bon,
    /// 4 - bashgah,
    /// 5 - etebari,
    /// 6 - marjoee,
    /// 7 - arzi,
    /// 8 - tasvie ba forush,
    /// 9 - barge sarafi,
    /// 11 - pish faktor,
    /// 21 - chek daryafti,
    /// </summa



    @SerializedName("BANK_UID")//آی دی بانک
    @Expose
    public String BANK_UID   ;
}
