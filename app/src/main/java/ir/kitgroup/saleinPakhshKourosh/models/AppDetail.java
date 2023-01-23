package ir.kitgroup.saleinPakhshKourosh.models;



import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class AppDetail {
    @SerializedName("AppId")
    @Expose
    private String appId;
    @SerializedName("UserName")
    @Expose
    private Object userName;
    @SerializedName("DisplayName")
    @Expose
    private Object displayName;
    @SerializedName("IsActive")
    @Expose
    private Boolean isActive;
    @SerializedName("LastLoggedIn")
    @Expose
    private Object lastLoggedIn;
    @SerializedName("Password")
    @Expose
    private Object password;
    @SerializedName("Roles")
    @Expose
    private Object roles;
    @SerializedName("SerialNumber")
    @Expose
    private Object serialNumber;
    @SerializedName("Forced")
    @Expose
    private Boolean forced;
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Link")
    @Expose
    private String link;
    @SerializedName("UpdateDesc")
    @Expose
    private String updateDesc;
    @SerializedName("UpdateTitle")
    @Expose
    private String updateTitle;
    @SerializedName("Version")
    @Expose
    private String version;


    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @SerializedName("CityName")
    @Expose
    private String cityName;


    @SerializedName("GuildName")
    @Expose
    private String guildName;


    @SerializedName("StateName")
    @Expose
    private String stateName;
    public String getGuildName() {
        return guildName;
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    @SerializedName("GuildId")
    @Expose
    private String guildId;

    @SerializedName("StateId")
    @Expose
    private String stateId;
    @SerializedName("CityId")
    @Expose
    private String cityId;

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    @SerializedName("ShopName")
    @Expose
    private String shopName;

    @SerializedName("Relation")
    @Expose
    private List<String> relation;

    @SerializedName("AccountId")
    @Expose
    private String accountId;


    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public List<String> getRelation() {
        return relation;
    }

    public void setRelation(List<String> relation) {
        this.relation = relation;
    }


    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }





    public String getIemi() {
        return iemi;
    }

    public void setIemi(String iemi) {
        this.iemi = iemi;
    }

    @SerializedName("IMEI")
    @Expose
    private String iemi;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Object getUserName() {
        return userName;
    }

    public void setUserName(Object userName) {
        this.userName = userName;
    }

    public Object getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Object displayName) {
        this.displayName = displayName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Object getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(Object lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    public Object getPassword() {
        return password;
    }

    public void setPassword(Object password) {
        this.password = password;
    }

    public Object getRoles() {
        return roles;
    }

    public void setRoles(Object roles) {
        this.roles = roles;
    }

    public Object getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Object serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Boolean getForced() {
        return forced;
    }

    public void setForced(Boolean forced) {
        this.forced = forced;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUpdateDesc() {
        return updateDesc;
    }

    public void setUpdateDesc(String updateDesc) {
        this.updateDesc = updateDesc;
    }

    public String getUpdateTitle() {
        return updateTitle;
    }

    public void setUpdateTitle(String updateTitle) {
        this.updateTitle = updateTitle;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
