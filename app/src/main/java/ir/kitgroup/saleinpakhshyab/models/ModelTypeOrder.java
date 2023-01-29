package ir.kitgroup.saleinpakhshyab.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import androidx.annotation.Keep;
@Keep
public class ModelTypeOrder {

    @SerializedName("OrderType")
    @Expose
    private List<OrderType> orderTypes = null;



    public List<OrderType> getOrderTypes() {
        return orderTypes;
    }



}