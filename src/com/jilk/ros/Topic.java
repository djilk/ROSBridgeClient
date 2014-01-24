/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros;

import java.util.concurrent.LinkedBlockingQueue;

import com.jilk.ros.message.Message;
import com.jilk.ros.rosbridge.MessageHandler;
import com.jilk.ros.rosbridge.ROSBridgeClient;
import com.jilk.ros.rosbridge.operation.*;


/**
 *
 * @author David J. Jilk
 */
public class Topic<T extends Message> extends LinkedBlockingQueue<T> implements MessageHandler {
    private String topic;
    private Class<? extends T> type;
    private String messageType;
    private MessageHandler<T> handler;
    private ROSBridgeClient client;
    
    public Topic(String topic, Class<? extends T> type, ROSBridgeClient client) {
        this.topic = topic;
        this.client = client;
        this.type = type;
        messageType = Message.getMessageType(type);
    }
    
    @Override
    public void onMessage(String id, Message message) {
        if (handler != null)
            handler.onMessage(id, (T) message);
        else add((T) message);
    }
    
    public void setMessageHandler(MessageHandler<T> handler) {
        this.handler = handler;
    }
    
    public void subscribe() {
        client.register(Publish.class, topic, type, this);
        send(new Subscribe(topic, messageType));
    }
    
    public void unsubscribe() {
        // need to handle race conditions in incoming message handler
        //    so that once unsubscribe has happened the handler gets no more
        //    messages
        send(new Unsubscribe(topic));        
        client.unregister(Publish.class, topic);
    }
    
    public void advertise() {
        send(new Advertise(topic, messageType));
    }
    
    public void publish(T message) {
        send(new Publish(topic, message));
    }
    
    public void unadvertise() {
        send(new Unadvertise(topic));
    }
    
    private void send(Operation operation) {
        client.send(operation);
    }
    
    public void verify() throws InterruptedException {

        boolean hasTopic = false;
        for (String s : client.getTopics()) {
            if (s.equals(topic)) {
                hasTopic = true;
                break;
            }
        }
        if (!hasTopic)
            throw new RuntimeException("Topic \'" + topic + "\' not available.");
        
        client.typeMatch(client.getTopicMessageDetails(topic), type);
    }
    
}
