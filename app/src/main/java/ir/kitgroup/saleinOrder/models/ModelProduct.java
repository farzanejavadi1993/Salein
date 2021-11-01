package ir.kitgroup.saleinOrder.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;




public class ModelProduct {



    @SerializedName("Product")
    @Expose
    private List<Product> ProductList = null;
    public List<Product> getProductList() {
        return ProductList;
    }
    public void setProductList(List<Product> productList) {
        this.ProductList = productList;
    }
}