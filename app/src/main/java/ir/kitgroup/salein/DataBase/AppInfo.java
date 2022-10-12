package ir.kitgroup.salein.DataBase;

import com.orm.SugarRecord;

public class AppInfo extends SugarRecord {
  private   String application_code="";
  private String gif_url="";
  private boolean salein_main=false;

  public boolean isSalein_main() {
    return salein_main;
  }

  public void setSalein_main(boolean salein_main) {
    this.salein_main = salein_main;
  }





  public String getApplication_code() {
    return application_code;
  }

  public void setApplication_code(String application_code) {
    this.application_code = application_code;
  }

  public String getGif_url() {
    String gift="";
    if (gif_url!=null)
      gift=gif_url;
    return gif_url;
  }

  public void setGif_url(String gif_url) {
    this.gif_url = gif_url;
  }



}
