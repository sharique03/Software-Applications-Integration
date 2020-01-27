package booking.agency.gateway;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import Message.*;
import booking.agency.model.AgencyReply;
import booking.agency.model.AgencyRequest;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.HashMap;

public abstract class AgencyAppGateway {
//to save relationship between corelationID & aggregationID
    private HashMap<String,Integer> aggregationIdMap=new HashMap<>();
    public MessageSender messageSender ;
    private MessageReceiver messageReceiver ;
//recQueueName for each agency
    public AgencyAppGateway(String recQueueName) {
        this.messageReceiver = new MessageReceiver(recQueueName);
        this.messageSender = new MessageSender("AgencyReply");
        MessageListener ml = new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                try {
                    JSONObject jsonObject = new JSONObject(((TextMessage)msg).getText());
                    String fromAirport = jsonObject.getString("fromAirport");
                    String toAirport = jsonObject.getString("toAirport");
                    String dateString=jsonObject.getString("date");
                    LocalDate date=LocalDate.parse(dateString);
                    int nrTravellers = jsonObject.getInt("nrTravellers");
                    boolean registeredClient=jsonObject.getBoolean("registeredClient");
                    aggregationIdMap.put(msg.getJMSCorrelationID(),msg.getIntProperty("aggregationId"));
                    AgencyRequest agencyRequest=new AgencyRequest(msg.getJMSCorrelationID(),fromAirport,toAirport,date,nrTravellers,registeredClient);
                    onRequestReceived(agencyRequest);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        };
        messageReceiver.setMessageListener(ml);
    }
    //sends agencyReply
    public void send(AgencyReply agencyReply){
        String correlationId=agencyReply.getId();
        JSONObject json = new JSONObject(agencyReply);
        Message msg = messageSender.create(json.toString());
        try {
            msg.setJMSCorrelationID(correlationId);
            msg.setIntProperty("aggregationId",aggregationIdMap.get(correlationId));
            messageSender.sendMessage(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public abstract void onRequestReceived(AgencyRequest agencyRequest);
}
