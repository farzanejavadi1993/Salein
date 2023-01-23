package ir.kitgroup.saleinPakhshKourosh.DataBase;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
@Keep
public class Company extends SugarRecord {
    @SerializedName("I")
    @Expose
    private String i;
    @SerializedName("N")
    @Expose
    private String n;
    @SerializedName("PI")
    @Expose
    private String pi;
    @SerializedName("T1")
    @Expose
    private String t1;
    @SerializedName("M1")
    @Expose
    private String m1;
    @SerializedName("LONG")
    @Expose
    private String _long;
    @SerializedName("LAT")
    @Expose
    private String lat;
    @SerializedName("IP1")
    @Expose
    private String ip1;
    @SerializedName("USER")
    @Expose
    private String user;
    @SerializedName("PASS")
    @Expose
    private String pass;
    @SerializedName("DESC")
    @Expose
    private String desc;
    @SerializedName("INSK_ID")
    @Expose
    private String inskId;
    @SerializedName("COUNTRY")
    @Expose
    private String country;
    @SerializedName("STATE")
    @Expose
    private String state;
    @SerializedName("CITY")
    @Expose
    private String city;
    @SerializedName("ABUS")
    @Expose
    private String abus;
    @SerializedName("TXT1")
    @Expose
    private String txt1;
    @SerializedName("TELG")
    @Expose
    private String telg;
    @SerializedName("WHATA")
    @Expose
    private String whata;
    @SerializedName("EMAIL")
    @Expose
    private String email;
    @SerializedName("WEBS")
    @Expose
    private String webs;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @SerializedName("AppId")
    @Expose
    private String appId;

    public String get_long() {
        return _long;
    }

    public void set_long(String _long) {
        this._long = _long;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getParent() {
        return Parent;
    }

    public void setParent(Boolean parent) {
        Parent = parent;
    }

    public Boolean getClick() {
        return click;
    }

    public void setClick(Boolean click) {
        this.click = click;
    }

    private String version;

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getPi() {
        return pi;
    }

    public void setPi(String pi) {
        this.pi = pi;
    }

    public String getT1() {
        return t1;
    }

    public void setT1(String t1) {
        this.t1 = t1;
    }

    public String getM1() {
        return m1;
    }

    public void setM1(String m1) {
        this.m1 = m1;
    }

    public Double getLong() {
        double longitude = 0.0;
        if (!_long.equals("") && !_long.equals("-") && _long != null)
            longitude = Double.parseDouble(_long);

        return longitude;
    }

    public void setLong(String _long) {
        this._long = _long;
    }

    public Double getLat() {

            double latitude = 0.0;
            if (!lat.equals("") && !lat.equals("-") && lat != null)
                latitude = Double.parseDouble(lat);

            return latitude;

    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getIp1() {
        return ip1;
    }

    public void setIp1(String ip1) {
        this.ip1 = ip1;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getInskId() {
        return inskId;
    }

    public void setInskId(String inskId) {
        this.inskId = inskId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAbus() {
        return abus;
    }

    public void setAbus(String abus) {
        this.abus = abus;
    }

    public String getTxt1() {
        return txt1;
    }

    public void setTxt1(String txt1) {
        this.txt1 = txt1;
    }

    public String getTelg() {
        return telg;
    }

    public void setTelg(String telg) {
        this.telg = telg;
    }

    public String getWhata() {
        return whata;
    }

    public void setWhata(String whata) {
        this.whata = whata;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebs() {
        return webs;
    }

    public void setWebs(String webs) {
        this.webs = webs;
    }

    public Boolean Parent;

    public Boolean click =false;
}
