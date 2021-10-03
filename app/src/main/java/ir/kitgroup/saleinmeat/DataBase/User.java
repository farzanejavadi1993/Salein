package ir.kitgroup.saleinmeat.DataBase;

import com.orm.SugarRecord;

public class User extends SugarRecord {
    public String userName;
    public String passWord;
    public String ipLocal;
    public String ipStatic;
    public String numberPos;
    public double lat;
    public double lng;
    public boolean CheckUser=false;



}
