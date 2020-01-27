package booking.broker.gateway;

import booking.broker.administration.Administration;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdministrationAppGateway {

    //calls Administration with custId and gets  resp with discount & type
    public static Administration getCustomerProfile(int customerId, String id)
    {
        URL url = null;
        Administration customerProfile=null;
        try {
            url = new URL("http://localhost:8080/administration/rest/client/" + customerId);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            System.out.println("customer id: " + customerId);
            String serverResponse = AdministrationAppGateway.doHttpRequest(con);
            System.out.println("Customer Profile Response: " + serverResponse);

            //response without blank body(json res)
            if (serverResponse != null && !serverResponse.equals(""))
            {
                JSONObject jsonObject = new JSONObject(serverResponse);
                customerProfile=new Administration(id,jsonObject.getInt("discount"),jsonObject.getString("type"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return customerProfile;
    }

    //executes http request and processes the response from admin
    public static String doHttpRequest(HttpURLConnection request) throws IOException {
        String responseContent="";
        BufferedInputStream br;
        int httpResponseCode = request.getResponseCode();
        if (200 <= httpResponseCode && httpResponseCode <= 299) {
            br = new BufferedInputStream(request.getInputStream());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                int read = br.read();
                while (read != -1) {
                    byteArrayOutputStream.write(read);
                    read = br.read();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
            responseContent = byteArrayOutputStream.toString();
            request.disconnect();
        }

        return responseContent;
    }


}
