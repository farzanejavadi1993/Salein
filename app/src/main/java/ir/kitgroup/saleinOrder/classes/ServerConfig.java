package ir.kitgroup.saleinOrder.classes;


import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ServerConfig {


    public String URL1;
    public String IP1;
    public String IP2;


    public ServerConfig(String IP1, String IP2) {

        this.IP1 = IP1;
        this.IP2 = IP2;

        URL1 = isConnectedToServer(3000);

    }


    private String isConnectedToServer(int timeout) {

        try {


            //java.net.URL myUrl = new URL("http://"+ IP1 +"/api/REST/getWelcome");
            URL myUrl1 = new URL("http://" + IP1 + "/api/REST/getWelcome");
            URLConnection connection1 = myUrl1.openConnection();
            connection1.setConnectTimeout(timeout);
            connection1.connect();
            if (((HttpURLConnection) connection1).getResponseCode() != 404)
                return IP1;
            else {
                URL myUrl2 = new URL("http://" + IP2 + "/api/REST/getWelcome");
                URLConnection connection2 = myUrl2.openConnection();
                connection2.setConnectTimeout(timeout);
                connection2.connect();
                if (((HttpURLConnection) connection1).getResponseCode() != 404);
                return IP2;
            }

        } catch (Exception ignored) {
            return IP1;
        }
    }
}
