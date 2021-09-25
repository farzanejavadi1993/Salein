package ir.kitgroup.saleinmeat.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.kitgroup.saleinmeat.DataBase.OrderType;

public class ModelTypeOrder {

    @SerializedName("OrderType")
    @Expose
    private List<OrderType> orderTypes = null;



    public List<OrderType> getOrderTypes() {
        return orderTypes;
    }



}