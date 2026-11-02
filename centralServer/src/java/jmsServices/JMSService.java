/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmsServices;


import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 *
 * @author Jovan
 */

@Stateless
public class JMSService {
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup = "QueueInSubsystem1")
    private Queue queueIn1;
    
    @Resource(lookup = "QueueOutSubsystem1")
    private Queue queueOut1;
    
    @Resource(lookup = "QueueInSubsystem2")
    private Queue queueIn2;
    
    @Resource(lookup = "QueueOutSubsystem2")
    private Queue queueOut2;
    
    @Resource(lookup = "QueueInSubsystem3")
    private Queue queueIn3;
    
    @Resource(lookup = "QueueOutSubsystem3")
    private Queue queueOut3;
    
    

    // -------------------Slanje i primanje poruka-------------------
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String sendAndReceiveSubsystem1(RequestMessage request){
        try (JMSContext context = connectionFactory.createContext()){
            JMSProducer producer = context.createProducer();
            TextMessage message = context.createTextMessage(request.getXmlArguments());
            message.setStringProperty("OPERATION", request.getOperation());
            producer.send(queueIn1, message);
            
            String msgId = message.getJMSMessageID();
            String selector = "JMSCorrelationID = '" + msgId + "'";
            
            try(JMSConsumer consumer = context.createConsumer(queueOut1, selector)){
                TextMessage reply = (TextMessage) consumer.receive(20000);
                if(reply == null) return "<error>Timeout</error>";
                return reply.getText();
            }
            
            
        } catch (JMSException ex){
            Logger.getLogger(JMSService.class.getName()).log(Level.SEVERE, null, ex);
            return "<error>ERROR: BAD JMS MESSAGE</error>";
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String sendAndReceiveSubsystem2(RequestMessage request){
        try (JMSContext context = connectionFactory.createContext()){
            JMSProducer producer = context.createProducer();
            TextMessage message = context.createTextMessage(request.getXmlArguments());
            message.setStringProperty("OPERATION", request.getOperation());
            producer.send(queueIn2, message);
            
            String msgId = message.getJMSMessageID();
            String selector = "JMSCorrelationID = '" + msgId + "'";
            
            try(JMSConsumer consumer = context.createConsumer(queueOut2, selector)){
                TextMessage reply = (TextMessage) consumer.receive(20000);
                if(reply == null) return "<error>Timeout</error>";
                return reply.getText();
            }
            
            
        } catch (JMSException ex){
            Logger.getLogger(JMSService.class.getName()).log(Level.SEVERE, null, ex);
            return "<error>ERROR: BAD JMS MESSAGE</error>";
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String sendAndReceiveSubsystem3(RequestMessage request){
        try (JMSContext context = connectionFactory.createContext()){
            JMSProducer producer = context.createProducer();
            TextMessage message = context.createTextMessage(request.getXmlArguments());
            message.setStringProperty("OPERATION", request.getOperation());
            producer.send(queueIn3, message);
            
            String msgId = message.getJMSMessageID();
            String selector = "JMSCorrelationID = '" + msgId + "'";
            
            try(JMSConsumer consumer = context.createConsumer(queueOut3, selector)){
                TextMessage reply = (TextMessage) consumer.receive(20000);
                if(reply == null) return "<error>Timeout</error>";
                return reply.getText();
            }
            
            
        } catch (JMSException ex){
            Logger.getLogger(JMSService.class.getName()).log(Level.SEVERE, null, ex);
            return "<error>ERROR: BAD JMS MESSAGE</error>";
        }
    }
}

/*@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String sendAndReceiveSubsystem2(RequestMessage request){
        try (JMSContext context = connectionFactory.createContext();
                JMSConsumer consumer = context.createConsumer(queueOut2)){
            JMSProducer producer = context.createProducer();
            TextMessage message = context.createTextMessage(request.getXmlArguments());
            message.setStringProperty("OPERATION", request.getOperation());
            producer.send(queueIn2, message);
            
            TextMessage reply = (TextMessage) consumer.receive(20000);
            return reply.getText();
        } catch (JMSException ex){
            Logger.getLogger(JMSService.class.getName()).log(Level.SEVERE, null, ex);
            return "<error>ERROR: BAD JMS MESSAGE</error>";
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String sendAndReceiveSubsystem3(RequestMessage request){
        try (JMSContext context = connectionFactory.createContext();
                JMSConsumer consumer = context.createConsumer(queueOut3)){
            JMSProducer producer = context.createProducer();
            TextMessage message = context.createTextMessage(request.getXmlArguments());
            message.setStringProperty("OPERATION", request.getOperation());
            producer.send(queueIn3, message);
            
            TextMessage reply = (TextMessage) consumer.receive(20000);
            return reply.getText();
        } catch (JMSException ex){
            Logger.getLogger(JMSService.class.getName()).log(Level.SEVERE, null, ex);
            return "<error>ERROR: BAD JMS MESSAGE</error>";
        }
    }*/
/*@Stateless
public class JMSService {
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup = "QueueInSubsystem1")
    private Queue queueIn1;
    
    @Resource(lookup = "QueueOutSubsystem1")
    private Queue queueOut1;
    
    @Resource(lookup = "QueueInSubsystem2")
    private Queue queueIn2;
    
    @Resource(lookup = "QueueOutSubsystem2")
    private Queue queueOut2;
    
    @Resource(lookup = "QueueInSubsystem3")
    private Queue queueIn3;
    
    @Resource(lookup = "QueueOutSubsystem3")
    private Queue queueOut3;
    
    private JMSContext context;
    
    private JMSConsumer consumer1;
    private JMSProducer producer1;
    
    private JMSConsumer consumer2;
    private JMSProducer producer2;
    
    private JMSConsumer consumer3;
    private JMSProducer producer3;
    
    @PostConstruct
    public void init() {
        context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE);
        producer1 = context.createProducer();
        consumer1 = context.createConsumer(queueOut1);
        producer2 = context.createProducer();
        consumer2 = context.createConsumer(queueOut2);
        producer3 = context.createProducer();
        consumer3 = context.createConsumer(queueOut3);
        
    }
    
    @PreDestroy
    public void cleanup() {
        if (context != null) {
            context.close();
        }
    }

    // -------------------Slanje poruka-------------------
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String sendMessageSubsystem1(RequestMessage request){
        try {
            TextMessage message = context.createTextMessage(request.getXmlArguments());
            message.setStringProperty("OPERATION", request.getOperation());
            producer1.send(queueIn1, message);
            return message.getJMSMessageID();
        } catch (JMSException ex) {
            Logger.getLogger(JMSService.class.getName()).log(Level.SEVERE, null, ex);
            return "<error>ERROR: BAD JMS MESSAGE</error>";
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String sendMessageSubsystem2(RequestMessage request){
        try {
            TextMessage message = context.createTextMessage(request.getXmlArguments());
            message.setStringProperty("OPERATION", request.getOperation());
            producer2.send(queueIn2, message);
            return message.getJMSMessageID();
        } catch (JMSException ex) {
            Logger.getLogger(JMSService.class.getName()).log(Level.SEVERE, null, ex);
            return "<error>ERROR: BAD JMS MESSAGE</error>";
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String sendMessageSubsystem3(RequestMessage request){
        try {
            TextMessage message = context.createTextMessage(request.getXmlArguments());
            message.setStringProperty("OPERATION", request.getOperation());
            producer3.send(queueIn3, message);
            return message.getJMSMessageID();
        } catch (JMSException ex) {
            Logger.getLogger(JMSService.class.getName()).log(Level.SEVERE, null, ex);
            return "<error>ERROR: BAD JMS MESSAGE</error>";
        }
    }
    // -------------------Primanje poruka-------------------
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String receiveMessageSubsystem1(String sentID){
        try {
            String corellationID = "JMSCorrelationID = '" + sentID + "'";
            JMSConsumer consumer1 = context.createConsumer(queueOut1, corellationID);
            try{
                Message reply = consumer1.receive(5000); // timeout 5s
                if (reply == null) {
                    return "<error>Timeout - no response from subsystem1</error>";
                }
                if (reply instanceof TextMessage) {
                    return ((TextMessage) reply).getText();
                } else {
                    return "<error>Expected TextMessage, got " + reply.getClass().getName() + "</error>";
                }
            } finally {
                consumer1.close();
            }
            
        } catch (JMSException ex) {
            Logger.getLogger(JMSService.class.getName()).log(Level.SEVERE, null, ex);
            return "<error>ERROR: BAD JMS MESSAGE</error>";
        }
    }
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String receiveMessageSubsystem2(String sentID){
        try {
            String corellationID = "JMSCorrelationID = '" + sentID + "'";
            JMSConsumer consumer2 = context.createConsumer(queueOut1, corellationID);
            try{
                TextMessage reply = (TextMessage) consumer2.receive();
                return reply.getText();
            } finally {
                consumer2.close();
            }
        } catch (JMSException ex) {
            Logger.getLogger(JMSService.class.getName()).log(Level.SEVERE, null, ex);
            return "<error>ERROR: BAD JMS MESSAGE</error>";
        }
    }
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String receiveMessageSubsystem3(String sentID){
        try {
            String corellationID = "JMSCorrelationID = '" + sentID + "'";
            JMSConsumer consumer3 = context.createConsumer(queueOut1, corellationID);
            try{
                TextMessage reply = (TextMessage) consumer2.receive();
                return reply.getText();
            } finally {
                consumer3.close();
            }
        } catch (JMSException ex) {
            Logger.getLogger(JMSService.class.getName()).log(Level.SEVERE, null, ex);
            return "<error>ERROR: BAD JMS MESSAGE</error>";
        }
    }

    // ---------------Posalji i prihvati odgovor----------
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String sendAndReceiveSubsystem1(RequestMessage request){
        String sentID = sendMessageSubsystem1(request);
        return receiveMessageSubsystem1(sentID);
    }
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String sendAndReceiveSubsystem2(RequestMessage request){
        String sentID = sendMessageSubsystem2(request);
        return receiveMessageSubsystem2(sentID);
    }
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String sendAndReceiveSubsystem3(RequestMessage request){
        String sentID = sendMessageSubsystem3(request);
        return receiveMessageSubsystem3(sentID);
    }

    public void close() {
        if (context != null) {
            context.close();
        }
    }
}*/