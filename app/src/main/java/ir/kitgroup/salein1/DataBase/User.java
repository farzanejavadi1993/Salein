package ir.kitgroup.salein1.DataBase;

import com.orm.SugarRecord;

public class User extends SugarRecord {
    public String userName;
    public String passWord;
    public String ipLocal;
    public String ipStatic;
    public String numberPos;
    public boolean CheckUser;

    public String ACCGID ;
}
