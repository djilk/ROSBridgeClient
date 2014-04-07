/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros;

import com.jilk.ros.message.Message;
import com.jilk.ros.rosapi.message.TypeDef;
import com.jilk.ros.rosbridge.operation.Operation;
import com.jilk.ros.rosbridge.ROSBridgeClient;

/**
 *
 * @author David J. Jilk
 */
public abstract class ROSClient {

    public ROSClient() {}
    
    public static ROSClient create(String uriString) {
        // if we ever implement other ROSClient types, we'll key off the URI protocol (e.g., ws://)
        // we'd also have to abstract out Topic and Service since they depend on the ROSBridge operations
        return new ROSBridgeClient(uriString);
    }
    
    public abstract boolean connect();
    public abstract void disconnect();
    public abstract void send(Operation operation);
    public abstract void register(Class<? extends Operation> c,
            String s,
            Class<? extends Message> m,
            MessageHandler h);
    public abstract void unregister(Class<? extends Operation> c, String s);
    public abstract void setDebug(boolean debug);
    public abstract String[] getNodes() throws InterruptedException;
    public abstract String[] getTopics() throws InterruptedException;
    public abstract String[] getServices() throws InterruptedException;
    public abstract TypeDef getTopicMessageDetails(String topic) throws InterruptedException;
    public abstract TypeDef getServiceRequestDetails(String service) throws InterruptedException;
    public abstract TypeDef getServiceResponseDetails(String service) throws InterruptedException;
    public abstract TypeDef getTypeDetails(String type) throws InterruptedException;
    public abstract void typeMatch(TypeDef t, Class<? extends Message> c) throws InterruptedException;    
}
