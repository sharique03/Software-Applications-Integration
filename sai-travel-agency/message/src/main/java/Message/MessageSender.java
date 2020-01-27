package Message;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MessageSender {

private Connection connection;
    private Session session;
    private Destination sendDestination;
    private MessageProducer producer;
//sets up the connection to send msg
    public MessageSender(String queue){
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            sendDestination = session.createQueue(queue);
            producer = session.createProducer(sendDestination);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
//create msg from string json
    public Message create(String json){
        try {
            Message requestMsg = this.session.createTextMessage(json);
            return requestMsg;
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }
//sends msg
    public void sendMessage(Message msg){
        try{
            producer.send(msg);
        }
        catch (JMSException e) {
            e.printStackTrace();
        }


    }


}
