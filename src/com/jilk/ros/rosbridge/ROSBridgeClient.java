/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge;

import com.jilk.ros.message.Message;
import com.jilk.ros.rosbridge.implementation.*;
import com.jilk.ros.rosbridge.operation.*;


/**
 *
 * @author David J. Jilk
 */
public class ROSBridgeClient {
    String uriString;
    ROSBridgeWebSocketClient client;
    
    public ROSBridgeClient(String uriString) {
        Operation.initialize();
        this.uriString = uriString;
    }
    
    public boolean connect() {
        boolean result = false;
        client = ROSBridgeWebSocketClient.create(uriString);
        if (client != null) {
            try {
                result = client.connectBlocking();
            }
            catch (InterruptedException ex) {}
        }
        return result;
    }
    
    public void disconnect() {
        try {
            client.closeBlocking();
        }
        catch (InterruptedException ex) {}
    }
    
    public void subscribe(String topic, MessageHandler handler, Class type) {
        Registry.registerTopic(topic, type);
        Registry.registerHandler(topic, handler);
        Subscribe s = new Subscribe(topic, Message.getMessageType(type));
        client.send(s);
    }
    
    public void unsubscribe(String topic) {
        // probably need to have an "unregister" mechanism
        // also need to handle race conditions in incoming message handler
        //    so that once unsubscribe has happened the handler gets no more
        //    messages
        Unsubscribe u = new Unsubscribe(topic);
        client.send(u);
    }
    
    public void call(String service, Message data, MessageHandler handler, Class type) {
        Registry.registerServiceResults(service, type);
        Registry.registerHandler(service, handler);
        CallService c = new CallService(service, data);        
        client.send(c);
    }
    
    public void advertise(String topic, Class type) {
        Advertise a = new Advertise(topic, Message.getMessageType(type));
        client.send(a);
    }
    
    public void publish(String topic, Message data) {
        Publish p = new Publish(topic, data);
        client.send(p);
    }
    
    public void unadvertise(String topic) {     
        Unadvertise u = new Unadvertise(topic);
        client.send(u);
    }
}
