package ir.kitgroup.saleinmeat.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.kitgroup.saleinmeat.DataBase.ProductGroupLevel1;
import ir.kitgroup.saleinmeat.DataBase.ProductGroupLevel2;

public class ModelProduct {

    @SerializedName("ProductLevel1")
    @Expose
    private List<ProductGroupLevel1> ProductLevel1List = null;

    @SerializedName("ProductLevel2")
    @Expose
    private List<ProductGroupLevel2> ProductLevel2List = null;

    @SerializedName("Product")
    @Expose
    private List<ir.kitgroup.saleinmeat.DataBase.Product> ProductList = null;

    public List<ProductGroupLevel1> getProductLevel1List() {
        return ProductLevel1List;
    }
    public void setProductLevel1List(List<ProductGroupLevel1> productLevel1List) {
        this.ProductLevel1List = productLevel1List;
    }

    public List<ProductGroupLevel2> getProductLevel2List() {
        return ProductLevel2List;
    }
    public void setProductLevel2List(List<ProductGroupLevel2> productLevel2List) {
        this.ProductLevel2List = productLevel2List;
    }

    public List<ir.kitgroup.saleinmeat.DataBase.Product> getProductList() {
        return ProductList;
    }
    public void setProductList(List<ir.kitgroup.saleinmeat.DataBase.Product> productList) {
        this.ProductList = productList;
    }
}