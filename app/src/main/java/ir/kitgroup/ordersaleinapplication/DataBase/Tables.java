package ir.kitgroup.ordersaleinapplication.DataBase;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class Tables extends SugarRecord {
    @Unique
    public  String I;//SYS_UID
    public  String N;//SYS_NUMBER
    public  Integer CC;//SYS_CHAIR_COUNT
    public  Boolean ST;//SYS_STATUS
    public  Boolean ACT;//true busy red        false free green
    public  Boolean RSV;//true blue
    public  Integer C;//get out order


    public Tables(){}
}
