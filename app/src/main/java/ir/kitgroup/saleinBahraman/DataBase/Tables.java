package ir.kitgroup.saleinBahraman.DataBase;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class Tables extends SugarRecord {
    @Unique
    public  String I;//SYS_UID
    public  String GO;//GET OUT ORDER NAME CUSTOMER
    public  String DATE;//DATE ORDER
    public  String N;//SYS_NUMBER
    public  Integer CC;//SYS_CHAIR_COUNT
    public  Boolean ST;//SYS_STATUS
    public  Boolean ACT;//true busy red        false free green
    public  Boolean RSV;//true blue
    public  Boolean SV;//true YELLOW    SAVE ORDER
    public  Integer C;//get out order
    public  String INVID;//Invoice Id


    public Tables(){}
}
