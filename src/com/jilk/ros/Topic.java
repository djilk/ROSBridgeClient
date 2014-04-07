/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros;

import java.util.concurrent.LinkedBlockingQueue;

import com.jilk.ros.message.Message;
import com.jilk.ros.rosbridge.operation.*;
import com.jilk.ros.rosbridge.FullMessageHandler;


/**
 *
 * @author David J. Jilk
 */
public class Topic<T extends Message> extends LinkedBlockingQueue<T> implements FullMessageHandler {
    protected String topic;
    private Class<? extends T> type;
    private String messageType;
    private ROSClient client;
    private Thread handlerThread;
    
    public Topic(String topic, Class<? extends T> type, ROSClient client) {
        this.topic = topic;
        this.client = client;
        this.type = type;
        messageType = Message.getMessageType(type);
        handlerThread = null;
    }
    
    @Override
    public void onMessage(String id, Message message) {
        add((T) message);
    }
    
    
    // warning: there is a delay between the completion of this method and 
    //          the completion of the subscription; it takes longer than
    //          publishing multiple other messages, for example.    
    public void subscribe(MessageHandler<T> handler) {
        startRunner(handler);
        subscribe();
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
        stopRunner();
    }
    
    private void startRunner(MessageHandler<T> handler) {
        stopRunner();
        handlerThread = new Thread(new MessageRunner(handler));
        handlerThread.setName("Message handler for " + topic);
        handlerThread.start();
    }
    
    private void stopRunner() {
        if (handlerThread != null) {
            handlerThread.interrupt();
            clear();
            handlerThread = null;
        }
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
    
    private class MessageRunner implements Runnable {
        private MessageHandler<T> handler;

        public MessageRunner(MessageHandler<T> handler) {
            this.handler = handler;
        }             
        
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    handler.onMessage(take());
                }
                catch (InterruptedException ex) {
                    break;
                }
            }
        }
    }
    
}
