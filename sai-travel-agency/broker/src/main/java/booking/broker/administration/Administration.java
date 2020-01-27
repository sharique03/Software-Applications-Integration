package booking.broker.administration;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

public class Administration {
    private String id;
    public int discount;
    public String type;

    public Administration(String id, int discount, String type) {
        this.id=id;
        this.discount = discount;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Administration{" +
                "id=" + id +
                ", discount=" + discount +
                ", type='" + type + '\'' +
                '}';
    }

    public int getDiscount() {
        return discount;
    }

    public String getType() {
        return type;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setType(String type) {
        this.type = type;
    }

}
