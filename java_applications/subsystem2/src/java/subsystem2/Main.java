/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subsystem2;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Jovan
 */
public class Main {
    public static void main(String[] args) {
        try {
            InitialContext initialContext = new InitialContext();
            ConnectionFactory connectionFactory =
                    (ConnectionFactory) initialContext.lookup("jms/__defaultConnectionFactory");

            Queue queueIn = (Queue) initialContext.lookup("QueueInSubsystem2");
            Queue queueOut = (Queue) initialContext.lookup("QueueOutSubsystem2");

            JMSContext context = connectionFactory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queueIn);

            System.out.println("Subsystem2 started!");

            while (true) {
                Message msg = consumer.receive();
                if (msg instanceof TextMessage) {
                    TextMessage textMsg = (TextMessage) msg;
                    String operation = textMsg.getStringProperty("OPERATION");
                    String xmlPayload = textMsg.getText();

                    String responseXml = CommandManager.doCommand(operation, xmlPayload);

                    TextMessage replyMessage = context.createTextMessage(responseXml);
                    replyMessage.setStringProperty("OPERATION", operation);
                    
                    replyMessage.setJMSCorrelationID(textMsg.getJMSMessageID());
                    
                    producer.send(queueOut, replyMessage);
                } else {
                    System.out.println("Received non-text JMS message in subsystem2, ignoring: " + msg);
                }
            }
        } catch (JMSException | NamingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
