package ir.kitgroup.salein.DataBase;



import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import ir.kitgroup.salein.Util.Util;

public class Product extends SugarRecord {
    @Unique
    public String I;

    public String Url;


}
