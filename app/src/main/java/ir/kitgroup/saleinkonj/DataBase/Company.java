package ir.kitgroup.saleinkonj.DataBase;

import com.orm.SugarRecord;

public class Company extends SugarRecord {
    public String I;
    public String N;
    public String USER;
    public String IP1;//آی پی خارجی
    public String IP2;//آی پی داخلی
    public String T1;
    public String PASS;
    public String M1;
    public String PI;
    public String TXT1;
    public String ABUS;
    public Boolean Open=false;
    public Boolean Parent;
    public String LAT;
    public String LONG;
    public String DESC="";
    public String INSK_ID;
    public String CITY;
    public int mode = 2;
    public int imageDialog;
    public String numberPos="";


}
