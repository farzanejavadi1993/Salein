package ir.kitgroup.salein.DataBase;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class Setting extends SugarRecord {
    @Unique
    public String DEFAULT_PRICE_INVOICE;
    public String MAX_SALE;
    public String DEFAULT_CUSTOMER;
    public String ORDER_TYPE_APP;
    public String SERVICE_TIME;
    public String UPDATE_APP;
    public String VERSION_APP;
    public String SERVICE_DAY;
    public String PEYK;
}
