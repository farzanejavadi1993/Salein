package ir.kitgroup.salein1.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.kitgroup.salein1.DataBase.Account;
import ir.kitgroup.salein1.DataBase.OrderType;

public class ModelTypeOrder {

    @SerializedName("OrderType")
    @Expose
    private List<OrderType> orderTypes = null;



    public List<OrderType> getOrderTypes() {
        return orderTypes;
    }



}