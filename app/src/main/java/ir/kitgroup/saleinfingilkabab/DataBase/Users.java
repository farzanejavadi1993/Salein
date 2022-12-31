package ir.kitgroup.saleinfingilkabab.DataBase;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

@Keep
public class Users extends SugarRecord {


    @Unique
    @SerializedName("I")
    @Expose
    public String I;
    @SerializedName("N")
    @Expose
    public String N;
    @SerializedName("C")
    @Expose
    private String C;
    @SerializedName("M")
    @Expose
    public String M;
    @SerializedName("STAPP")
    @Expose
    public Boolean STAPP;
    @SerializedName("STS")
    @Expose
    public boolean STS;
    @SerializedName("M2")
    @Expose
    private String M2;
    @SerializedName("CRDT")
    @Expose
    public Double CRDT;
    @SerializedName("PSW")
    @Expose
    public String PSW;
    @SerializedName("P1")
    @Expose
    private String P1;
    @SerializedName("PC")//INTRODUCTION CODE
    @Expose
    public String PC;
    @SerializedName("LAT")
    @Expose
    public String LAT;
    @SerializedName("LNG")
    @Expose
    public String LNG;
    @SerializedName("LAT1")
    @Expose
    public String LAT1;
    @SerializedName("LNG1")
    @Expose
    public String LNG1;
    @SerializedName("ADR")
    @Expose
    public String ADR;
    @SerializedName("ADR2")
    @Expose
    public String ADR2;
    @SerializedName("S")
    @Expose
    public String S;










}
