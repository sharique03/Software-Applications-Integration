package booking.client.gateway;

import booking.client.model.ClientBookingReply;
import booking.client.model.ClientBookingRequest;
import org.json.JSONObject;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.UUID;

import Message.*;

public abstract class ClientAppGateway {

    //saves all ClientBookingRequest to use when receive response from broker
    private HashMap<String, ClientBookingRequest> clientMap;
    public MessageSender messageSender ;
    private MessageReceiver messageReceiver ;
    public ClientAppGateway() {
        this.clientMap = new HashMap<>();
        this.messageSender = new MessageSender("BookingRequest");
        this.messageReceiver = new MessageReceiver("BookingReply");
        MessageListener ml = new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                try {
                    ClientBookingRequest clientBookingRequest = clientMap.get(msg.getJMSCorrelationID());
                    JSONObject jsonObject = new JSONObject(((TextMessage)msg).getText());

                    String id = jsonObject.getString("id");
                    String agencyName = jsonObject.getString("agencyName");
                    double price = jsonObject.getDouble("totalPrice");
                    ClientBookingReply clientBookingReply = new ClientBookingReply(id,agencyName,price);
                    onReplyReceived(clientBookingRequest,clientBookingReply);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        };
        messageReceiver.setMessageListener(ml);
    }
//sends msg using message gateway
    public void send(ClientBookingRequest clientBookingRequest){
        JSONObject json = new JSONObject(clientBookingRequest);
        Message msg = messageSender.create(json.toString());
        try {
            msg.setJMSCorrelationID(UUID.randomUUID().toString());
            messageSender.sendMessage(msg);
            clientMap.put(msg.getJMSCorrelationID(),clientBookingRequest);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * to send back the control along with req & rep to controller
     * @param clientBookingRequest
     * @param clientBookingReply
     */
   public abstract void onReplyReceived(ClientBookingRequest clientBookingRequest, ClientBookingReply clientBookingReply);

}
