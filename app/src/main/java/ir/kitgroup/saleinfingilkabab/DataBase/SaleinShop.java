package ir.kitgroup.saleinfingilkabab.DataBase;

import com.orm.SugarRecord;

public class SaleinShop extends SugarRecord {

  private   String application_code="";
  private String gif_url="";
  private boolean saleinApp =false;


  public boolean isPublicApp() {
    return saleinApp;
  }

  public void setSaleinApp(boolean saleinApp) {
    this.saleinApp = saleinApp;
  }


  public String getApplication_code() {
    return application_code;
  }
  public void setApplication_code(String application_code) {
    this.application_code = application_code;
  }


  public String getGif_url() {
    String gif="";

    if (gif_url!=null)
      gif=gif_url;

    return gif;
  }
  public void setGif_url(String gif_url) {
    this.gif_url = gif_url;
  }



}
