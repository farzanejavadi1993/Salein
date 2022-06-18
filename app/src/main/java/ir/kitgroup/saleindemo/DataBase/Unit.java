package ir.kitgroup.saleindemo.DataBase;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
@Keep
public class Unit extends SugarRecord{

    @SerializedName("UOM_UID")
    @Expose
    private String uomUid;
    @SerializedName("UOM_NAME")
    @Expose
    private String uomName;

    public String getUomUid() {
        return uomUid;
    }

    public void setUomUid(String uomUid) {
        this.uomUid = uomUid;
    }

    public String getUomName() {
        return uomName;
    }

    public void setUomName(String uomName) {
        this.uomName = uomName;
    }

}
