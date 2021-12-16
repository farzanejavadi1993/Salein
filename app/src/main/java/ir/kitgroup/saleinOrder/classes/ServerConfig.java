package ir.kitgroup.saleinOrder.classes;

import android.os.StrictMode;

import com.orm.query.Select;

import java.net.URL;
import java.net.URLConnection;



public class ServerConfig {


    public String URL;
    public String IP1;
    public String IP2;

    public ServerConfig(String IP1, String IP2) {

        this.IP1 = IP1;
        this.IP2 = IP2;
        URL = isConnectedToServer(3000);
    }

    private String isConnectedToServer(int timeout) {
        try {


            java.net.URL myUrl = new URL("http://"+IP1+"/api/REST/getWelcome");
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return IP1;
        } catch (Exception e) {

            try {
                java.net.URL myUrl = new URL("http://" +IP2+ "/api/REST/getWelcome");

                URLConnection connection = myUrl.openConnection();
                connection.setConnectTimeout(timeout);
                connection.connect();

                return IP2;
            } catch (Exception ex) {
                return "";
            }
        }
    }
}
