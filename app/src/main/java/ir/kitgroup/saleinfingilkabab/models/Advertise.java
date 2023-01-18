package ir.kitgroup.saleinfingilkabab.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Advertise {

    public String getId() {
        return id;
    }

    @SerializedName("Id")
    @Expose
    private String id;

    @SerializedName("CompanyId")
    @Expose
    private String companyId;

    @SerializedName("CompanyName")
    @Expose
    private String companyName;

    @SerializedName("Title")
    @Expose
    private String title;

    @SerializedName("Description")
    @Expose
    private String description;

    @SerializedName("IsActive")
    @Expose
    private Boolean isActive;

    @SerializedName("StartDate")
    @Expose
    private String startDate;

    @SerializedName("ExpirationDate")
    @Expose
    private String expirationDate;

    @SerializedName("IsSaved")
    @Expose
    private boolean isSaved;

    @SerializedName("DTypeText")
    @Expose
    public String dTypeText;

    @SerializedName("Count")
    @Expose
    private Integer count;

    @SerializedName("Phone")
    @Expose
    private String phone;

    @SerializedName("Link")
    @Expose
    private String link;


    public boolean isSpeacial() {
        return speacial;
    }

    public void setSpeacial(boolean speacial) {
        this.speacial = speacial;
    }

    private boolean speacial;

    public String getPhone() {
        String tel="";
        if (!phone.equals(""))
            tel=phone;

        return tel;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLink() {
        String l="";
        if (!link.equals(""))
            l=link;

        return l;
    }

    public void setLink(String link) {
        this.link = link;
    }





    public String getI() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean getIsSaved() {
        return isSaved;
    }
    public void setIsSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }



    public String getdTypeText() {
        return dTypeText;
    }

    public void setdTypeText(String dTypeText) {
        this.dTypeText = dTypeText;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}


