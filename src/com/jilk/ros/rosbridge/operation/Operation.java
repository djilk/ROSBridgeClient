/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.operation;

import com.jilk.ros.message.Message;
import com.jilk.ros.message.MessageType;
import com.jilk.ros.rosbridge.implementation.JSON;
import com.jilk.ros.rosbridge.implementation.Registry;


/**
 *
 * @author David J. Jilk
 */
@MessageType(string = "operation")
public class Operation extends Message {
    private static boolean initialized = false;
    private static Long uid = 0L;

    public String op;
    public String id;
    
    public Operation() {
        this.op = getMessageType(getClass());
        this.id = nextId();
    }
    
    private static synchronized String nextId() {
        String result = uid.toString();
        uid++;
        return result;
    }
    
    public String toJSON() {
        return JSON.toJSON(this);
    }
    
    public static Operation toOperation(String json, Registry<Class> registry) {
        return ((Wrapper) JSON.toMessage(json, Wrapper.class, registry)).msg;
    }
    
    public static void initialize(Registry<Class> registry) {
        if (!initialized) {
            initClass(registry, Advertise.class);
            initClass(registry, Authenticate.class);
            initClass(registry, CallService.class);
            initClass(registry, Fragment.class);
            initClass(registry, Operation.class);
            initClass(registry, PNG.class);
            initClass(registry, Publish.class);
            initClass(registry, ServiceResponse.class);
            initClass(registry, SetStatusLevel.class);
            initClass(registry, Status.class);
            initClass(registry, Subscribe.class);
            initClass(registry, Unadvertise.class);
            initClass(registry, Unsubscribe.class);
            initClass(registry, Wrapper.class);
            
            registry.register(Wrapper.class, Message.getMessageType(Publish.class), Publish.class);
            registry.register(Wrapper.class, Message.getMessageType(CallService.class), CallService.class);
            registry.register(Wrapper.class, Message.getMessageType(ServiceResponse.class), ServiceResponse.class);
            initialized = true;
        }
    }    
    
    private static void initClass(Registry<Class> registry, Class<? extends Message> c) {
        registry.register(Message.class, Message.getMessageType(c), c);
    }
}
