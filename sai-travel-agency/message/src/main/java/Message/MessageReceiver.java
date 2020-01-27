package Message;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class MessageReceiver {

private Connection connection;
    private Session session;
    private Destination receiveDestination;
    private MessageConsumer messageConsumer;

    //sets up the msg connection to receive msg
    public MessageReceiver(String queue){
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            receiveDestination = session.createQueue(queue);
            messageConsumer = session.createConsumer(receiveDestination);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
//starts the conn to listen the msg
    public void setMessageListener(MessageListener messageListener){
        try {
            messageConsumer.setMessageListener(messageListener);
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
