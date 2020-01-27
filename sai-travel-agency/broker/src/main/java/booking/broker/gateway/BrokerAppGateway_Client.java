package booking.broker.gateway;

import Message.MessageReceiver;
import Message.MessageSender;

import booking.client.model.ClientBookingReply;
import booking.client.model.ClientBookingRequest;
import org.json.JSONObject;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public abstract class BrokerAppGateway_Client {
    public MessageSender messageSender ;
    private MessageReceiver messageReceiver ;
    public BrokerAppGateway_Client() {
        this.messageSender = new MessageSender("BookingReply");
        this.messageReceiver = new MessageReceiver("BookingRequest");
        MessageListener ml = new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                try {
                    JSONObject jsonObject = new JSONObject(((TextMessage)msg).getText());
                    String originAirport = jsonObject.getString("originAirport");
                    String destinationAirport = jsonObject.getString("destinationAirport");
                    int clientID = jsonObject.getInt("clientID");
                    int numberOfTravellers = jsonObject.getInt("numberOfTravellers");
                    ClientBookingRequest clientBookingRequest = new ClientBookingRequest(msg.getJMSCorrelationID(), originAirport,destinationAirport, numberOfTravellers, clientID);
                    onRequestReceived(clientBookingRequest);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        };
        messageReceiver.setMessageListener(ml);
    }
    //sends clientBookingReply
    public void send(ClientBookingReply clientBookingReply){
        JSONObject json = new JSONObject(clientBookingReply);
        Message msg = messageSender.create(json.toString());
        try {
            msg.setJMSCorrelationID(clientBookingReply.getId());
            messageSender.sendMessage(msg);
            } catch (JMSException e) {
            e.printStackTrace();
        }
    }

   public abstract void onRequestReceived(ClientBookingRequest clientBookingRequest);

}
