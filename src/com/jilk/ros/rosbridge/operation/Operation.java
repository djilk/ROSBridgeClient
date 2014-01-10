/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jilk.ros.rosbridge.operation;

import com.jilk.ros.message.Message;
import com.jilk.ros.message.MessageType;
import com.jilk.ros.rosbridge.implementation.JSON;


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
    
    Operation() {
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
    
    public static Operation toOperation(String json) {
        return ((Wrapper) JSON.toMessage(json, Wrapper.class)).msg;
    }
    
    // Operation subclasses are registered directly rather than through the
    //     passive static initializer approach, because they can be at the root
    //     of an inbound message and otherwise we wouldn't find them.
    public static void initialize() {
        if (!initialized) {
            Message.register(Advertise.class);
            Message.register(Authenticate.class);
            Message.register(CallService.class);
            Message.register(Fragment.class);
            Message.register(Operation.class);
            Message.register(PNG.class);
            Message.register(Publish.class);
            Message.register(ServiceResponse.class);
            Message.register(SetStatusLevel.class);
            Message.register(Status.class);
            Message.register(Subscribe.class);
            Message.register(Unadvertise.class);
            Message.register(Unsubscribe.class);
            Message.register(Wrapper.class);
            initialized = true;
        }
    }    
}
