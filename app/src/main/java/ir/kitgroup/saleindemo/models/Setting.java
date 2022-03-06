package ir.kitgroup.saleindemo.models;

import com.orm.dsl.Unique;

public class Setting  {
    @Unique
    public String DEFAULT_PRICE_INVOICE;
    public String MAX_SALE;
    public String DEFAULT_CUSTOMER;
    public String ORDER_TYPE_APP;
    public String SERVICE_TIME;
    public String UPDATE_APP;
    public String VERSION_APP;
    public String SERVICE_DAY;
    public String CLOSE_DAY="1,6";
    public String PEYK;
    public String PAYMENT_TYPE;
    public String LINK_UPDATE;
    public String LINK_PAYMENT;
    public String ACC_STATUS_APP;

}
