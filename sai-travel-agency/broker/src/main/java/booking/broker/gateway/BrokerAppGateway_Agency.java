package booking.broker.gateway;

import Message.MessageReceiver;
import Message.MessageSender;
import booking.agency.model.AgencyReply;
import booking.agency.model.AgencyRequest;
import org.json.JSONObject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.HashMap;


public abstract class BrokerAppGateway_Agency {
    //saves all the response from agency
    private ArrayList<Message> agencyReplyMessages=new ArrayList<>();
    //saves all the request to agency
    private ArrayList<AgencyRequest> agencyRequestList=new ArrayList<>();
    //map of aggregationId & corresponding no. of requests
    private HashMap<Integer,Integer> aggregationCountReq =new HashMap<>();
    //map of aggregationId & corresponding no. of replies
    private HashMap<Integer,Integer> aggregationCountRep=new HashMap<>();
    private MessageReceiver messageReceiver ;
    private static int aggregationIdGenerator=1;
    public BrokerAppGateway_Agency() {

        this.messageReceiver = new MessageReceiver("AgencyReply");
        MessageListener ml = new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                try {
                    agencyReplyMessages.add(msg);
                    handleAggregation(msg);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        };
        messageReceiver.setMessageListener(ml);
    }

    //implements aggregation pattern
    //checks the count of msg received from agency
    // if all msgs are received then selects the response with the minimum tkt price
    public void handleAggregation(Message msg) throws JMSException {

        AgencyRequest agencyRequest=new AgencyRequest();
        AgencyReply agencyReply=new AgencyReply();
        int requestCount=0;
        int respCount=0;
        double minTicketPrice=0.0;
        int aggregationId=msg.getIntProperty("aggregationId");
        requestCount= aggregationCountReq.get(aggregationId);
        respCount=aggregationCountRep.get(aggregationId)+1;

        if(requestCount>respCount)
        {
            aggregationCountRep.put(aggregationId,respCount);
            //System.out.println(respCount + " responses received out of "+ requestCount+" for aggregation id : " + aggregationId);
            System.out.println("\nAggregation id : " + aggregationId);
            System.out.println("Total Request: " + requestCount);
            System.out.println("Total Response : " + respCount);
        }
        else
        {
            if (requestCount == respCount) {
                System.out.println("\nAll responses received for aggregation id : " + aggregationId);
                for (Message replyMessage : agencyReplyMessages) {
                    if (replyMessage.getIntProperty("aggregationId")==aggregationId) {
                        AgencyReply ag=getAgencyReplyFromMessage(replyMessage);
                        if (minTicketPrice == 0.0) {
                            minTicketPrice = ag.getPrice();
                            agencyReply=ag;
                        } else if(minTicketPrice>ag.getPrice()){
                            minTicketPrice =ag.getPrice();
                            agencyReply=ag;
                        }
                    }
                }
                agencyRequest= getAgencyRequestFromId(agencyReply.getId());
                onReplyReceived(agencyRequest,agencyReply);
            }
        }
}

    // generates agencyReply from JMS msg
    public AgencyReply getAgencyReplyFromMessage(Message msg)
    {
        AgencyReply agencyReply = new AgencyReply();
        try {
            JSONObject receivedJson = new JSONObject(((TextMessage) msg).getText());
            agencyReply.setId(msg.getJMSCorrelationID());
            agencyReply.setName(receivedJson.getString("name"));
            agencyReply.setPrice(receivedJson.getInt("price"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return agencyReply;
    }

    // generates agencyRequest from agencyRequestList
    public AgencyRequest getAgencyRequestFromId(String id)
    {
        AgencyRequest agencyRequest = new AgencyRequest();
       for (AgencyRequest a : agencyRequestList) {
        if (a.getId().equals(id)){
            agencyRequest=a;
            break;
        }
        }
       return agencyRequest;
    }

    //sends agencyRequest
    public void send(AgencyRequest agencyRequest,String queue){

       JSONObject json = new JSONObject(agencyRequest);
        MessageSender messageSender =new MessageSender(queue);
        Message msg = messageSender.create(json.toString());
        try {
            msg.setJMSCorrelationID(agencyRequest.getId());
            msg.setIntProperty("aggregationId",aggregationIdGenerator);
            messageSender.sendMessage(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    //implements recipient list
    public void sendRequestToAgencies(AgencyRequest agencyRequest)
    {
        int outboundMsgCount=0;
        agencyRequestList.add(agencyRequest);
        aggregationCountRep.put(aggregationIdGenerator,0);
        for (AgencyQueues queue:AgencyQueues.values()) {
            if(queue.evaluate(agencyRequest))
            {
                outboundMsgCount++;
                send(agencyRequest,queue.getQueue());
            }
        }
        aggregationCountReq.put(aggregationIdGenerator,outboundMsgCount);
        aggregationIdGenerator++;
    }
    public abstract void onReplyReceived(AgencyRequest agencyRequest,AgencyReply agencyReply);

}