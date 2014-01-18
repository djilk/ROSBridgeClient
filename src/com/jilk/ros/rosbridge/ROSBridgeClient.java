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
    
    public void send(Operation operation) {
        client.send(operation);
    }
    
    public void register(Class<? extends Operation> c,
            String s,
            Class<? extends Message> m,
            MessageHandler h) {
        client.register(c, s, m, h);
    }

    public void unregister(Class<? extends Operation> c, String s) {
        client.unregister(c, s);
    }
    
}
