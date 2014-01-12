/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros;

import java.util.concurrent.LinkedBlockingQueue;

import com.jilk.ros.message.Message;
import com.jilk.ros.rosbridge.implementation.Registry;
import com.jilk.ros.rosbridge.MessageHandler;
import com.jilk.ros.rosbridge.ROSBridgeClient;
import com.jilk.ros.rosbridge.operation.*;

/**
 *
 * @author David J. Jilk
 */
public class Topic<T extends Message> extends LinkedBlockingQueue<T> implements MessageHandler {
    private String topic;
    private String type;
    private MessageHandler<T> handler;
    private ROSBridgeClient client;
    
    public Topic(String topic, Class<T> type, ROSBridgeClient client) {
        this.topic = topic;
        this.type = Message.getMessageType(type);
        this.client = client;
        Registry.registerTopic(topic, type);        
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
        Registry.registerHandler(topic, this);
        send(new Subscribe(topic, type));
    }
    
    public void unsubscribe() {
        // probably need to have an "unregister" mechanism
        // also need to handle race conditions in incoming message handler
        //    so that once unsubscribe has happened the handler gets no more
        //    messages
        send(new Unsubscribe(topic));        
    }
    
    public void advertise() {
        send(new Advertise(topic, type));
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
}
